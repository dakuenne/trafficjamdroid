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
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Route;
import org.traffic.server.data.Request;
import org.traffic.server.data.Response;
import org.traffic.utils.SocketCommunicator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

/**
 * Class to handle Refresh-Route-Requests. It checks if the client has an active
 * {@link Route}, loads and returns it.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class RefreshRouteHandler extends RequestHandler {

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public RefreshRouteHandler(Socket socket) {
		super(socket);
	}

	@Override
	public void handleRequest(Request r) {

		// check if the client has a valid identification
		if (r.getClient() == null) {
			SocketCommunicator.writeOutput(getSocket(),
					"{'error': 'no valid identification'}");
			Log.e("RefreshRouteHandler",
					"Error@handleRequest:no valid identification");
			return;
		} else {

			// if the client has an active route -> send it
			if (r.getClient().getRoute() != null) {
				Session s = Database.session();
				s.beginTransaction();
				Response res = new Response();
				LineString route = r.getClient().getRoute().getRoute();
				Coordinate[] c = route.getCoordinates();
				for (int i = 0; i < c.length; i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					Coordinate coord = c[i];
					map.put("lat", coord.y);
					map.put("lon", coord.x);
					res.append(map, "route");
				}
				SocketCommunicator.writeOutput(getSocket(), res.getData());
				r.getClient().getRoute().setUpdated(false);
				s.saveOrUpdate(r.getClient().getRoute());
				Database.end(true);
			} else {
				SocketCommunicator.writeOutput(getSocket(),
						"{error: 'nothing to route'}");
			}
		}
	}
}
