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
	public static void createTable(String tblName, String[] columns, String[] columnTypes, boolean[] notNull) {
		
		// check for spaces in tablename
		if(tblName.contains(" ")){
			System.out.println("You may not use [space] in table name.");
			return;
		}
		
		// Check for equal string array sizes
		if(columns.length != columnTypes.length || columnTypes.length != notNull.length){
			System.out.println("The arrays are not equa in length");
			return;
		}
		String sql = "CREATE TABLE " + tblName + " (ID INTEGER PRIMARY KEY,";
		
		for(int i = 0 ; i < columns.length ; i++){
			sql += columns[i] + " " + columnTypes[i] + ((notNull[i])?" NOT NULL":"");
			if(i != columns.length -1){
				sql += ",\n";
			}else{
				sql += ")";
			}
		}
		System.out.println(sql);

		
		
		
		
		
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
	
	public static void main(String[] args) {
		Database.createTable();
	}
}
