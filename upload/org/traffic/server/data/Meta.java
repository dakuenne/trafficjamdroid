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

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.traffic.database.Database;
import org.traffic.models.traffic.Client;
import org.traffic.utils.IConstants;

/**
 * Class to handle the <code>meta</code>-data of an incoming JSON-request-
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see org.traffic.utils.IConstants
 * @see org.traffic.models.traffic.Client
 * @see net.sf.json.JSONObject
 */
public class Meta {

	/** The type of the request */
	private int type;

	/** The id of the client */
	private String id = null;

	/** The {@link Client} identified by the <code>id</code> */
	private Client client = null;

	/**
	 * Custom-Constructor with the received {@link JSONObject}.
	 * 
	 * @param node
	 *            The received data as JSON
	 */
	public Meta(JSONObject node) {
		if (node == null)
			throw new IllegalArgumentException("meta is missing");

		// check if nodes available
		this.type = node.getInt("type");
		this.id = node.getString("id");
		if (id != null) {
			Session s = Database.session();
			s.beginTransaction();
			client = (Client) s.createCriteria(Client.class)
					.add(Restrictions.eq("hash", id)).setMaxResults(1)
					.uniqueResult();
			Database.end(false);
		}

		if (id == null && type != IConstants.REQUEST_ID)
			throw new IllegalArgumentException("missing id");

		// check the type
		if (type < 1 || type > 11)
			throw new IllegalArgumentException("missing_metadata");
	}

	/**
	 * Returns the request-type.
	 * 
	 * @return The type
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Returns the session-ID.
	 * 
	 * @return The ID
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * Returns the {@link Client} identified by the <code>id</code.
	 * 
	 * @return The {@link Client}
	 */
	public Client getClient() {
		return client;
	}
}
