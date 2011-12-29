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
 * POJO for all speed-informations. This class is not mapped on a table or view
 * in the database. Only the subclasses {@link SpeedToStart} and
 * {@link SpeedToEnd} are used.
 * <p>
 * This class implements {@link Comparable} to allow the belonging instance of
 * {@link RoadStrip} to sort three entities of both {@link SpeedToStart} and
 * {@link SpeedToEnd}.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see RoadStrip
 * @see SpeedToEnd
 * @see SpeedToStart
 */
public class Speed implements Comparable<Speed> {

	/** Quality of the data - indicates very up-to-date data */
	public static final int QUALITY_UP_TO_DATE = 0;
	/**
	 * Quality of the data - indicates data interpolated over the same days -
	 * for example: all tuesdays
	 */
	public static final int QUALITY_DAY = 1;
	/**
	 * Quality of the data - indicates data interpolated over all information
	 * for this road
	 */
	public static final int QUALITY_ALL = 2;

	/** The unique ID */
	private String id;

	/** The quality of the data */
	private Integer category;

	/** The calculated speed */
	private Double speed;

	/** The belonging {@link RoadStrip} */
	private RoadStrip roadstrip;

	/**
	 * Default-Constructor
	 */
	protected Speed() {
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
	public Speed(String id, int category, double speed, RoadStrip strip) {
		this.id = id;
		this.category = category;
		this.speed = speed;
		this.roadstrip = strip;
	}

	/**
	 * Returns the generated unique ID.
	 * 
	 * @return The ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the category of the record.
	 * 
	 * @return The category
	 */
	public Integer getCategory() {
		return category;
	}

	/**
	 * Returns the calculated speed.
	 * 
	 * @return The speed.
	 */
	public Double getSpeed() {
		return speed;
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
	 * Sets the ID.
	 * 
	 * @param id
	 *            The ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the category.
	 * 
	 * @param category
	 *            The new category
	 */
	public void setCategory(Integer category) {
		this.category = category;
	}

	/**
	 * Sets the speed.
	 * 
	 * @param speed
	 *            The speed
	 */
	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	/**
	 * Sets the {@link RoadStrip}.
	 * 
	 * @param roadstrip
	 *            The {@link RoadStrip}
	 */
	public void setRoadstrip(RoadStrip roadstrip) {
		this.roadstrip = roadstrip;
	}

	@Override
	public int compareTo(Speed s) {
		if (!s.getClass().equals(this.getClass()))
			return this.getClass().getName().compareTo(s.getClass().getName());
		return this.getCategory() - s.getCategory();
	}
}