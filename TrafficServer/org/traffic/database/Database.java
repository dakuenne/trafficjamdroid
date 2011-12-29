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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * This class provides a bunch of methods to interact with the database. It sets
 * up the connection with a given configuration and allows the administration of
 * {@link Session} and {@link org.hibernate.Transaction}.
 * 
 * @author Daniel Kuenne
 * @version $LastChangedRevision: 220 $
 * @see SessionFactory
 * @see Session
 * @see org.hibernate.Transaction
 * @see Configuration
 */
public class Database {

	/** The factory to create new sessions */
	private static SessionFactory sessionFactory = null;

	/** The active configuration */
	private static Configuration hibernate_config;

	/**
	 * Initializes the database-connection, loads the configuration-files and
	 * creates the session-factory.
	 * 
	 * @throws IllegalStateException
	 *             The database is already initialized
	 */
	public static void initialize() throws IllegalStateException {
		if (isInitialized())
			throw new IllegalStateException("Database was already initialized");

		File config_file = new File("res/hibernate.cfg.xml");
		sessionFactory = buildSessionFactory(config_file);
	}

	/**
	 * Checks whether the connection is initialized or not.
	 * 
	 * @return The status of the initialization
	 */
	public static boolean isInitialized() {
		return hibernate_config != null;
	}

	/**
	 * Resets the connection to the default-values.
	 */
	public static void reset() {
		if (hibernate_config == null)
			throw new ExceptionInInitializerError(
					"No config present, cant reset!");

		SchemaExport ex = new SchemaExport(hibernate_config);
		ex.create(false, true);
	}

	/**
	 * Creates the session-factory with the given configuration.
	 * 
	 * @param config_file
	 *            The file containing the configuration
	 * @return The {@link SessionFactory}
	 */
	protected static SessionFactory buildSessionFactory(File config_file) {
		try {
			hibernate_config = new Configuration().configure(config_file);
			return hibernate_config.buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed. " + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Returns the current session-factory.
	 * 
	 * @return The {@link SessionFactory}
	 */
	private static SessionFactory getSessionFactory() {
		if (!isInitialized())
			initialize();

		return sessionFactory;
	}

	/**
	 * Returns the current Session.
	 * 
	 * @return The {@link Session}
	 */
	public static Session session() {
		return getSessionFactory().getCurrentSession();
	}

	/**
	 * Starts a new {@link org.hibernate.Transaction} within the current
	 * {@link Session}.
	 */
	public static void begin() {
		getSessionFactory().getCurrentSession().beginTransaction();
	}

	/**
	 * Ends an open {@link org.hibernate.Transaction}.
	 * 
	 * @param commit
	 *            Flag to perform a commit or a rollback
	 */
	public static void end(boolean commit) {
		if (commit)
			commit();
		else
			rollback();
	}

	/**
	 * Revokes all changes and performs a rollback on the
	 * {@link org.hibernate.Transaction}.
	 */
	public static void rollback() {
		getSessionFactory().getCurrentSession().getTransaction().rollback();
	}

	/**
	 * Performs a commit on the {@link org.hibernate.Transaction}.
	 */
	public static void commit() {
		getSessionFactory().getCurrentSession().getTransaction().commit();
	}

	/**
	 * Opens a new {@link Session}.
	 * 
	 * @return The new {@link Session}
	 */
	public static Session openNewSession() {
		return getSessionFactory().openSession();
	}
}