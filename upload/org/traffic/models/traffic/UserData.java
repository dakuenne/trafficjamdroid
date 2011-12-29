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

import java.util.Date;

import com.vividsolutions.jts.geom.Point;

/**
 * POJO for all informations collected by the clients. The <code>connectionhash</code> is
 * used by {@link org.traffic.services.SetDirectionService} to set the driving direction in the
 * attributes <code>to_start</code> and <code>to_end</code>.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class UserData {

	/** The unique ID */
	private int id;

	/** The time of the measure */
	private Date time;

	/** The position of the measure */
	private Point position;

	/** The speed of the measuring client */
	private double speed;

	/** The current session-ID */
	private String connectionhash;

	/** The ID of the belonging {@link RoadStrip} - null if none found */
	private Integer road_id;

	/**
	 * Flag for the driving direction indicating a movement from the end to the
	 * start in the {@link RoadStrip}
	 */
	private Boolean to_start;

	/**
	 * Flag for the driving direction indicating a movement from the start to
	 * the end in the {@link RoadStrip}
	 */
	private Boolean to_end;

	/**
	 * Default-Constructor
	 */
	public UserData() {
	}

	/**
	 * Custom-Constructor with time, position, speed and session-id of the
	 * measure.
	 * 
	 * @param time
	 *            The acquisition time
	 * @param position
	 *            The acquisition position
	 * @param speed
	 *            The measured speed
	 * @param hash
	 *            The session-ID
	 */
	public UserData(Date time, Point position, double speed, String hash) {
		this.time = time;
		this.position = position;
		this.connectionhash = hash;
		this.speed = speed;
		this.to_start = null;
		this.to_end = null;
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
	 * Returns the acquisition time.
	 * 
	 * @return The acquisition time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * Returns the acquisition position.
	 * 
	 * @return The acquisition position
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Returns the measured speed.
	 * 
	 * @return The speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Returns the session-ID.
	 * 
	 * @return The ID
	 */
	public String getConnectionhash() {
		return connectionhash;
	}

	/**
	 * Returns the ID of the belonging {@link RoadStrip}.
	 * 
	 * @return The ID
	 */
	public Integer getRoad_id() {
		return road_id;
	}

	/**
	 * Returns the flag for the driving direction.
	 * 
	 * @return The flag
	 */
	public Boolean getTo_start() {
		return to_start;
	}

	/**
	 * Returns the flag for the driving direction.
	 * 
	 * @return The flag
	 */
	public Boolean getTo_end() {
		return to_end;
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
	 * Sets the acquisition time.
	 * 
	 * @param time
	 *            The acquisition time
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * Sets the acquisition position.
	 * 
	 * @param position
	 *            The acquisition position
	 */
	public void setPosition(Point position) {
		this.position = position;
	}

	/**
	 * Sets the speed.
	 * 
	 * @param speed
	 *            The new speed
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Sets the session-ID.
	 * 
	 * @param connectionhash
	 *            The new session-ID
	 */
	public void setConnectionhash(String connectionhash) {
		this.connectionhash = connectionhash;
	}

	/**
	 * Sets the ID of the belonging {@link RoadStrip}.
	 * 
	 * @param road
	 *            The ID
	 */
	public void setRoad_id(Integer road) {
		this.road_id = road;
	}

	/**
	 * Sets the flag for the driving direction.
	 * 
	 * @param to_start
	 *            The flag
	 */
	public void setTo_start(Boolean to_start) {
		this.to_start = to_start;
	}

	/**
	 * Sets the flag for the driving direction.
	 * 
	 * @param to_end
	 *            The flag
	 */
	public void setTo_end(Boolean to_end) {
		this.to_end = to_end;
	}

	@Override
	public String toString() {
		return "UserData:" + time + "," + position + "," + speed + ","
				+ road_id;
	}
}
