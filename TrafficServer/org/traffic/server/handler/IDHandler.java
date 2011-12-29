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
import java.security.MessageDigest;
import java.util.Date;

import org.hibernate.Session;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Client;
import org.traffic.server.data.Request;
import org.traffic.server.data.Response;
import org.traffic.utils.SocketCommunicator;

/**
 * Class to handle ID-Requests. The client sends this request, if it has no
 * valid ID or if the stored one has expired.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class IDHandler extends RequestHandler {

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public IDHandler(Socket socket) {
		super(socket);
	}

	@Override
	public void handleRequest(Request r) {
		try {
			if (r.getData().containsKey("device")) {

				// creating md5-hash from device-id and current timestamp
				String deviceID = r.getData().getString("device");
				deviceID += System.currentTimeMillis();
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.reset();
				md5.update(deviceID.getBytes());
				byte[] result = md5.digest();

				StringBuffer hexString = new StringBuffer();
				for (int i = 0; i < result.length; i++) {
					hexString.append(Integer.toHexString(0xFF & result[i]));
				}
				// setting lease-time to 24 hours
				long lease = System.currentTimeMillis() + 86399000;

				Response res = new Response();
				res.set(hexString.toString(), "id");
				res.set(lease, "lease");
				
				// saving the new client
				Client c = new Client(hexString.toString(), new Date(lease));
				Session s = Database.session();
				s.beginTransaction();
				s.clear();
				s.save(c);
				Database.end(true);
				
				SocketCommunicator.writeOutput(getSocket(),res.getData());
			} else {
				throw new IllegalArgumentException("device id not found");
			}
		} catch (IllegalArgumentException ex) {
			SocketCommunicator.writeOutput(getSocket(),
					"{'error': '" + ex.getMessage() + "'}");
			Log.e("IDHandler",
					ex.getClass() + "@handleRequest: " + ex.getMessage());
		} catch (Exception ex) {
			Log.e("IDHandler",
					ex.getClass() + "@handleRequest: " + ex.getMessage());
		}
	}

}
