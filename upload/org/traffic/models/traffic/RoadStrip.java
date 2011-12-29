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

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.LineString;

/**
 * POJO containing all information about a part of a {@link Road}. This is the
 * most important class, used for nearly all requests.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see SpeedToEnd
 * @see SpeedToStart
 * @see Congestion
 * @see Road
 */
public class RoadStrip {

	/** The unique ID */
	private int id;

	/** The geometry describing this object */
	private LineString way;

	/**
	 * A collection of speeds going from the end to the start of the
	 * way-attribute
	 */
	private SortedSet<SpeedToStart> temposToStart;

	/**
	 * A collection of speeds going from the start to the end of the
	 * way-attribute
	 */
	private SortedSet<SpeedToEnd> temposToEnd;

	/** A collection of all congestions */
	private Set<Congestion> congestions;

	/** The {@link Road} this object belongs to */
	private Road road;

	/**
	 * Default-Constructor
	 */
	protected RoadStrip() {
		temposToStart = new TreeSet<SpeedToStart>();
		temposToEnd = new TreeSet<SpeedToEnd>();
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
	 * Returns the geometry of this object.
	 * 
	 * @return The geometry
	 */
	public LineString getWay() {
		return way;
	}

	/**
	 * Returns the speeds measured from the end to the start.
	 * 
	 * @return List of speeds
	 */
	public Set<SpeedToStart> getTemposToStart() {
		return temposToStart;
	}

	/**
	 * Returns the speeds measured from the start to the end.
	 * 
	 * @return List of speeds
	 */
	public Set<SpeedToEnd> getTemposToEnd() {
		return temposToEnd;
	}

	/**
	 * Returns a list of congestions.
	 * 
	 * @return List of congestions
	 */
	public Set<Congestion> getCongestions() {
		return congestions;
	}

	/**
	 * Returns the {@link Road}.
	 * 
	 * @return The road
	 */
	public Road getRoad() {
		return road;
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
	 * Sets the geometry.
	 * 
	 * @param way
	 *            The new geometry
	 */
	public void setWay(LineString way) {
		this.way = way;
	}

	/**
	 * Sets a new list of speeds.
	 * 
	 * @param tempos
	 *            The new list
	 */
	public void setTemposToStart(Set<SpeedToStart> tempos) {
		this.temposToStart.addAll(tempos);
	}

	/**
	 * Sets a new list of speeds.
	 * 
	 * @param tempos
	 *            The new list
	 */
	public void setTemposToEnd(Set<SpeedToEnd> tempos) {
		this.temposToEnd.addAll(tempos);
	}

	/**
	 * Sets a new list of congestions.
	 * 
	 * @param congestions
	 *            The new list
	 */
	public void setCongestions(Set<Congestion> congestions) {
		this.congestions = congestions;
	}

	/**
	 * Sets the {@link Road}.
	 * 
	 * @param road
	 *            The road
	 */
	public void setRoad(Road road) {
		this.road = road;
	}

	/**
	 * Searches for the best speed found in the database.
	 * 
	 * @param toStart
	 *            The driving direction
	 * @return The best value
	 */
	public Speed getBestSpeed(boolean toStart) {
		// best speed from end to start
		if (!this.temposToStart.isEmpty() && toStart) {
			return this.temposToStart.first();
		}

		// best speed from start to end
		if (!this.temposToEnd.isEmpty() && !toStart) {
			return this.temposToEnd.first();
		}

		// default value
		double speed = (this.getRoad().getMaxspeed() != null) ? this.getRoad()
				.getMaxspeed() : -1;
		return new SpeedToStart("-100", -1, speed, this);
	}

	@Override
	public String toString() {
		return this.way.toString();
	}
}
