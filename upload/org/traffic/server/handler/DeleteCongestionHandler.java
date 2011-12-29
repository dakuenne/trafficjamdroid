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
import org.hibernate.criterion.Restrictions;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Congestion;
import org.traffic.server.data.Request;

/**
 * Class to handle Delete-Congestion-Requests. The handler searches for a
 * congestion identified by the provided ID and deletes it.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class DeleteCongestionHandler extends RequestHandler {

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public DeleteCongestionHandler(Socket socket) {
		super(socket);
	}

	@Override
	public void handleRequest(Request r) {
		// check if the client has a valid identification
		if (r.getClient() == null) {
			Log.e("DeleteCongestionHandler",
					"Error@handleRequest:no valid identification");
			return;
		}
		try {
			if (r.getData().containsKey("id")) {

				// deletes the congestion identified by the given id
				int id = r.getData().getInt("id");
				Session s = Database.session();
				s.beginTransaction();
				Congestion c = (Congestion) s.createCriteria(Congestion.class)
						.add(Restrictions.eq("id", id)).setMaxResults(1)
						.uniqueResult();
				s.clear();
				s.delete(c);
				Database.end(true);
			}
		} catch (Exception e) {
			Log.e("DeleteCongestionHandler", e.getClass() + "@handleRequest: "
					+ e.getMessage());
		}
	}

}
