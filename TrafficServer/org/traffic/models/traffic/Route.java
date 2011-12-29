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

import com.vividsolutions.jts.geom.LineString;

/**
 * POJO for all informations about a route. Instances are created if a
 * {@link Client} starts a routing and deleted if the target is reached or the
 * user stops the routing.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see Client
 */
public class Route {

	/** The unique ID */
	private int id;

	/** The geometry describing the route */
	private LineString route;

	/** The time the routing started */
	private Date started;

	/** The {@link Client} which started the route */
	private Client client;

	/** Flag to check whether the route was updated */
	private boolean updated;

	/** The request-string for CloudMade */
	private String cloudmade;

	/**
	 * Default-Constructor
	 */
	protected Route() {
	}

	/**
	 * Custom-Constructor with starting time and route.
	 * 
	 * @param started
	 *            The starting time
	 * @param route
	 *            The route
	 */
	public Route(Date started, LineString route) {
		this.started = started;
		this.route = route;
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
	 * Returns the geometry of the route.
	 * 
	 * @return The geometry
	 */
	public LineString getRoute() {
		return route;
	}

	/**
	 * Returns the time the routing started.
	 * 
	 * @return The starting time
	 */
	public Date getStarted() {
		return started;
	}

	/**
	 * Returns the {@link Client} which started the route.
	 * 
	 * @return The {@link Client}
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * Checks whether the route was updated.
	 * 
	 * @return The update-status
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * Returns the query-string for CloudMade.
	 * 
	 * @return The query-string
	 */
	public String getCloudmade() {
		return cloudmade;
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
	 * Sets the route.
	 * 
	 * @param route
	 *            The route
	 */
	public void setRoute(LineString route) {
		this.route = route;
	}

	/**
	 * Sets the starting time.
	 * 
	 * @param started
	 *            The starting time
	 */
	public void setStarted(Date started) {
		this.started = started;
	}

	/**
	 * Sets the {@link Client}.
	 * 
	 * @param client
	 *            The {@link Client}
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * Sets the update-status
	 * 
	 * @param updated
	 *            The update-status
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	/**
	 * Sets the query-string for CloudMade.
	 * 
	 * @param cloudmade
	 *            The query-string
	 */
	public void setCloudmade(String cloudmade) {
		this.cloudmade = cloudmade;
	}
}