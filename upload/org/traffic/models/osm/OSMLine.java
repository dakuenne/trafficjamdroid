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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * Wrapperclass for the original data from OpenStreetMap
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class OSMLine {

	/** The unique id */
	private int id;

	/** The type of the street */
	private String highway;

	/** The name of the street - null, if not set */
	private String name;

	/** The speed-limit - null, if not set */
	private String maxspeed;

	/**
	 * The geometry of the street - typically a {@link LineString} or a
	 * {@link com.vividsolutions.jts.geom.MultiLineString}
	 */
	private Geometry way;

	/**
	 * Default-Constructor
	 */
	protected OSMLine() {
	}

	/**
	 * Custom-Constructor with a geometry.
	 * 
	 * @param way
	 *            The geometry
	 */
	public OSMLine(LineString way) {
		this("50", way);
	}

	/**
	 * Custom-Constructor with speed-limit and geometry.
	 * 
	 * @param maxspeed
	 *            A given speed-limit
	 * @param way
	 *            The geometry
	 */
	public OSMLine(String maxspeed, LineString way) {
		this("", maxspeed, way);
	}

	/**
	 * Custom-Constructor with type, speed-limit and geometry.
	 * 
	 * @param highway
	 *            The type of the road
	 * @param maxspeed
	 *            A given speed-limit
	 * @param way
	 *            The geometry
	 */
	public OSMLine(String highway, String maxspeed, LineString way) {
		this.highway = highway;
		this.maxspeed = maxspeed;
		this.way = way;
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
	 * Returns the type of the street. Null if no value given.
	 * 
	 * @return Type of the street
	 */
	public String getHighway() {
		return highway;
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
	 * Returns the speed-limit of the street. Null if no value given.
	 * 
	 * @return The speed-limit
	 */
	public String getMaxspeed() {
		return maxspeed;
	}

	/**
	 * Returns the {@link Geometry} of the street.
	 * 
	 * @return The geometry
	 */
	public Geometry getWay() {
		return way;
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
	 * Sets the type of the highway.
	 * 
	 * @param highway
	 *            The new type
	 */
	public void setHighway(String highway) {
		this.highway = highway;
	}

	/**
	 * Sets the name of the street.
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the speed-limit of the street.
	 * 
	 * @param maxspeed
	 *            The speed-limit
	 */
	public void setMaxspeed(String maxspeed) {
		this.maxspeed = maxspeed;
	}

	/**
	 * Sets the course of the road.
	 * 
	 * @param way
	 *            The course
	 */
	public void setWay(Geometry way) {
		this.way = way;
	}

	@Override
	public String toString() {
		return "OSMLine " + name + ": " + way.toString();
	}

}
