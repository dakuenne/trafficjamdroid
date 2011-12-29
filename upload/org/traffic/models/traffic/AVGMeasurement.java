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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Wrapperclass to store the informations generated in the
 * {@link org.traffic.services.FindRecurringProblemsService}. An instance of
 * this class contains the data of a recurring problems which was recognized at
 * least twice.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 145 $
 */
public class AVGMeasurement {

	/** The day of week */
	private int dow;

	/** The hour */
	private int hour;

	/** The ID of the belonging {@link RoadStrip} */
	private int road_id;

	/** A temporary list to group messages from more than one day */
	private List<Integer> tmpDows;

	/**
	 * Custom-Constructor with day of week, hour and ID of the {@link RoadStrip}
	 * .
	 * 
	 * @param dow
	 *            The day of week
	 * @param hour
	 *            The hour
	 * @param road_id
	 *            The ID of the {@link RoadStrip}
	 */
	public AVGMeasurement(int dow, int hour, int road_id) {
		this.dow = dow;
		this.hour = hour;
		this.road_id = road_id;
		tmpDows = new LinkedList<Integer>();
	}

	/**
	 * Returns the day of week.
	 * 
	 * @return The day of week
	 */
	public int getDow() {
		return dow;
	}

	/**
	 * Returns the hour.
	 * 
	 * @return The hour
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Returns the ID of the {@link RoadStrip}.
	 * 
	 * @return The ID
	 */
	public int getRoad_id() {
		return road_id;
	}

	/**
	 * Returns all temporarily saved dows.
	 * 
	 * @return A list with the dows
	 */
	public List<Integer> getTmpDows() {
		return tmpDows;
	}

	/**
	 * Returns a list with the temporarily saved dows and the <code>dow</code>,
	 * if it's not in the list.
	 * 
	 * @return A list with all dows
	 */
	public List<Integer> getAllDows() {
		List<Integer> lst = new LinkedList<Integer>();
		lst.addAll(tmpDows);
		lst.add(dow);
		Collections.sort(lst);
		return lst;
	}

	/**
	 * Sets the day of the week.
	 * 
	 * @param dow
	 *            The new dow
	 */
	public void setDow(int dow) {
		this.dow = dow;
	}

	/**
	 * Sets the hour.
	 * 
	 * @param hour
	 *            The new hour
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * Sets the ID of the {@link RoadStrip}.
	 * 
	 * @param road_id
	 *            The new ID
	 */
	public void setRoad_id(int road_id) {
		this.road_id = road_id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AVGMeasurement)) {
			return false;
		}
		AVGMeasurement a = (AVGMeasurement) obj;
		return a.getDow() == this.getDow() && a.getHour() == this.getHour()
				&& a.getRoad_id() == this.getRoad_id();
	}

	@Override
	public int hashCode() {
		StringBuffer buff = new StringBuffer();
		buff.append(road_id);
		buff.append(dow);
		buff.append(hour);
		return buff.toString().hashCode();
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("[" + dow);
		for (Integer i : tmpDows) {
			buff.append("," + i);
		}
		buff.append("]");
		return buff.toString();
	}

}
