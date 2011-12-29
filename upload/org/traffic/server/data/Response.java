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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class collecting all data for the response in a JSON-like object-structure.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class Response {

	/** The content of the response */
	private Object data;

	/**
	 * Default-Constructor
	 */
	public Response() {
		this.data = null;
	}

	/**
	 * Appends the <code>value</code> with the given <code>keys</code> to the
	 * response.
	 * 
	 * @param value
	 *            The value
	 * @param keys
	 *            The keys - could be empty
	 */
	@SuppressWarnings("unchecked")
	public void append(Object value, String... keys) {
		if (keys.length == 0) {
			// check data is the correct object
			if (this.data == null)
				this.data = new LinkedList<Object>();
			if (!(this.data instanceof List))
				throw new IllegalArgumentException("data is no list!");

			// and add the value
			List<Object> list = (List<Object>) this.data;
			list.add(value);
		}
		// get the parent and then add the object
		else {
			// get the parent-object and add the value
			List<Object> parent = (List<Object>) getParent(false, keys);
			parent.add(value);
		}
	}

	/**
	 * Sets the <code>value</code> for the given <code>keys</code>.
	 * 
	 * @param value
	 *            The value
	 * @param keys
	 *            The keys - could be empty
	 */
	@SuppressWarnings("unchecked")
	public void set(Object value, String... keys) {
		// if no keys, store directly under data
		if (keys.length == 0) {
			if (this.data != null)
				throw new IllegalStateException("data was already set!");
			this.data = value;
		}
		// if more than one key traverse it
		else {
			// get the parent and check the value is not in the map already
			Map<String, Object> parent = (Map<String, Object>) getParent(true,
					keys);
			String key = keys[keys.length - 1];
			if (parent.containsKey(key))
				throw new IllegalArgumentException(
						"the last key is already present");
			parent.put(key, value);
		}
	}

	/**
	 * Searches for the parent object, if the depth of the keys is more than
	 * one.
	 * 
	 * @param asMap
	 *            Result as map or list
	 * @param keys
	 *            The keys to search
	 * @return The parent object
	 */
	@SuppressWarnings("unchecked")
	private Object getParent(boolean asMap, String... keys) {
		// make sure there is at least one key!
		if (keys.length < 1)
			throw new IllegalArgumentException();

		// first check data is a map
		if (this.data == null)
			this.data = new HashMap<String, Object>();
		if (!(this.data instanceof Map))
			throw new IllegalStateException(
					"Response: Data already set but not a map!");

		// now iterate over the n-1 keys
		Map<String, Object> parent = (Map<String, Object>) this.data;
		for (int i = 0; i < keys.length - 1; ++i) {
			// check if the key is present and, if so, a map. Otherwise create
			// the map
			if (parent.containsKey(keys[i])) {
				if (!(parent.get(keys[i]) instanceof Map))
					throw new IllegalStateException(
							"Response: One element in the chain is not a map!");
			} else {
				parent.put(keys[i], new HashMap<String, Object>());
			}

			// get as new parent
			parent = (Map<String, Object>) parent.get(keys[i]);
		}

		Object return_value;

		// if not as map, check the last Key is a list
		if (!asMap) {
			String last_key = keys[keys.length - 1];
			// if the key is in the map check it is a list and set as
			// return_value
			if (parent.containsKey(last_key)) {
				if (!(parent.get(last_key) instanceof List))
					throw new IllegalStateException(
							"Response: Key is set but not a list!");
				return_value = parent.get(last_key);
			}
			// otherwise add a new list and set as parent
			else {
				List<Object> list = new LinkedList<Object>();
				parent.put(last_key, list);
				return_value = list;
			}
		} else {
			return_value = parent;
		}

		// finally return the object
		return return_value;
	}

	/**
	 * Returns the content of the response.
	 * 
	 * @return The content
	 */
	public Object getData() {
		return data;
	}

}
