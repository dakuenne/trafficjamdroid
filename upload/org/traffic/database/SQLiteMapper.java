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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.hibernate.Session;
import org.hibernatespatial.criterion.SpatialRestrictions;
import org.traffic.logging.Log;
import org.traffic.models.traffic.RoadStrip;
import org.traffic.utils.GeomHelper;

/**
 * An instance of this class creates an SQLite-database on the server, which
 * stores the information about the geometries stores in the class
 * {@link org.traffic.models.traffic.RoadStrip}. Each database-file provides the
 * data for a field of 0.5 degree x 0.5 degree and is identified by the
 * bottom-left corner.
 * <p>
 * The clients download this files, if they are inside the matching field. To
 * reduce the traffic as much as possible the database-files are compressed with
 * a GZIP-algorithm.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see DecimalFormat
 * @see File
 */
public class SQLiteMapper extends Thread {

	/** Format of the latitude and longitude values in the filename */
	private static final DecimalFormat FOUR_BIT_FORMAT = new java.text.DecimalFormat(
			"0000");

	/** The path to the folder where the databases are stored */
	private static final String DBPATH = "/srv/fileserver/databases/";

	/** The SQL-statement to create the needed table in the SQLite-database */
	private static final String CREATE_TABLE = "CREATE TABLE roadstrips(id INTEGER PRIMARY KEY, way TEXT)";

	/** Latitude of the bottom-left corner */
	private double minLat;

	/** Longitude of the bottom-left corner */
	private double minLon;

	/** The created database-file */
	private File database;

	/**
	 * Custom-Constructor with the latitude and longitude of the bottom-left
	 * corner.
	 * 
	 * @param minLat
	 *            The latitude
	 * @param minLon
	 *            The longitude
	 */
	public SQLiteMapper(int minLat, int minLon) {
		this.minLat = minLat;
		this.minLon = minLon;
		database = new File(DBPATH + FOUR_BIT_FORMAT.format(minLon * 100)
				+ FOUR_BIT_FORMAT.format(minLat * 100) + ".db");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		if (database.exists()) {
			database.delete();
		}
		try {
			// setting up the file and the database
			Log.i("SQLiteMapper", "setting up DB " + database.getName());
			database.createNewFile();
			Class.forName("org.sqlite.JDBC");
			Connection cn = DriverManager.getConnection("jdbc:sqlite:/"
					+ database.getAbsolutePath(), "", "");
			Statement create = cn.createStatement();
			create.executeUpdate(CREATE_TABLE);
			create.close();

			// getting the information
			Log.i("SQLiteMapper", "getting the original data");
			Session s = Database.session();
			s.beginTransaction();
			List<RoadStrip> l = (List<RoadStrip>) s
					.createCriteria(RoadStrip.class)
					.add(SpatialRestrictions.intersects("way", GeomHelper
							.createRectangle(minLon, minLat, minLon + 0.5,
									minLat + 0.5))).list();
			Database.end(false);
			for (RoadStrip r : l) {
				Statement insert = cn.createStatement();
				insert.executeUpdate("INSERT INTO roadstrips(id, way) VALUES("
						+ r.getId() + ",'" + r.getWay().toString() + "')");
				insert.close();
			}
			cn.close();

			// Create the streams
			Log.i("SQLiteMapper", "compressing file");
			String outFilename = database.getAbsolutePath() + ".gz";
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(
					outFilename));
			FileInputStream in = new FileInputStream(database);

			// Transfer bytes from the input file to the GZIP output stream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();

			// Complete the GZIP file
			out.finish();
			out.close();
		} catch (Exception e) {
			Log.e("SQLiteMapper",
					e.getClass().getSimpleName() + "@run: " + e.getMessage());
		}
	}
}
