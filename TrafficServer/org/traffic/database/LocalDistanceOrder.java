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
package org.traffic.database;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Class to provide an object to order database queries, created with the
 * Hibernate Criteria API.
 * <p>
 * This calculates the distance of all entities to a given comparison geometry
 * and orders the result by this value.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class LocalDistanceOrder extends Order {

	/** A unique ID */
	private static final long serialVersionUID = 1L;

	/** Sorting direction - ascending if true, otherwise descending */
	private boolean ascending;

	/** The geometry to which the distance is calculated */
	private Geometry geom;

	/**
	 * CustomConstructor used in <code>asc</code> and <code>desc</code>.
	 * 
	 * @param prop
	 *            The name of the property
	 * @param asc
	 *            The sorting direction
	 * @param geom
	 *            The base geometry
	 */
	private LocalDistanceOrder(String prop, boolean asc, Geometry geom) {
		super(prop, asc);
		ascending = asc;
		this.geom = geom;
	}

	@Override
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {
		StringBuffer fragment = new StringBuffer();
		fragment.append("ST_DISTANCE(this_.way, GeometryFromText('" + geom
				+ "', 4326))");
		fragment.append(ascending ? " asc" : " desc");
		return fragment.toString();
	}

	/**
	 * Creates an instance of an ascending {@link Order} with the given
	 * parameters.
	 * 
	 * @param propertyName
	 *            The name of the property
	 * @param geom
	 *            The comparison geometry
	 * @return The ascending order
	 */
	public static Order asc(String propertyName, Geometry geom) {
		return new LocalDistanceOrder(propertyName, true, geom);
	}

	/**
	 * Creates an instance of an descending {@link Order} with the given
	 * parameters.
	 * 
	 * @param propertyName
	 *            The name of the property
	 * @param geom
	 *            The comparison geometry
	 * @return The descending Order
	 */
	public static Order desc(String propertyName, Geometry geom) {
		return new LocalDistanceOrder(propertyName, false, geom);
	}
}
