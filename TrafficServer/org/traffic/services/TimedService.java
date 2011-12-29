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
package org.traffic.services;

import org.traffic.logging.Log;

/**
 * Superclass of all Services which make a pause between each run.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public abstract class TimedService extends Service {

	/** The time to pause */
	private long timeInMillis;

	/**
	 * Custom-Constructor of a TimedService.
	 * 
	 * @param time
	 *            Time in millis to pause between the rund
	 */
	public TimedService(long time) {
		timeInMillis = time;
	}

	@Override
	public void run() {
		for (;;) {
			serve();
			try {
				Thread.sleep(timeInMillis);
			} catch (Exception e) {
				Log.e("TimedService", e.getClass() + "@run: " + e.getMessage());
			}
		}
	}
}
