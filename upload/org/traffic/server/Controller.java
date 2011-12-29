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
package org.traffic.server;

import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.traffic.logging.Log;
import org.traffic.server.data.Request;
import org.traffic.server.handler.RequestHandler;
import org.traffic.utils.SocketCommunicator;

/**
 * This class handles the incoming requests from the clients. The input is
 * parsed and the {@link RequestHandler} matching the type of the request is loaded.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class Controller extends Thread {

	/** Stores all registered handlers and the type of request they belong to */
	private static Map<Integer, Class<? extends RequestHandler>> handler = new HashMap<Integer, Class<? extends RequestHandler>>();

	/** The socket of the connected client */
	private Socket socket;

	/**
	 * Custom-Constructor of a <code>Controller</code>. One instance per request
	 * needed.
	 * 
	 * @param s
	 *            The socket of the connected client
	 */
	public Controller(Socket s) {
		socket = s;
	}

	@Override
	public void run() {
		try {
			// read the incoming JSON
			Request r = SocketCommunicator.read(socket);

			// searching for a matching handler
			Class<? extends RequestHandler> cl = handler.get(r.getType());
			if (cl != null) {
				Constructor<? extends RequestHandler> ct = cl
						.getConstructor(Socket.class);
				RequestHandler handle = ct.newInstance(socket);
				handle.handleRequest(r);
			} else {
				SocketCommunicator.writeOutput(socket,
						"{'error':'no matching RequestHandler found'}");
			}
		} catch (Exception e) {
			SocketCommunicator.writeOutput(socket,
					"{'error':'" + e.getMessage() + "'}");
			Log.e("Controller",
					e.getClass().getSimpleName() + "@run: " + e.getMessage());
		}
	}

	/**
	 * Registers a new <code>{@link RequestHandler}</code>. This handler could
	 * be used to handle a special request of the clients. Only one handler per
	 * type is valid.
	 * 
	 * @param requestType
	 *            The identifier of a request.
	 * @param handler
	 *            The handler for the selected request.
	 */
	public static void registerHandler(int requestType,
			Class<? extends RequestHandler> handler) {
		Log.i("Server", "new " + handler.getSimpleName() + " registerd");
		if (!Controller.handler.containsKey(requestType)) {
			Controller.handler.put(requestType, handler);
		}
	}
}
