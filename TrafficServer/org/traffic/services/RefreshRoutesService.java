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
package org.traffic.services;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Session;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Congestion;
import org.traffic.models.traffic.RoadStrip;
import org.traffic.models.traffic.Route;
import org.traffic.utils.GeomHelper;
import org.traffic.utils.SocketCommunicator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * This service checks whether a stored route must be updated due to a change in
 * the jammed streets or the congestions.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 231 $
 */
public class RefreshRoutesService extends TimedService {

	/**
	 * Custom-Constructor
	 * 
	 * @param time
	 *            Time to pause between the runs
	 */
	public RefreshRoutesService(long time) {
		super(time);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void serve() {
		Session s = Database.session();
		s.beginTransaction();

		// getting all routes
		List<Route> routes = (List<Route>) s.createCriteria(Route.class).list();

		for (Route r : routes) {
			Log.d("RefreshRoutesService", "Refreshing route " + r.getId());
			Point start = r.getRoute().getStartPoint();
			Point end = r.getRoute().getEndPoint();
			Polygon bbox = GeomHelper.createRectangle(start.getX(),
					start.getY(), end.getX(), end.getY());

			// searcing jammed roads
			List<RoadStrip> l = (List<RoadStrip>) s
					.createSQLQuery(
							"SELECT DISTINCT rs.* FROM data.roadstrips rs "
									+ "INNER JOIN data.roads r ON rs.road_id =r.id "
									+ "INNER JOIN data.speeds_to_start sts ON rs.id = sts.road_id "
									+ "WHERE r.maxspeed * 0.8 > sts.speed "
									+ "AND ST_Intersects(\"way\", GeometryFromText ( '"
									+ bbox.toString() + "', 4326 ))")
					.addEntity(RoadStrip.class).list();

			l.addAll((List<RoadStrip>) s
					.createSQLQuery(
							"SELECT DISTINCT rs.* FROM data.roadstrips rs "
									+ "INNER JOIN data.roads r ON rs.road_id =r.id "
									+ "INNER JOIN data.speeds_to_end sts ON rs.id = sts.road_id "
									+ "WHERE r.maxspeed * 0.8 > sts.speed "
									+ "AND ST_Intersects(\"way\", GeometryFromText ( '"
									+ bbox.toString() + "', 4326 ))")
					.addEntity(RoadStrip.class).list());

			// searching congestions
			List<Congestion> lc = (List<Congestion>) s.createCriteria(
					Congestion.class).list();

			// setting up the request for Cloudmade
			StringBuffer query = new StringBuffer();
			query.append(r.getCloudmade());
			
			// adding the coordinate of all known congestions
			for (Congestion c : lc) {
				Point p = c.getPosition();
				query.append("&blockedRoad=" + p.getY() + "," + p.getX());
			}

			// adding the point from the blocked roadstrips to block it
			for (RoadStrip rs : l) {
				Coordinate[] cPoints = rs.getWay().getCoordinates();
				for (int i = 0; i < cPoints.length; i++) {
					Coordinate c = cPoints[i];
					query.append("&blockedRoad=" + c.y + "," + c.x);
				}
			}

			// getting the JSON-response and parsing it
			try {
				String directionsJSON = SocketCommunicator.getContent(
						query.toString(), "UTF8");
				JSONObject jAnswer = JSONObject.fromObject(directionsJSON);
				JSONArray jRouting = jAnswer.getJSONArray("route_geometry");

				Point[] points = new Point[jRouting.size()];
				for (int i = 0; i < jRouting.size(); i++) {
					points[i] = GeomHelper.createPoint(jRouting.getJSONArray(i)
							.getDouble(1), jRouting.getJSONArray(i)
							.getDouble(0));
				}
				LineString routing = GeomHelper.createLineString(points);
				if (!r.getRoute().equals(routing)) {
					r.setRoute(routing);
					r.setUpdated(true);
					s.saveOrUpdate(r);
				}
				Log.d("RefreshRoutesService", "Route " + r.getId() + " refreshed");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Database.end(true);
	}
}
