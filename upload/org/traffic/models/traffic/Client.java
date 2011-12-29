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

/**
 * POJO containing all information about the registered clients.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class Client {

	/** The unique ID */
	private int id;

	/** The ID identifying the current session */
	private String hash;

	/** Flag to check whether the client has acknowledged the ID */
	private boolean ack;

	/** The leasetime of the current ID */
	private Date lease;

	/** The current {@link Route} - <code>null</code> if not active */
	private Route route;

	/**
	 * Default-Constructor
	 */
	public Client() {
		ack = false;
	}

	/**
	 * Custom-Constructor with the ID of the current session and the lease.
	 * 
	 * @param hash
	 *            The ID of the session
	 * @param lease
	 *            The leasetime
	 */
	public Client(String hash, Date lease) {
		this.hash = hash;
		this.lease = lease;
		ack = false;
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
	 * Returns the ID of the session.
	 * 
	 * @return The ID of the session
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Returns whether the ID is acknowledged or not.
	 * 
	 * @return The status of the acknowledgement
	 */
	public boolean isAck() {
		return ack;
	}

	/**
	 * Returns the {@link Route} of this client. Null if not given.
	 * @return The route
	 */
	public Route getRoute() {
		return route;
	}

	/**
	 * Returns the leasetime of the ID.
	 * @return The leasetime
	 */
	public Date getLease() {
		return lease;
	}

	/**
	 * Sets the unique ID.
	 * @param id The unique ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the ID of the session.
	 * @param hash The ID
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * Sets the acknowledgement-status.
	 * @param ack The new status
	 */
	public void setAck(boolean ack) {
		this.ack = ack;
	}

	/**
	 * Sets the new {@link Route}.
	 * @param route The route
	 */
	public void setRoute(Route route) {
		this.route = route;
		if (route != null) {
			this.route.setClient(this);
		}
	}

	/**
	 * Sets the new leasetime.
	 * @param lease The new leasetime
	 */
	public void setLease(Date lease) {
		this.lease = lease;
	}
}
