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
import org.traffic.server.data.Request;

/**
 * Class to handle Acknowledge-ID-Requests. The handler marks the client as
 * active.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class AcknowledgeHandler extends RequestHandler {

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public AcknowledgeHandler(Socket socket) {
		super(socket);
	}

	@Override
	public void handleRequest(Request r) {

		// save acknowledgement
		Session s = Database.session();
		s.beginTransaction();
		r.getClient().setAck(true);
		s.update(r.getClient());
		Database.end(true);
	}

}
