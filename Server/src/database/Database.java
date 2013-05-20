package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	private String dbName;
	private Connection c = null;

	// private static final String create = "CREATE TABLE";

	public Database(String dbName) {
		this.dbName = dbName;
		initConnection();

	}

	public void closeConnection() {
		try {
			this.c.close();
			c = null;
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public Connection getConnection() {
		if (c == null) {
			System.out.println("Creating new connection to " + dbName);
			initConnection();
		}

		return c;
	}

	public void initConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".db");
			System.out.println("Opened database succsessfully");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void setC(Connection c) {
		this.c = c;
	}
}
