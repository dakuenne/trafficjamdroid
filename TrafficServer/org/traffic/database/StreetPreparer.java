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

import java.util.List;

import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.hibernatespatial.GeometryUserType;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This class provides a bunch of methods to set up the needed information for
 * the server to run. It needs the databases schemas <code>osmdata</code> for
 * the original OpenStreetMap-data and <code>data</code> for the tables which
 * store the information used at runtime.
 * <p>
 * The six steps are:
 * <ol>
 * <li>Extracting all streets from the OpenStreetMap-data</li>
 * <li>Importing the needed information to a table in the servers database schema</li>
 * <li>Merging the overlapping streets to a single {@link com.vividsolutions.jts.geom.LineString}</li>
 * <li>Splitting the new created streets at all crossroads</li>
 * <li>Creating instances for the server to work with</li>
 * <li>Cleaning up the database</li>
 * </ol>
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 143 $
 * @see org.traffic.models.traffic.Road
 * @see org.traffic.models.traffic.Road
 * @see org.traffic.models.osm.OSMLine
 * @see org.traffic.models.osm.OSMRoad
 */
public class StreetPreparer {

	/**
	 * Merges the data given in table <code>osmdata.planet_osm_line</code>. The
	 * lines are grouped by street name, street type and speed limit.
	 */
	private static void mergeOriginalOSMData() {
		Session s = Database.session();
		s.beginTransaction();
		s.createSQLQuery(
				"INSERT INTO data.osmlines(way, highway, name, maxspeed) "
						+ "SELECT ST_LineMerge(ST_Union(way)), highway, name, maxspeed "
						+ "FROM osmdata.planet_osm_line "
						+ "WHERE highway IS NOT NULL "
						+ "GROUP BY name, highway, maxspeed").executeUpdate();
		Database.end(true);
	}

	/**
	 * Extracts the information about the roads, which is used in
	 * {@link org.traffic.models.traffic.Road}.
	 * <p>
	 * A <code>TypeCastException</code> thrown by the database is possible, if
	 * the <code>maxspeed</code>-value is not numeric or <code>null</code>.
	 */
	private static void extractRoadInformation() {
		Session s = Database.session();
		s.beginTransaction();
		s.createSQLQuery(
				"INSERT INTO data.roads(id, highway, name, maxspeed) "
						+ "SELECT id, highway, name, CAST(maxspeed AS Integer) FROM data.osmlines")
				.executeUpdate();
		Database.end(true);
	}

	/**
	 * Creates instances of the class
	 * {@link org.traffic.models.traffic.RoadStrip} by calculating intersection
	 * points of a new road with all other new roads and the already saved
	 * roads.
	 * <p>
	 * This method needs an installed version of PostGIS 2.0 or later!
	 */
	@SuppressWarnings("unchecked")
	private static void createRoadStrips() {
		Session s = Database.session();
		s.beginTransaction();
		List<Integer> ids = (List<Integer>) s
				.createSQLQuery("SELECT id FROM data.osmlines")
				.addScalar("id", StandardBasicTypes.INTEGER).list();
		for (Integer i : ids) {
			// calculate intersection points with other new linetrings
			List<Geometry> geoms = (List<Geometry>) s
					.createSQLQuery(
							"SELECT (St_Dump(ST_Intersection(o.way, c.way))).geom AS geom "
									+ "FROM data.osmlines o, data.osmlines c "
									+ "WHERE o.id = " + i
									+ " AND o.name <> c.name "
									+ "AND (ST_Crosses(o.way, c.way) "
									+ "OR ST_Touches(o.way, c.way))")
					.addScalar("geom", GeometryUserType.TYPE).list();

			// strip the linestrings with the calculated points
			for (Geometry g : geoms) {
				s.createSQLQuery(
						"UPDATE data.osmlines SET way = ST_CollectionExtract(ST_Split(way, :g),2) WHERE id = "
								+ i).setParameter("g", g).executeUpdate();
			}

			// calculate intersection points with other old linetrings
			geoms = (List<Geometry>) s
					.createSQLQuery(
							"SELECT (St_Dump(ST_Intersection(o.way, c.way))).geom AS geom "
									+ "FROM data.osmlines o, data.roadstrips c "
									+ "WHERE o.id = " + i + " "
									+ "AND (ST_Crosses(o.way, c.way) "
									+ "OR ST_Touches(o.way, c.way))")
					.addScalar("geom", GeometryUserType.TYPE).list();

			// strip the linestrings with the calculated points
			for (Geometry g : geoms) {
				s.createSQLQuery(
						"UPDATE data.osmlines SET way = ST_CollectionExtract(ST_Split(way, :g),2) WHERE id = "
								+ i).setParameter("g", g).executeUpdate();
			}

			// saving the whole bunch as roadstrips
			s.createSQLQuery(
					"INSERT INTO data.roadstrips(id, road_id, way) "
							+ "SELECT nextval('data.strips_id_seq'), id, (ST_Dump(way)).geom "
							+ "FROM data.osmlines WHERE id = " + i)
					.executeUpdate();
		}
		Database.end(true);
	}

	/**
	 * Cleans up all temporarily used tables
	 */
	public static void cleanUpDB() {
		Session s = Database.session();
		s.beginTransaction();
		s.createSQLQuery("DELETE FROM data.osmlines").executeUpdate();
		s.createSQLQuery("DELETE FROM data.osmroads").executeUpdate();
		Database.end(true);
	}

	/**
	 * Prepares the data given in osmdata.planet_osm_line and creates the needed
	 * instances of {@link org.traffic.models.traffic.Road} and
	 * {@link org.traffic.models.traffic.RoadStrip}.
	 * <p>
	 * This method initializes the database if it has not already happened.
	 */
	public static void prepareData() {
		if (!Database.isInitialized()) {
			Database.initialize();
		}

		mergeOriginalOSMData();
		extractRoadInformation();
		createRoadStrips();
		cleanUpDB();
	}

}
