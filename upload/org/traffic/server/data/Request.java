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
package org.traffic.server.data;

import net.sf.json.JSONObject;

import org.traffic.models.traffic.Client;

/**
 * Class to wrap an incoming JSON-request into <code>meta</code> and
 * <code>data</code>. The <code>meta</code>-information are used to identify the
 * {@link Client} and the <code>data</code>-part provides additional information
 * needed to fulfill the request.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see net.sf.json.JSONObject
 * @see org.traffic.utils.IConstants
 * @see org.traffic.models.traffic.Client
 */
public class Request {

	/** Meta-object for this request */
	private Meta meta;

	/** Data-object for this request */
	private JSONObject data;

	/**
	 * Custom-Constructor with meta and data.
	 * 
	 * @param meta
	 *            The meta-object
	 * @param data
	 *            The data-object (may be <code>null</code>)
	 */
	public Request(Meta meta, JSONObject data) {
		if (meta == null || data == null)
			throw new IllegalArgumentException();
		this.meta = meta;
		this.data = data;
	}

	/**
	 * Returns the <code>data</code> as {@link JSONObject}.
	 * 
	 * @return The data object cast as the given type or null
	 */
	public JSONObject getData() {
		return this.data;
	}

	/**
	 * Returns the type of the request, stored in the <code>meta</code>-object.
	 * Must be a value of the request-types defined in
	 * {@link org.traffic.utils.IConstants}.
	 * 
	 * @return The request-type
	 */
	public int getType() {
		return this.meta.getType();
	}

	/**
	 * Returns the session-ID, which is part of the <code>meta</code>-object.
	 * 
	 * @return The session-ID
	 */
	public String getID() {
		return this.meta.getID();
	}

	/**
	 * Returns the {@link Client} identified by the session-ID.
	 * 
	 * @return The {@link Client}
	 */
	public Client getClient() {
		return this.meta.getClient();
	}
}