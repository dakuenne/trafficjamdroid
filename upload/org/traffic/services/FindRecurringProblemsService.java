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
package org.traffic.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.AVGMeasurement;
import org.traffic.models.traffic.Problem;
import org.traffic.models.traffic.RoadStrip;
import org.traffic.utils.IConstants;
import org.traffic.utils.SocketCommunicator;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * This service searches the database for recurring problems. Any road with two
 * or more entries is saved as a {@link Problem}. These problems could be
 * grouped by the day of week or the time they occur. If several roads are
 * jammed within a geographic region at the same time they may also be grouped.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 143 $
 */
public class FindRecurringProblemsService extends TimedService {

	/**
	 * Custom-Constructor
	 * 
	 * @param time
	 *            Time to pause between the runs
	 */
	public FindRecurringProblemsService(long time) {
		super(time);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void serve() {
		Session s = Database.session();
		s.beginTransaction();

		Map<AVGMeasurement, Boolean> processed = new HashMap<AVGMeasurement, Boolean>();

		// finding all problems, which are identified almost twice
		List<Object[]> l = (List<Object[]>) s
				.createSQLQuery(
						"SELECT road_id, dow, hour "
								+ "FROM data.avg_speed_per_time "
								+ "WHERE maxspeed * 0.8 > speed "
								+ "GROUP BY road_id, dow, hour "
								+ "HAVING Count(*) > 1")
				.addScalar("road_id", StandardBasicTypes.INTEGER)
				.addScalar("dow", StandardBasicTypes.INTEGER)
				.addScalar("hour", StandardBasicTypes.INTEGER).list();
		for (Object[] o : l) {
			processed.put(new AVGMeasurement((Integer) o[1], (Integer) o[2],
					(Integer) o[0]), false);
		}

		Map<List<Integer>, AVGMeasurement> spaces = new HashMap<List<Integer>, AVGMeasurement>();

		// finding all problems who affect more than one roadstrip
		for (AVGMeasurement a : processed.keySet()) {
			if (processed.get(a)) {
				continue;
			}
			List<Integer> roadstrips = new LinkedList<Integer>();
			RoadStrip r = (RoadStrip) s.load(RoadStrip.class, a.getRoad_id());
			roadstrips.add(a.getRoad_id());
			processed.put(a, true);

			// finding neighbours in a 5 km radius - same dow, same time
			List<Object[]> neighbours = (List<Object[]>) s
					.createSQLQuery(
							"SELECT a.road_id, a.dow, a.hour "
									+ "FROM data.avg_speed_per_time a "
									+ "INNER JOIN data.roadstrips rs ON a.road_id = rs.id "
									+ "WHERE a.maxspeed * 0.8 > a.speed and a.dow = :dow and a.hour = :hour and a.road_id <> :road "
									+ "		and ST_DWithin(rs.way, GeometryFromText('"
									+ r.getWay().toString()
									+ "', 4326), 0.074) "
									+ "GROUP BY a.road_id, a.dow, a.hour "
									+ "HAVING Count(*) > 1")
					.addScalar("road_id", StandardBasicTypes.INTEGER)
					.addScalar("dow", StandardBasicTypes.INTEGER)
					.addScalar("hour", StandardBasicTypes.INTEGER)
					.setParameter("dow", a.getDow())
					.setParameter("hour", a.getHour())
					.setParameter("road", a.getRoad_id()).list();

			// making lists and mark the processed avgs
			for (Object[] o : neighbours) {
				AVGMeasurement avg = new AVGMeasurement((Integer) o[1],
						(Integer) o[2], (Integer) o[0]);
				roadstrips.add(avg.getRoad_id());
				processed.put(avg, true);
			}

			// finding same problem at another dow
			Collections.sort(roadstrips);
			AVGMeasurement done = (AVGMeasurement) spaces.get(roadstrips);
			if (done != null) {
				done.getTmpDows().add(a.getDow());
				spaces.put(roadstrips, done);
			} else {
				spaces.put(roadstrips, a);
			}
		}

		// creating the recognized Problems
		for (Map.Entry<List<Integer>, AVGMeasurement> entry : spaces.entrySet()) {
			Problem p = new Problem(entry.getValue().getHour(), entry.getKey()
					.toString());
			int id = entry.getKey().get(
					(int) (Math.random() * entry.getKey().size()));
			RoadStrip rs = (RoadStrip) s.load(RoadStrip.class, id);
			Coordinate c = rs.getWay().getCoordinateN(
					(int) (Math.random() * rs.getWay().getNumPoints()));
			String cloudmade = IConstants.CM_GEOCODING + c.y + "," + c.x;
			String street = null;
			try {
				// getting the name of an affected street
				JSONObject jAnswer = JSONObject.fromObject(SocketCommunicator
						.getContent(cloudmade, "UTF8"));
				JSONArray jFeatures = jAnswer.getJSONArray("features");
				street = jFeatures.getJSONObject(0).getJSONObject("properties")
						.getString("name");
			} catch (Exception e) {
				Log.e("FindRecurringProblemsService", e.getClass() + "@serve: "
						+ e.getMessage());
			}

			// generating a description and saving the problem
			p.generateDescription(entry.getValue().getAllDows(), street);
			int entries = s.createCriteria(Problem.class)
					.add(Restrictions.eq("hour", p.getHour()))
					.add(Restrictions.eq("regionJSON", p.getRegionJSON()))
					.list().size();
			if (entries == 0) {
				s.save(p);
			}
		}
		Database.end(true);
	}
}
