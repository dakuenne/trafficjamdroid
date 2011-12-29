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
import java.util.GregorianCalendar;

import com.vividsolutions.jts.geom.Point;

/**
 * POJO containing all needed information about a congestion. New objects are
 * created if a {@link Client} sends data about a recognized problem.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see org.traffic.utils.IConstants
 * @see RoadStrip
 */
public class Congestion {

	/** The unique ID */
	private int id;

	/** Type of the congestion - see {@link org.traffic.utils.IConstants} */
	private int type;

	/** Position of the congestion */
	private Point position;

	/** The time the congestion was reported */
	private Date reportingtime;

	/** The {@link RoadStrip} the congestion belongs to */
	private RoadStrip roadstrip;

	/**
	 * Default-Constructor
	 */
	public Congestion() {
	}

	/**
	 * Custom-Constructor with type and position.
	 * 
	 * @param type
	 *            The type of the congestion
	 * @param position
	 *            The position
	 */
	public Congestion(int type, Point position) {
		this(type, position, GregorianCalendar.getInstance().getTime());
	}

	/**
	 * Custom-Constructor with type, position and reportingtime.
	 * 
	 * @param type
	 *            The type of the congestion
	 * @param position
	 *            The position
	 * @param time
	 *            The reportingtime
	 */
	public Congestion(int type, Point position, Date time) {
		this.type = type;
		this.position = position;
		this.reportingtime = time;
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
	 * Returns the type of the congestion.
	 * 
	 * @return The type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the position of the congestion.
	 * 
	 * @return The position
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * Returns the reportingtime.
	 * 
	 * @return The time
	 */
	public Date getReportingtime() {
		return reportingtime;
	}

	/**
	 * Returns the belonging {@link RoadStrip}.
	 * 
	 * @return The {@link RoadStrip}
	 */
	public RoadStrip getRoadstrip() {
		return roadstrip;
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
	 * Sets the type of the congestion.
	 * 
	 * @param type
	 *            The type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Sets the position.
	 * 
	 * @param position
	 *            The new position
	 */
	public void setPosition(Point position) {
		this.position = position;
	}

	/**
	 * Sets the reportingtime.
	 * 
	 * @param reportingtime
	 *            The time
	 */
	public void setReportingtime(Date reportingtime) {
		this.reportingtime = reportingtime;
	}

	/**
	 * Sets the belonging {@link RoadStrip}.
	 * 
	 * @param roadstrip
	 *            The new {@link RoadStrip}
	 */
	public void setRoadstrip(RoadStrip roadstrip) {
		this.roadstrip = roadstrip;
	}
}