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

import org.hibernate.Session;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Route;
import org.traffic.server.data.Request;

/**
 * Class to handle Delete-Route-Requests. The handler searches for an active
 * {@link Route} of the client and deletes it if it's not null.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 148 $
 */
public class DeleteRouteHandler extends RequestHandler {

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public DeleteRouteHandler(Socket socket) {
		super(socket);
	}

	@Override
	public void handleRequest(Request r) {
		
		// check if the client has a valid identification
		if (r.getClient() == null) {
			Log.e("DeleteRouteHandler",
					"Error@handleRequest:no valid identification");
			return;
		}
		if (r.getClient().getRoute() != null) {
			Session s = Database.session();
			s.beginTransaction();

			// deleting the route if present
			s.update(r.getClient());
			if (r.getClient().getRoute() != null) {
				s.delete(r.getClient().getRoute());
			}
			r.getClient().setRoute(null);
			s.saveOrUpdate(r.getClient());

			Database.end(true);
		}
	}

}
