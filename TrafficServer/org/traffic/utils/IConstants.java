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
package org.traffic.utils;

/**
 * Interface to provide the constants used in this application (e.g. the request
 * types).
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public interface IConstants {

	@Deprecated
	/** Request type - sends the path to the databases*/
	public static final int REQUEST_DB = 1;
	/**
	 * Request type - sends an update for the current position of the requesting
	 * client
	 */
	public static final int REQUEST_UPDATE = 2;
	/** Request type - receives a new congestion */
	public static final int REQUEST_SET_CONGESTION = 3;
	/** Request type - creates a new session id for the client */
	public static final int REQUEST_ID = 4;
	/** Request type - receives the acknowledgement the session_id */
	public static final int REQUEST_ACK_ID = 5;
	/** Request type - deletes a congestion */
	public static final int REQUEST_DELETE_CONGESTION = 6;
	/** Request type - calculates an optimal route */
	public static final int REQUEST_CALCULATE_ROUTE = 7;
	/** Request type - refreshes the session_id for a client */
	public static final int REQUEST_REFRESH_ID = 8;
	/** Request type - sends the calculated or refreshed route */
	public static final int REQUEST_GET_ROUTE = 9;
	/** Request type - deletes the current route */
	public static final int REQUEST_DELETE_ROUTE = 10;
	/** Request type - sends all known problems */
	public static final int REQUEST_GET_PROBLEMS = 11;

	@Deprecated
	/** URL of the folder with the database-files */
	public static final String DB_SERVER_PATH = "http://131.173.22.12/fileserver/databases/";

	/** Congestion type - jam on the road */
	public static final int CONGESTION_JAM = 0;
	/** Congestion type - crash on the road */
	public static final int CONGESTION_CRASH = 1;
	/** Congestion type - road under construction */
	public static final int CONGESTION_CONSTRUCTION = 2;
	/** Congestion type - icy road */
	public static final int CONGESTION_ICE = 3;
	/** Congestion type - an event on the road */
	public static final int CONGESTION_EVENT = 4;
	/** Congestion type - general congestion, not specified */
	public static final int CONGESTION_GENERAL = 5;

	/** CloudMade-Request - navigation */
	public static final String CM_NAVIGATION = "http://navigation.cloudmade.com/f299360fab694616b7d00ee0f701e94d/api/0.3/";
	/** CloudMade-Request - geocoding */
	public static final String CM_GEOCODING = "http://geocoding.cloudmade.com/f299360fab694616b7d00ee0f701e94d/geocoding/v2/find.js?object_type=road&results=1&around=";
}
