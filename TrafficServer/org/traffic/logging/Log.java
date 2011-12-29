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
package org.traffic.logging;

/**
 * Class to log text-messages to <code>System.out</code>.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class Log {

	/**
	 * Send a <code>DEBUG</code> log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param message
	 *            The message you would like logged.
	 */
	public static void d(String tag, String message) {
		System.out.println("DEBUG|" + tag + "|" + message);
	}

	/**
	 * Send a <code>ERROR</code> log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param message
	 *            The message you would like logged.
	 */
	public static void e(String tag, String message) {
		System.out.println("ERROR|" + tag + "|" + message);
	}

	/**
	 * Send a <code>INFO</code> log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param message
	 *            The message you would like logged.
	 */
	public static void i(String tag, String message) {
		System.out.println("INFO|" + tag + "|" + message);
	}

	/**
	 * Send a <code>VERBOSE</code> log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param message
	 *            The message you would like logged.
	 */
	public static void v(String tag, String message) {
		System.out.println("VERBOSE|" + tag + "|" + message);
	}

	/**
	 * Send a <code>WARNING</code> log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param message
	 *            The message you would like logged.
	 */
	public static void w(String tag, String message) {
		System.out.println("WARNING|" + tag + "|" + message);
	}

}
