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
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.traffic.database.Database;
import org.traffic.models.traffic.Road;
import org.traffic.models.traffic.RoadStrip;

/**
 * This service tries to calculate a speed limit for street where none is given.
 * It goes to several steps to reach this target:
 * <ul>
 * <li>Finding all {@link RoadStrip} with more than
 * <code>MINIMAL_MESSAGES_TOTAL</code> messages which are documented
 * <code>MINIMAL_DISTANCE</code> degree away from crossroads</li>
 * <li>Counting the messages for different ranges of speed</li>
 * <li>Ignore all ranges with less than <code>MINIMAL_MESSAGES_BORDER</code>
 * messages</li>
 * <li>Checking if the number of messages for a range is about
 * <code>PERCENTAGE_DISTANCE</code> higher</li>
 * <li>Select this range as speed limit</li>
 * <li>Select the lowest value otherwise</li>
 * </ul>
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 143 $
 */
public class UpdateSpeedService extends TimedService {

	/** Number of messages to start the calculation for this road */
	private static final int MINIMAL_MESSAGES_TOTAL = 200;

	/** Distance to crossroads */
	private static final double MINIMAL_DISTANCE = 0.0015;

	/** Number of messages to accept a border */
	private static final int MINIMAL_MESSAGES_BORDER = 100;

	/** Percentage to set a border as speed limit */
	private static final double PERCENTAGE_BORDER = 0.5;

	/** Minimal distance in percentage between to values */
	private static final double PERCENTAGE_DISTANCE = 0.2;

	/**
	 * Custom-Constructor
	 * 
	 * @param time
	 *            Time to pause between the runs
	 */
	public UpdateSpeedService(long time) {
		super(time);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void serve() {
		Session s = Database.session();
		s.beginTransaction();
		// getting all strips with more than MINIMAL_MESSAGES_TOTAL messages
		List<Object[]> strips = (List<Object[]>) s
				.createSQLQuery(
						"SELECT r.id, Count(*) AS count "
								+ "FROM data.roadstrips r "
								+ "INNER JOIN data.userdata u ON u.road_id = r.id "
								+ "WHERE ST_Distance(ST_StartPoint(r.way), u.position) > :distance "
								+ "OR st_distance(ST_endPoint(r.way), u.position) > :distance "
								+ "GROUP BY r.id " + "HAVING Count(*) >= :min")
				.addScalar("id", StandardBasicTypes.INTEGER)
				.addScalar("count", StandardBasicTypes.INTEGER)
				.setParameter("min", MINIMAL_MESSAGES_TOTAL)
				.setParameter("distance", MINIMAL_DISTANCE).list();
		for (Object[] o : strips) {
			int total = (Integer) o[1];

			// getting the messages for each range
			List<Object[]> speeds = (List<Object[]>) s
					.createSQLQuery(
							"SELECT tmp.road_id, tmp.range, Count(*) AS count "
									+ "FROM (SELECT u.road_id, CASE "
									+ "WHEN u.speed BETWEEN 0 AND 20 THEN 10 "
									+ "WHEN u.speed BETWEEN 20 AND 40 THEN 30 "
									+ "WHEN u.speed BETWEEN 40 AND 60 THEN 50 "
									+ "WHEN u.speed BETWEEN 60 AND 85 THEN 70 "
									+ "WHEN u.speed BETWEEN 85 AND 110 THEN 100 "
									+ "ELSE 110 END AS range "
									+ "FROM data.roadstrips r "
									+ "INNER JOIN data.userdata u ON u.road_id = r.id WHERE u.road_id = "
									+ (Integer) o[0]
									+ " AND (ST_Distance(ST_StartPoint(r.way), u.position) > "
									+ MINIMAL_DISTANCE
									+ " OR st_distance(ST_endPoint(r.way), u.position) > "
									+ MINIMAL_DISTANCE + ") ) tmp "
									+ "GROUP BY tmp.road_id, tmp.range")
					.addScalar("road_id", StandardBasicTypes.INTEGER)
					.addScalar("range", StandardBasicTypes.INTEGER)
					.addScalar("count", StandardBasicTypes.INTEGER).list();

			// storing ranges with more than MINIMAL_MESSAGES_BORDER messages
			Map<Integer, Double> amountMessages = new HashMap<Integer, Double>();
			Integer speedlimit = null;
			for (Object[] messages : speeds) {
				int amount = (Integer) messages[2];
				if (amount >= MINIMAL_MESSAGES_BORDER) {
					amountMessages.put((Integer) messages[1],
							(1.0 * amount / total));
					if ((1.0 * amount / total) > PERCENTAGE_BORDER)
						speedlimit = (Integer) messages[1];
				}

			}

			// checking the distance between the measures
			if (speedlimit == null && amountMessages.size() > 1) {
				for (Map.Entry<Integer, Double> entryX : amountMessages
						.entrySet()) {
					boolean check = true;
					for (Map.Entry<Integer, Double> entryY : amountMessages
							.entrySet()) {
						if (entryX.getKey() != entryY.getKey()
								&& entryX.getValue() < (entryY.getValue() + PERCENTAGE_DISTANCE)) {
							check = false;
						}
					}

					if (check) {
						speedlimit = entryX.getKey();
						break;
					}

				}

				// selecting the smallest value
				if (speedlimit == null) {
					speedlimit = Collections.min(amountMessages.keySet());
				}
			}

			// save the value and set the flag
			if (speedlimit != null) {
				RoadStrip rs = (RoadStrip) s.load(RoadStrip.class,
						(Integer) o[0]);
				Road r = rs.getRoad();
				if (r.getCalculated() == null || r.getCalculated()) {
					r.setMaxspeed(speedlimit);
					s.save(r);
				}
			}
		}
		Database.end(true);
	}
}
