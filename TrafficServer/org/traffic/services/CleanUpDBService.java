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

import java.util.Date;

import org.hibernate.Session;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.Congestion;
import org.traffic.models.traffic.UserData;

/**
 * This service checks the {@link UserData} and the {@link Congestion}to delete
 * invalid entries. Additionally all expired Clients are deleted.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 */
public class CleanUpDBService extends TimedService {

	/**
	 * Custom-Constructor
	 * 
	 * @param time
	 *            Time to pause between the runs
	 */
	public CleanUpDBService(long time) {
		super(time);
	}

	/**
	 * Checks the <code>UserData</code> and the <code>Congestions</code> to
	 * delete invalid entries. Additionally all expired Clients are deleted.
	 */
	@Override
	protected void serve() {
		Session s = Database.session();
		s.beginTransaction();
		int deletedLines = s.createQuery(
				"DELETE UserData u WHERE u.road_id IS NULL").executeUpdate();
		Log.i("CleanUpDB", deletedLines + " line(s) of UserData deleted");
		int deletedCongestions = s
				.createQuery(
						"DELETE Congestion c WHERE c.reportingtime < :date")
				.setDate("date",
						new Date(System.currentTimeMillis() - 86399000))
				.executeUpdate();
		Log.i("CleanUpDB", deletedCongestions
				+ " line(s) of Congestion deleted");
		int deletedClients = s
				.createQuery("DELETE Client c WHERE c.lease < :date")
				.setParameter("date", new Date(System.currentTimeMillis()))
				.executeUpdate();
		Log.i("CleanUpDB", deletedClients + " Clients deleted");
		int deletedRoutes = s
				.createQuery(
						"DELETE Route r WHERE not exists ( from Client c WHERE c.route = r.id)")
				.executeUpdate();
		Log.i("CleanUpDB", deletedRoutes + " Routes deleted");
		Database.end(true);

	}

}
