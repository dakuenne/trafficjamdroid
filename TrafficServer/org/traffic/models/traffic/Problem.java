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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * POJO containing all information about a recurring problem. Instances of this
 * class are only created within the
 * {@link org.traffic.services.FindRecurringProblemsService}.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 145 $
 */
public class Problem {

	/** A map with the weekdays in German language */
	private static final Map<Integer, String> WEEKDAYS = new HashMap<Integer, String>();

	/** The unique ID */
	private int id;

	/** The day of week - <code>null</code> if on different days */
	private Integer dow;

	/** The hour */
	private Integer hour;

	/** The IDs of the regions, in which this problem was recognized */
	private String regionJSON;

	/**
	 * A generated description of the problem - containing a street name and the
	 * weekdays
	 */
	private String description;

	/**
	 * Class-Constructor for the weekdays
	 */
	static {
		WEEKDAYS.put(0, "Sonntags");
		WEEKDAYS.put(1, "Montags");
		WEEKDAYS.put(2, "Dienstags");
		WEEKDAYS.put(3, "Mittwochs");
		WEEKDAYS.put(4, "Donnerstags");
		WEEKDAYS.put(5, "Freitags");
		WEEKDAYS.put(6, "Samstags");
	}

	/**
	 * Default-Constructor
	 */
	protected Problem() {
	}

	/**
	 * Custom-Constructor for problems at one specific hour at one specific
	 * weekday.
	 * 
	 * @param dow
	 *            The day of week
	 * @param hour
	 *            The hour
	 */
	public Problem(int dow, int hour) {
		this(dow, hour, null);
	}

	/**
	 * Custom-Constructor for problems in a specific region at a specific hour.
	 * 
	 * @param hour
	 *            The hour
	 * @param region
	 *            The IDs of the regions
	 */
	public Problem(int hour, String region) {
		this(null, hour, region);
	}

	/**
	 * Custom-Constructor for problems in a specific region at an hour at a dow.
	 * 
	 * @param dow
	 *            The day of week
	 * @param hour
	 *            The hour
	 * @param region
	 *            The IDs of the regions
	 */
	public Problem(Integer dow, int hour, String region) {
		this.dow = dow;
		this.hour = hour;
		this.regionJSON = region;
	}

	/**
	 * Returns the unique ID.
	 * 
	 * @return The unique ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the day of week.
	 * 
	 * @return The dow
	 */
	public Integer getDow() {
		return dow;
	}

	/**
	 * Returns the hour.
	 * 
	 * @return The hour
	 */
	public Integer getHour() {
		return hour;
	}

	/**
	 * Returns the list of regions as JSON-String.
	 * 
	 * @return The regions
	 */
	public String getRegionJSON() {
		return regionJSON;
	}

	/**
	 * Returns the problems description.
	 * 
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the unique ID.
	 * 
	 * @param id
	 *            The new ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the day of week.
	 * 
	 * @param dow
	 *            The new dow
	 */
	public void setDow(Integer dow) {
		this.dow = dow;
	}

	/**
	 * Sets the hour.
	 * 
	 * @param hour
	 *            The new hour
	 */
	public void setHour(Integer hour) {
		this.hour = hour;
	}

	/**
	 * Sets the new JSON-String for the regions.
	 * 
	 * @param region
	 *            The regions
	 */
	public void setRegionJSON(String region) {
		this.regionJSON = region;
	}

	/**
	 * Sets a new description for the problem.
	 * 
	 * @param description
	 *            The new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Generates a description with the passed list of days of weeks and a
	 * street name.
	 * 
	 * @param dows
	 *            The days of week
	 * @param street
	 *            A street name
	 */
	public void generateDescription(List<Integer> dows, String street) {
		StringBuffer buff = new StringBuffer();
		buff.append("Wiederkehrendes Problem");
		if (street != null) {
			buff.append(" (" + street + ")");
		}
		buff.append(": ");
		for (Integer i : dows) {
			buff.append(WEEKDAYS.get(i) + ", ");
		}
		buff.append("jeweils ab " + hour + ":00 Uhr");
		description = buff.toString();
	}

	@Override
	public String toString() {
		return description;
	}
}
