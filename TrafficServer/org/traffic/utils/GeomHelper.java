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
package org.traffic.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This class contains a bunch of methods that help to deal with the PostGIS
 * geometry-types.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class GeomHelper {

	/**
	 * Creates a geometry of the type {@link Point} from a longitude and
	 * latitude value
	 * 
	 * @param lng
	 *            The longitude value
	 * @param lat
	 *            The latitude value
	 * @return The new point
	 */
	public static Point createPoint(double lng, double lat) {
		PrecisionModel prec = new PrecisionModel(PrecisionModel.FLOATING);
		GeometryFactory factory = new GeometryFactory(prec, 4326);
		Coordinate coord = new Coordinate(lng, lat);
		return factory.createPoint(coord);
	}

	/**
	 * Creates a {@link Polygon} from a given center and a diameter
	 * 
	 * @param center
	 *            Center of the polygon
	 * @param diameter
	 *            The diameter in meters
	 * @return A polygon in the shape of a rectangle
	 */
	public static Polygon createRectangle(Point center, double diameter) {
		double left = center.getX()
				- GeomHelper.convertM2Lng(diameter / 2, center);
		double right = center.getX()
				+ GeomHelper.convertM2Lng(diameter / 2, center);
		double bottom = center.getY()
				- GeomHelper.convertM2Lat(diameter / 2, center);
		double top = center.getY()
				+ GeomHelper.convertM2Lat(diameter / 2, center);
		return createRectangle(left, bottom, right, top);
	}

	/**
	 * Creates a {@link Polygon} from several points. The first point will be
	 * used twice (for the first and last point) so the polygon is closed
	 * 
	 * @param points
	 *            List of points describing the polygon
	 * @return A polygon that consists of the given points.
	 */
	public static Polygon createPolygon(Point... points) {
		PrecisionModel prec = new PrecisionModel(PrecisionModel.FLOATING);
		GeometryFactory factory = new GeometryFactory(prec, 4326);
		WKTReader reader = new WKTReader(factory);

		StringBuffer sb = new StringBuffer();
		sb.append("POLYGON((");

		// add all polygons to the string
		for (int i = 0; i < points.length; ++i) {
			Point p = points[i];

			sb.append(p.getX());
			sb.append(" ");
			sb.append(p.getY());
			sb.append(", ");

		}

		// finally add the first point again
		sb.append(points[0].getX());
		sb.append(" ");
		sb.append(points[0].getY());
		sb.append("))");

		try {
			return (Polygon) reader.read(sb.toString());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a {@link LineString} from several points.
	 * 
	 * @param points
	 *            List of points describing the linestring
	 * @return A LineString that consists of the given points.
	 */
	public static LineString createLineString(Point... points) {
		PrecisionModel prec = new PrecisionModel(PrecisionModel.FLOATING);
		GeometryFactory factory = new GeometryFactory(prec, 4326);
		WKTReader reader = new WKTReader(factory);

		StringBuffer sb = new StringBuffer();
		sb.append("LINESTRING (");

		// add all polygons to the string
		for (int i = 0; i < points.length; ++i) {
			Point p = points[i];

			sb.append(p.getX());
			sb.append(" ");
			sb.append(p.getY());
			sb.append(", ");

		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append(")");

		try {
			return (LineString) reader.read(sb.toString());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a rectangle-polygon from the given values. The points for the
	 * rectangle will be left-top, right-top, right-bottom, left-bottom.
	 * 
	 * @param min_lng
	 *            The longitude of the top-left corner
	 * @param min_lat
	 *            The latitude of the top-left corner
	 * @param max_lng
	 *            The longitude of the bottom-right corner
	 * @param max_lat
	 *            The latitude of the bottom-right corner
	 * @return A polygon in the shape of a rectangle
	 */
	public static Polygon createRectangle(double min_lng, double min_lat,
			double max_lng, double max_lat) {

		Point top_left = createPoint(min_lng, max_lat);
		Point top_right = createPoint(max_lng, max_lat);
		Point bottom_right = createPoint(max_lng, min_lat);
		Point bottom_left = createPoint(min_lng, min_lat);

		return createPolygon(top_left, top_right, bottom_right, bottom_left);
	}

	/**
	 * Converts a given distance from meters into latitude.
	 * 
	 * @param m
	 *            Distance in meters
	 * @param point
	 *            Reference point
	 * @return Distance in degree
	 */
	public static double convertM2Lat(double m, Geometry point) {
		return m / 111320;
	}

	/**
	 * Converts a given distance from meters to longitude
	 * 
	 * @param m
	 *            Distance in meters
	 * @param point
	 *            Reference point
	 * @return Distance in degree
	 */
	public static double convertM2Lng(double m, Geometry point) {
		return (m * 360.0 / (2.0 * Math.PI * 6370000 * Math.cos(Math
				.toRadians(point.getCoordinate().y))));
	}

	/**
	 * Converts a given distance from longitude to meters
	 * 
	 * @param degree
	 *            Distance in degree
	 * @param point
	 *            Reference point
	 * @return Distance in meters
	 */
	public static double convertLng2M(double degree, Geometry point) {
		return (degree
				* (2.0 * Math.PI * 6370000 * Math.cos(Math.toRadians(point
						.getCoordinate().y))) / 360.0);
	}

}
