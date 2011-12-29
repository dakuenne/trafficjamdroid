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

import java.util.HashSet;
import java.util.Set;

/**
 * POJO for the information of a road. Roads are subdivided into RoadStrips and
 * each of them is unique for a combination of street name, type of road and the
 * speed limit.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see RoadStrip
 */
public class Road {

	/** The unique ID */
	private int id;

	/** The name of the street - <code>null</code>, if not set */
	private String name;

	/** The type of the street */
	private String highway;

	/** The speed-limit - <code>null</code>, if not set */
	private Integer maxspeed;

	/** A list of all parts the road is subdivided into */
	private Set<RoadStrip> strips;
	
	/** A flag to identify the quality of the <code>maxspeed</code */
	private Boolean calculated;

	/**
	 * Default-Constructor
	 */
	protected Road() {
		this.strips = new HashSet<RoadStrip>();
	}

	/**
	 * Returns the unique ID
	 * @return The unique ID
	 */
	public int getId() {
		return id;
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
	 * Returns the speed-limit of the street. Null if no value given.
	 * 
	 * @return The speed-limit
	 */
	public Integer getMaxspeed() {
		return maxspeed;
	}

	/**
	 * Returns a collection of all parts.
	 * @return The parts
	 */
	public Set<RoadStrip> getStrips() {
		return strips;
	}
	
	/**
	 * Returns the flag.
	 * @return The flag
	 */
	public Boolean getCalculated() {
		return calculated;
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
	 * Sets the name of the street.
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

	/**
	 * Sets the speed-limit of the street.
	 * 
	 * @param maxspeed
	 *            The speed-limit
	 */
	public void setMaxspeed(Integer maxspeed) {
		this.maxspeed = maxspeed;
	}

	/**
	 * Sets a new Set of parts for the road.
	 * @param strips The new parts
	 */
	public void setStrips(Set<RoadStrip> strips) {
		this.strips = strips;
	}
	
	/**
	 * Sets the flag.
	 * @param calculated The new flag
	 */
	public void setCalculated(Boolean calculated) {
		this.calculated = calculated;
	}

	@Override
	public String toString() {
		return this.name + "(" + maxspeed + "): " + strips.size() + " strips";
	}
}