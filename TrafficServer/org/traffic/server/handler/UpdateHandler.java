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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernatespatial.criterion.DWithinExpression;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.traffic.database.Database;
import org.traffic.database.LocalDistanceOrder;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Congestion;
import org.traffic.models.traffic.RoadStrip;
import org.traffic.models.traffic.UserData;
import org.traffic.server.data.Request;
import org.traffic.server.data.Response;
import org.traffic.utils.GeomHelper;
import org.traffic.utils.SocketCommunicator;

import com.vividsolutions.jts.geom.Point;

/**
 * Class to handle Update-Requests. It loads the speed information for the
 * current road of the client and sends it back. If the client requests more
 * detailed information the crossing roads are also loaded.
 * <p>
 * In addition to the speed information the {@link Response} contains all
 * instances of {@link Congestion} near the position of the client and checks
 * whether the client has an active road that has changed since the last
 * request.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 236 $
 */
public class UpdateHandler extends RequestHandler {

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public UpdateHandler(Socket socket) {
		super(socket);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(Request r) {

		// check if the client has a valid identification
		if (r.getClient() == null) {
			SocketCommunicator.writeOutput(getSocket(),
					"{'error': 'no valid identification'}");
			Log.e("UpdateHandler",
					"Error@handleRequest:no valid identification");
			return;
		}
		Session s = Database.session();
		s.beginTransaction();

		// getting the data of the client
		if (r.getData().containsKey("lat") && r.getData().containsKey("lon")
				&& r.getData().containsKey("time")
				&& r.getData().containsKey("speed")) {
			int bboxType = Integer.valueOf(r.getData().getString("bbox"));
			double lat = r.getData().getDouble("lat");
			double lon = r.getData().getDouble("lon");
			long time = r.getData().getLong("time");
			double speed = r.getData().getDouble("speed");
			boolean save = true;
			if (r.getData().containsKey("save")) {
				save = r.getData().getBoolean("save");
			}
			String hash = r.getID();
			Point p = GeomHelper.createPoint(lon, lat);
			UserData d = new UserData(new Date(time), p, speed, hash);

			// building the response
			Response res = new Response();
			RoadStrip rs = (RoadStrip) s.createCriteria(RoadStrip.class)
					.add(new DWithinExpression("way", p, 8.2E-4))
					.addOrder(LocalDistanceOrder.asc("distance", p))
					.setMaxResults(1).uniqueResult();

			if (rs != null) {
				List<UserData> prevData = (List<UserData>) s
						.createCriteria(UserData.class)
						.add(Restrictions.eq("connectionhash", r.getID()))
						.add(Restrictions.lt("time", new Date(time)))
						.add(Restrictions.isNotNull("road_id"))
						.addOrder(Order.desc("time")).list();

				// getting the driving direction
				boolean driveDirection = false;
				if (prevData.size() > 0) {
					UserData prev = prevData.get(0);
					if (prev.getRoad_id() == rs.getId()) {
						driveDirection = (prev.getPosition().distance(
								rs.getWay().getStartPoint()) < d.getPosition()
								.distance(rs.getWay().getStartPoint()));
					} else {
						driveDirection = (prev.getPosition().distance(
								rs.getWay().getStartPoint()) > prev
								.getPosition().distance(
										rs.getWay().getEndPoint()));
					}
				}

				// adding the current road to the response
				d.setRoad_id(rs.getId());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", rs.getId());
				int max = (rs.getRoad().getMaxspeed() != null) ? rs.getRoad()
						.getMaxspeed() : 0;
				map.put("maxspeed", max);
				map.put("speed", rs.getBestSpeed(driveDirection).getSpeed());
				map.put("quality", rs.getBestSpeed(driveDirection)
						.getCategory());
				res.append(map, "traffic");

				// surrounding congestions of the current road
				List<Congestion> congestions = new LinkedList<Congestion>();
				congestions.addAll(rs.getCongestions());

				if (bboxType != 1) {
					// adding all known neighbours to the response
					Criteria crit = s.createCriteria(RoadStrip.class).add(
							Restrictions.ne("id", rs.getId()));
					if (bboxType == 2) {
						crit.add(SpatialRestrictions.intersects("way",
								rs.getWay()));
					} else {
						crit.add(SpatialRestrictions.intersects("way",
								GeomHelper.createRectangle(p, 2000)));
					}

					List<RoadStrip> neighbours = (List<RoadStrip>) crit.list();
					// adding the neighbors to the output
					for (RoadStrip neighbour : neighbours) {
						boolean direction = neighbour.getWay().getStartPoint()
								.intersects(rs.getWay());
						map = new HashMap<String, Object>();
						map.put("id", neighbour.getId());
						max = (neighbour.getRoad().getMaxspeed() != null) ? neighbour
								.getRoad().getMaxspeed() : 0;
						map.put("maxspeed", max);
						map.put("speed", neighbour.getBestSpeed(direction)
								.getSpeed());
						map.put("quality", neighbour.getBestSpeed(direction)
								.getCategory());
						res.append(map, "traffic");

						// congestions of the neighbor
						congestions.addAll(neighbour.getCongestions());
					}
				}
				
				// sending congestions to the client
				for (Congestion c : congestions) {
					map = new HashMap<String, Object>();
					map.put("lon", c.getPosition().getX());
					map.put("lat", c.getPosition().getY());
					map.put("type", c.getType());
					map.put("time", c.getReportingtime().getTime());
					map.put("id", c.getId());
					res.append(map, "congestions");
				}
			}

			// check if a route is active and has changed since the last request
			if (r.getClient().getRoute() != null
					&& r.getClient().getRoute().isUpdated()) {
				res.append(true, "routing");
			}

			// send, clear and save
			SocketCommunicator.writeOutput(getSocket(), res.getData());
			if (save) {
				s.clear();
				s.save(d);
				Database.end(true);
			} else {
				Database.end(false);
			}
		} else {
			SocketCommunicator.writeOutput(getSocket(),
					"{error: 'missing arguments'}");
		}
	}

}
