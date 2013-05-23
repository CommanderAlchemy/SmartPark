package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

abstract public class Database {
	private String dbName;
	private Connection c = null;

	// private static final String create = "CREATE TABLE";
	public Database(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * Initialize a connection to the database
	 */
	public void initConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".db");
			System.out.println("Opened database succsessfully");

		} catch (Exception e) {
			System.out.println("[ERROR] During initConnection():");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Close the connection to the database
	 */
	private void closeConnection() {
		try {
			this.c.close();
			c = null;
		} catch (SQLException e) {
			System.out.println("[ERROR] During closeConnection():");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Get connection to the database
	 * @return
	 */
	private Connection getConnection() {
		if (c == null) {
			System.out.println("Creating new connection to " + dbName);
			initConnection();
		}

		return c;
	}

	/**
	 * Set connection to the database
	 * @param c
	 */
	private void setConnection(Connection c) {
		this.c = c;
	}

	/**
	 * Create table in the database
	 */
	public void createTable(String tblName) {
	}

	/**
	 * Insert data into the table
	 * 
	 * @param obj
	 */
	public void insertIntoTable(Object obj) {
	}

	/**
	 * Select data from the table
	 * 
	 * @param searcCol
	 *            what column in table to search in
	 * @param searchValue
	 *            search value to search for
	 * @param rangeSelection
	 *            select range of columns
	 */
	public void selectDataFromTable(Col searchCol, String searchValue,
			boolean rangeSelection) {
	}

	/**
	 * Update information in table
	 * 
	 * @param searcCol
	 *            what column in table to search in
	 * @param searchValue
	 *            search value to search for
	 * @param whatCol
	 *            whatCol to update
	 * @param whatValue
	 *            what new value
	 */
	public void updateTableData(Col searcCol, String searchValue, Col whatCol,
			String whatValue) {
	}

	/**
	 * Get resultList from previus query
	 * 
	 * @return
	 */
	public LinkedList<String> getResultList() {
		return null;
	}

	/**
	 * set resultList
	 * 
	 * @return
	 */
	public LinkedList<String> setResultList() {
		return null;
	}

	/**
	 * ToString method
	 * 
	 * @return
	 */
	public String toString() {
		return null;
	}
}
