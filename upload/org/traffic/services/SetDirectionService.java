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

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.RoadStrip;
import org.traffic.models.traffic.UserData;

/**
 * This service loads all {@link UserData} for which the driving direction is
 * not set. It searches the previous entry and calculates the direction by
 * different comparisons.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class SetDirectionService extends TimedService {

	/**
	 * Custom-Constructor
	 * 
	 * @param time
	 *            Time to pause between the runs
	 */
	public SetDirectionService(long time) {
		super(time);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void serve() {
		Session s = Database.session();
		s.beginTransaction();
		// loading all data without driving direction
		List<UserData> lst = (List<UserData>) s
				.createCriteria(UserData.class)
				.add(Restrictions.or(Restrictions.isNull("to_start"),
						Restrictions.isNull("to_end"))).list();
		for (UserData data : lst) {
			if (data.getRoad_id() == null) {
				continue;
			}
			
			// searching previous message
			RoadStrip rs = (RoadStrip) s.load(RoadStrip.class,
					data.getRoad_id());
			List<UserData> lstPrev = (List<UserData>) s
					.createCriteria(UserData.class)
					.add(Restrictions.lt("time", data.getTime()))
					.addOrder(Order.desc("time")).setMaxResults(1).list();
			for (UserData prev : lstPrev) {
				
				// previous message on the same road
				if (prev.getRoad_id() == data.getRoad_id()) {
					double dist = data.getPosition().distance(
							rs.getWay().getStartPoint());
					double distPrev = prev.getPosition().distance(
							rs.getWay().getStartPoint());
					if (dist > distPrev) {
						data.setTo_end(true);
						data.setTo_start(false);
					} else {
						data.setTo_end(false);
						data.setTo_start(true);
					}
				} else {
					// previous message on another road
					double distStartPrev = prev.getPosition().distance(
							rs.getWay().getStartPoint());
					double distEndPrev = prev.getPosition().distance(
							rs.getWay().getEndPoint());
					if (distStartPrev > distEndPrev) {
						data.setTo_end(false);
						data.setTo_start(true);
					} else {
						data.setTo_end(true);
						data.setTo_start(false);
					}
				}
			}
			if (lstPrev.size() == 0) {
				data.setTo_end(true);
				data.setTo_start(true);
			}
		}
		Database.end(true);
		Log.i("SetDirections", lst.size()
				+ " line(s) of UserData with directions refreshed");
	}
}
