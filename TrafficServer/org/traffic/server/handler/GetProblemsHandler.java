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
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.hibernate.Session;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Problem;
import org.traffic.server.data.Request;
import org.traffic.server.data.Response;
import org.traffic.utils.SocketCommunicator;

/**
 * Class to handle Get-Problems-Requests. All identified problems are loaded
 * from the database and send to the requesting client.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 148 $
 */
public class GetProblemsHandler extends RequestHandler {

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public GetProblemsHandler(Socket socket) {
		super(socket);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleRequest(Request r) {

		// check if the client has a valid identification
		if (r.getClient() == null) {
			SocketCommunicator.writeOutput(getSocket(),
					"{'error': 'no valid identification'}");
			Log.e("GetProblemsHandler",
					"Error@handleRequest:no valid identification");
			return;
		}

		// getting all problems
		Session s = Database.session();
		s.beginTransaction();
		List<Problem> problems = (List<Problem>) s
				.createCriteria(Problem.class).list();

		// formatting as JSON
		Response res = new Response();
		for (Problem p : problems) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("description", p.getDescription());
			map.put("region", JSONArray.fromObject(p.getRegionJSON()));
			res.append(map, "problems");
		}

		SocketCommunicator.writeOutput(getSocket(), res.getData());
		Database.end(false);
	}

}
