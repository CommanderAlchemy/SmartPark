package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

abstract public class Database {
	private String dbName;
	private Connection obj = null;

	// private static final String create = "CREATE TABLE";

	public Database(String dbName) {
		this.dbName = dbName;
		try {
			Class.forName("org.sqlite.JDBC");
			obj = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".db");
			System.out.println("Opened database succsessfully");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

	}

	public void closeConnection() {
		try {
			this.obj.close();
			obj = null;
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public Connection getConnection() {
		if (obj == null) {
			System.out.println("Creating new connection to " + dbName);
			initConnection();
		}

		return obj;
	}

	public void initConnection() {

	}

	public void setC(Connection c) {
		this.obj = c;
	}

	/**
	 * Available columns in the database.
	 * 
	 * @author commander
	 * 
	 */
	public enum Col {
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
