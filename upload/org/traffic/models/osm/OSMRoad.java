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
package org.traffic.models.osm;


/**
 * Wrapperclass for the OpenStreetMap-data in the servers database schema
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class OSMRoad {

	/** The unique id */
	private int id;

	/** The type of the street */
	private String highway;

	/** The name of the street - null, if not set */
	private String name;

	/** The speed-limit - null, if not set */
	private String maxspeed;

	/**
	 * Default-Constructor
	 */
	protected OSMRoad() {
	}

	/**
	 * Custom-Constructor with geometry and name.
	 * 
	 * @param name
	 *            The name of the street
	 */
	public OSMRoad(String name) {
		this(name, null);
	}

	/**
	 * Custom-Constructor with geometry, name and type of the street.
	 * 
	 * @param name
	 *            The name of the street
	 * @param highway
	 *            The type of the road
	 */
	public OSMRoad(String name, String highway) {
		this(name, highway, null);
	}

	/**
	 * Custom-Constructor with geometry, name, type of the street and
	 * speed-limit.
	 * 
	 * @param name
	 *            The name of the street
	 * @param highway
	 *            The type of the road
	 * @param maxspeed
	 *            The speed-limit
	 */
	public OSMRoad(String name, String highway, String maxspeed) {
		this.name = name;
		this.highway = highway;
		this.maxspeed = maxspeed;
	}

	/**
	 * Returns the unique ID.
	 * 
	 * @return unique ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the speed-limit of the street. Null if no value given.
	 * 
	 * @return The speed-limit
	 */
	public String getMaxspeed() {
		return maxspeed;
	}

	/**
	 * Returns the name of the street. Null if no value given.
	 * 
	 * @return Name of the street
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of the street. Null if no value given.
	 * 
	 * @return Type of the street
	 */
	public String getHighway() {
		return highway;
	}

	/**
	 * Sets the unique ID.
	 * 
	 * @param id
	 *            The unique ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the speed-limit of the street
	 * 
	 * @param maxspeed
	 *            The speed-limit
	 */
	public void setMaxspeed(String maxspeed) {
		this.maxspeed = maxspeed;
	}

	/**
	 * Sets the name of the street
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the type of the highway.
	 * 
	 * @param highway
	 *            The new type
	 */
	public void setHighway(String highway) {
		this.highway = highway;
	}

	@Override
	public String toString() {
		return "OSMRoad " + name;
	}

}
