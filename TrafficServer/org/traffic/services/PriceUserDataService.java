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

import org.hibernate.Session;
import org.traffic.database.Database;
import org.traffic.logging.Log;
import org.traffic.models.traffic.UserData;

/**
 * This service runs over the {@link UserData} and prices each message. This
 * price depends on the position of the message. If it lays in the middle of a
 * road it is weighted with factor 10. If it's near a crossing road it gets
 * factor 1.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 143 $
 */
public class PriceUserDataService extends TimedService {

	/**
	 * Custom-Constructor
	 * 
	 * @param time
	 *            Time to pause between the runs
	 */
	public PriceUserDataService(long time) {
		super(time);
	}

	@Override
	protected void serve() {
		Session s = Database.session();
		s.beginTransaction();

		// prices the userdata
		int updatedLines = s
				.createSQLQuery(
						"UPDATE data.userdata SET factor = CASE "
								+ "WHEN(SELECT Count(r.id) AS count "
								+ "FROM data.roadstrips r "
								+ "WHERE ST_DWithin(userdata.position, r.way, 0.0007)) < 2 THEN 10 "
								+ "ELSE 1 END " + "WHERE factor IS NULL")
				.executeUpdate();
		Log.i("CleanUpDB", updatedLines + " line(s) of UserData updated");
		Database.end(true);
	}

}
