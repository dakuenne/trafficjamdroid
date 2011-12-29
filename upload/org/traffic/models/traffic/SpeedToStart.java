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
package org.traffic.models.traffic;

/**
 * POJO for all speed-values having a driving direction from the end of the
 * way-attribute in the class {@link RoadStrip} to the start.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see Speed
 */
public class SpeedToStart extends Speed {
	
	/**
	 * Default-Constructor
	 */
	protected SpeedToStart() {
		super();
	}
	
	/**
	 * Custom-Constructor with ID, category, speed and the part of a road.
	 * 
	 * @param id
	 *            The ID
	 * @param category
	 *            The quality
	 * @param speed
	 *            The calculated speed
	 * @param strip
	 *            The part of the road
	 */
	public SpeedToStart(String id, int category, double speed, RoadStrip strip) {
		super(id, category, speed, strip);
	}

}