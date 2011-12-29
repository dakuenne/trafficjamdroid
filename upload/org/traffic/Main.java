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
package org.traffic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.server.Controller;
import org.traffic.server.handler.AcknowledgeHandler;
import org.traffic.server.handler.CalculateRouteHandler;
import org.traffic.server.handler.CongestionHandler;
import org.traffic.server.handler.DeleteCongestionHandler;
import org.traffic.server.handler.DeleteRouteHandler;
import org.traffic.server.handler.GetProblemsHandler;
import org.traffic.server.handler.IDHandler;
import org.traffic.server.handler.RefreshIDHandler;
import org.traffic.server.handler.RefreshRouteHandler;
import org.traffic.server.handler.UpdateHandler;
import org.traffic.services.CleanUpDBService;
import org.traffic.services.PriceUserDataService;
import org.traffic.services.RefreshRoutesService;
import org.traffic.services.SetDirectionService;
import org.traffic.services.UpdateSpeedService;
import org.traffic.utils.IConstants;

/**
 * Class to initialize the server and accept the requests of the clients.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 234 $
 * @see org.traffic.server.Controller
 */
public class Main {

	public static void main(String[] args) {

		Log.i("Main",
				"starting server at " + new Date(System.currentTimeMillis()));
		ServerSocket server = null;
		try {
			Database.initialize();
			server = new ServerSocket(10101);
		} catch (Exception e) {
			Log.e("Main", e.getClass() + "@main: " + e.getMessage());
			System.exit(1);
		} catch (Error e) {
			Log.e("Main", e.getClass() + "@main: " + e.getMessage());
			System.exit(1);
		}

		Log.i("Main", "registering handlers");
		Controller.registerHandler(IConstants.REQUEST_ID, IDHandler.class);
		Controller.registerHandler(IConstants.REQUEST_ACK_ID,
				AcknowledgeHandler.class);
		Controller.registerHandler(IConstants.REQUEST_CALCULATE_ROUTE,
				CalculateRouteHandler.class);
		Controller.registerHandler(IConstants.REQUEST_DELETE_CONGESTION,
				DeleteCongestionHandler.class);
		Controller.registerHandler(IConstants.REQUEST_SET_CONGESTION,
				CongestionHandler.class);
		Controller.registerHandler(IConstants.REQUEST_UPDATE, UpdateHandler.class);
		Controller.registerHandler(IConstants.REQUEST_REFRESH_ID, RefreshIDHandler.class);
		Controller.registerHandler(IConstants.REQUEST_GET_ROUTE, RefreshRouteHandler.class);
		Controller.registerHandler(IConstants.REQUEST_DELETE_ROUTE, DeleteRouteHandler.class);
		Controller.registerHandler(IConstants.REQUEST_GET_PROBLEMS, GetProblemsHandler.class);

		Log.i("Main", "starting services");
		new CleanUpDBService(1800000).start();
		new SetDirectionService(1800000).start();
		new RefreshRoutesService(120000).start();
		new PriceUserDataService(300000).start();
		new UpdateSpeedService(86400000).start();

		// always accepting
		for (;;) {
			Socket s;
			try {
				// wait for clients to establish a connection
				s = server.accept();
				Controller serv = new Controller(s);
				serv.start();
			} catch (IOException e) {
				Log.e("Main", e.getClass() + "@main: " + e.getMessage());
			}
		}
	}
}
