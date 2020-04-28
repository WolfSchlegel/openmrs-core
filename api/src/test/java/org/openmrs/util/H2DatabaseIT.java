/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.h2.jdbc.JdbcSQLNonTransientException;
import org.junit.After;
import org.junit.Before;
import org.openmrs.liquibase.LiquibaseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2DatabaseIT implements LiquibaseProvider {
	
	private static final Logger log = LoggerFactory.getLogger(H2DatabaseIT.class);
	
	private static final String CONTEXT = "some context";
	
	protected static final String USER_NAME = "another_user";
	
	protected static final String PASSWORD = "another_password";
	
	private Connection connection;
	
	@Before
	public void setup() throws SQLException {
		this.initializeDatabase();
	}
	
	@After
	public void tearDown() throws SQLException {
		this.dropAllDatabaseObjects();
	}
	
	public Liquibase getLiquibase(String filename) throws LiquibaseException {
		Database liquibaseConnection = DatabaseFactory.getInstance()
		        .findCorrectDatabaseImplementation(new JdbcConnection(connection));
		liquibaseConnection.setDatabaseChangeLogTableName("LIQUIBASECHANGELOG");
		liquibaseConnection.setDatabaseChangeLogLockTableName("LIQUIBASECHANGELOGLOCK");
		
		return new Liquibase(filename, new ClassLoaderResourceAccessor(getClass().getClassLoader()), liquibaseConnection);
	}
	
	protected void initializeDatabase() throws SQLException {
		String driver = "org.h2.Driver";
		try {
			Class.forName(driver);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		connection = DriverManager.getConnection("jdbc:h2:mem:openmrs;DB_CLOSE_DELAY=-1", USER_NAME, PASSWORD);
		connection.setAutoCommit(false);
	}
	
	protected void updateDatabase(String filename) throws SQLException, LiquibaseException {
		Liquibase liquibase = getLiquibase(filename);
		liquibase.update(new Contexts(CONTEXT));
		connection.commit();
	}
	
	protected void dropAllDatabaseObjects() throws SQLException {
		Statement statement = null;
		try {
			statement = connection.createStatement();
			String query = "DROP ALL OBJECTS";
			statement.execute(query);
		}
		catch (JdbcSQLNonTransientException e) {
			log.info("connection is already closed, most likely a test method already dropped all database objects");
		}
		finally {
			connection.close();
		}
	}
}
