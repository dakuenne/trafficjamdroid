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

import org.traffic.server.data.Request;

/**
 * Abstract superclass for all Request-Handler.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public abstract class RequestHandler {

	/** The {@link Socket} for the communication with the client */
	private Socket socket;

	/**
	 * Custom-Constructor with the {@link Socket} for the communication.
	 * 
	 * @param socket
	 *            {@link Socket}
	 */
	public RequestHandler(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Returns the socket for the communication.
	 * 
	 * @return The {@link Socket}
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * Abstract method, which must be implemented in the subclasses. It is used
	 * to handle the incoming {@link Request} for which the Handler is
	 * registered.
	 * 
	 * @param r
	 *            The {@link Request}
	 */
	public abstract void handleRequest(Request r);

}
