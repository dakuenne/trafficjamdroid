/*
 * Copyright (c) 2011, Daniel Kuenne
 * 
 * This file is part of TrafficJamDroid.
 *
 * TrafficJamDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TrafficJamDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TrafficJamDroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.traffic.server.handler;

import java.net.Socket;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernatespatial.criterion.DWithinExpression;
import org.traffic.database.Database;
import org.traffic.database.LocalDistanceOrder;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Congestion;
import org.traffic.models.traffic.RoadStrip;
import org.traffic.server.data.Request;
import org.traffic.utils.GeomHelper;

import com.vividsolutions.jts.geom.Point;

/**
 * Class to handle Create-Congestion-Requests. The handler creates a
 * {@link Congestion} by the information, searches the nearest road, checks
 * whether the congestion is already in the database and stores it if not.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 234 $
 */
public class CongestionHandler extends RequestHandler {

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public CongestionHandler(Socket socket) {
		super(socket);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(Request r) {

		// check if the client has a valid identification
		if (r.getClient() == null) {
			Log.e("CongestionHandler",
					"Error@handleRequest:no valid identification");
			return;
		}

		try {
			if (r.getData().containsKey("lat")
					&& r.getData().containsKey("lon")
					&& r.getData().containsKey("time")
					&& r.getData().containsKey("type")) {

				// getting data
				double lat = r.getData().getDouble("lat");
				double lon = r.getData().getDouble("lon");
				long time = r.getData().getLong("time");
				int type = r.getData().getInt("type");
				Session s = Database.session();
				s.beginTransaction();

				// getting nearest road within 500 meters
				Point p = GeomHelper.createPoint(lon, lat);
				List<RoadStrip> lstRS = (List<RoadStrip>) s
						.createCriteria(RoadStrip.class)
						.add(new DWithinExpression("way", p, 0.0074))
						.addOrder(LocalDistanceOrder.asc("distance", p))
						.setMaxResults(1).list();

				if (lstRS.size() > 0) {
					RoadStrip rs = lstRS.get(0);

					// checking if congestion exists
					List<Congestion> l = (List<Congestion>) s
							.createCriteria(Congestion.class)
							.add(Restrictions.eq("type", type))
							.add(Restrictions.eq("roadstrip", rs)).list();
					if (l.size() == 0) {
						// saving the new congestion
						Congestion c = new Congestion(type,
								GeomHelper.createPoint(lon, lat),
								new Date(time));
						c.setRoadstrip(rs);
						s.clear();
						s.save(c);
						Database.end(true);
					} else {
						Database.end(false);
					}
				}
			}
		} catch (Exception e) {
			Log.e("CongestionHandler",
					e.getClass() + "@handleRequest: " + e.getMessage());
		}
	}

}
