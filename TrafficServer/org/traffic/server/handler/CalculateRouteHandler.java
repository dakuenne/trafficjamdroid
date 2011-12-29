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
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Session;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Congestion;
import org.traffic.models.traffic.RoadStrip;
import org.traffic.models.traffic.Route;
import org.traffic.server.data.Request;
import org.traffic.utils.GeomHelper;
import org.traffic.utils.IConstants;
import org.traffic.utils.SocketCommunicator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Class to handle Calculate-Route-Requests. The handler loads all jammed roads
 * and congestions from the database and sends a routing-request to CloudMade
 * with this information in the parameter <code>blockedRoads</code>. If
 * CloudMade could generate a route, this is stored in the database.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class CalculateRouteHandler extends RequestHandler {

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public CalculateRouteHandler(Socket socket) {
		super(socket);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(Request r) {
		// check if the client has a valid identification
		if (r.getClient() == null) {
			SocketCommunicator.writeOutput(getSocket(),
					"{'error': 'no valid identification'}");
			Log.e("CalculateRouteHandler",
					"Error@handleRequest:no valid identification");
			return;
		}

		try {
			if (r.getData().has("route")) {

				// checking if enough points are given
				if (!r.getData().getJSONArray("route").isArray()
						|| r.getData().getJSONArray("route").size() < 2) {
					SocketCommunicator.writeOutput(getSocket(),
							"{error: 'nothing to route'}");
					return;
				}

				// getting all points for the route
				JSONArray jRoute = r.getData().getJSONArray("route");
				List<Point> route = new LinkedList<Point>();
				for (int i = 0; i < jRoute.size(); i++) {
					JSONObject jPoint = jRoute.getJSONObject(i);
					route.add(GeomHelper.createPoint(jPoint.getDouble("lon"),
							jPoint.getDouble("lat")));
				}

				// calculating a bounding-box
				Point start = GeomHelper.createPoint(route.get(0)
						.getCoordinate().x, route.get(0).getCoordinate().y);
				Point end = GeomHelper.createPoint(route.get(route.size() - 1)
						.getCoordinate().x, route.get(route.size() - 1)
						.getCoordinate().y);
				Polygon bbox = GeomHelper.createRectangle(start.getX(),
						start.getY(), end.getX(), end.getY());

				// searching jammed roads
				Session s = Database.session();
				s.beginTransaction();
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
				query.append(IConstants.CM_NAVIGATION);
				query.append(start.getY() + "," + start.getX() + ",");
				if (route.size() > 2) {
					query.append("[");
					for (int i = 1; i < route.size() - 1; i++) {
						if (i > 1)
							query.append(",");
						Point transit = GeomHelper.createPoint(route.get(i)
								.getCoordinate().x, route.get(i)
								.getCoordinate().y);
						query.append(transit.getY() + "," + transit.getX());
					}
					query.append("],");
				}

				query.append(end.getY() + "," + end.getX());
				query.append("/car.js?tId=CloudMade");
				String cloudmade = query.toString();

				// adding the point from the blocked roadstrips to block it
				for (RoadStrip rs : l) {
					Coordinate[] cPoints = rs.getWay().getCoordinates();
					for (int i = 0; i < cPoints.length; i++) {
						Coordinate c = cPoints[i];
						query.append("&blockedRoad=" + c.y + "," + c.x);
					}
				}

				// adding the coordinate of all known congestions
				for (Congestion c : lc) {
					Point p = c.getPosition();
					query.append("&blockedRoad=" + p.getY() + "," + p.getX());
				}
				System.out.println(query.toString());

				// getting the JSON-response and parsing it
				String directionsJSON = SocketCommunicator.getContent(
						query.toString(), "UTF8");
				JSONObject jAnswer = JSONObject.fromObject(directionsJSON);
				JSONArray jRouting = jAnswer.getJSONArray("route_geometry");

				if (jRouting.size() == 0) {
					SocketCommunicator.writeOutput(getSocket(),
							"{error: 'nothing to route'}");
					return;
				}

				Point[] points = new Point[jRouting.size()];
				for (int i = 0; i < jRouting.size(); i++) {
					points[i] = GeomHelper.createPoint(jRouting.getJSONArray(i)
							.getDouble(1), jRouting.getJSONArray(i)
							.getDouble(0));
				}
				LineString routing = GeomHelper.createLineString(points);
				r.getClient()
						.setRoute(
								new Route(new Date(System.currentTimeMillis()),
										routing));
				r.getClient().getRoute().setUpdated(true);
				r.getClient().getRoute().setCloudmade(cloudmade);
				s.saveOrUpdate(r.getClient());
				Database.end(true);
				SocketCommunicator.writeOutput(getSocket(), "{status: 'done'}");
			}
		} catch (Exception e) {
			SocketCommunicator.writeOutput(getSocket(),
					"{error: 'critical server error'}");
			Log.e("CalculateRouteHandler", e.getClass() + "@handleRequest: "
					+ e.getMessage());
		}
	}

}
