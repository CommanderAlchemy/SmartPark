package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import tables.SmartPark;
import tables.Customer.Col;

public class Database {
	private String dbName;
	private Connection c = null;

	// private static final String create = "CREATE TABLE";
	public Database(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * Initialize a connection to the database
	 */
	private void initConnection() {
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
	 * 
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
	 * 
	 * @param c
	 */
	private void setConnection(Connection c) {
		this.c = c;
	}

	/**
	 * Create table in the database
	 * 
	 * @return
	 */
	public String createTable(String tblName, String[] columns,
			String[] columnTypes, boolean[] notNull) {

		// check for spaces in tablename
		if (tblName.contains(" ")) {
			System.out.println("You may not use [space] in table name.");
			return "You may not use [space] in table name.";
		}
		// Check for equal string array sizes
		if (columns.length != columnTypes.length
				|| columnTypes.length != notNull.length) {
			System.out.println("The arrays are not equa in length");
			return "The arrays are not equa in length";
		}
		String sql = "CREATE TABLE " + tblName + " (ID INTEGER PRIMARY KEY,";

		for (int i = 0; i < columns.length; i++) {
			sql += columns[i] + " " + columnTypes[i]
					+ ((notNull[i]) ? " NOT NULL" : "");
			if (i != columns.length - 1) {
				sql += ",";
			} else {
				sql += ")";
			}
		}
		String errorStack = "";
		try {
			Statement s = getConnection().createStatement();
			s.executeUpdate(sql);
			s.close();
		} catch (SQLException e1) {
			System.out.println("[ERROR] During Create New Customer Table:");
			System.err
					.println(e1.getClass().getName() + ": " + e1.getMessage());
			errorStack = e1.getMessage();
		} finally {
			closeConnection();
		}
		return errorStack;
	}

	/**
	 * Insert
	 * 
	 * @param tblName
	 * @param columns
	 * @param columnTypes
	 * @param columnData
	 */
	public void insertIntoTable(String tblName, String[] columns,
			String[] columnTypes, String[] columnData) {
		// check for spaces in tablename
		if (tblName.contains(" ")) {
			System.out.println("You may not use [space] in table name.");
			return;
		}
		// Check for equal string array sizes
		if (columns.length != columnData.length) {
			System.out.println("The arrays are not equa in length");
			return;
		}

		String sql = "INSERT INTO " + tblName + " (ID,";

		for (int i = 0; i < columns.length; i++) {
			sql += columns[i];
			if (i != columns.length - 1) {
				sql += ",";
			} else {
				sql += ") ";
			}
		}
		sql += "VALUES (NULL,";

		for (int i = 0; i < columnData.length; i++) {
			if (columnTypes[i].equals("TEXT")) {
				sql += "'";
			}
			sql += columnData[i];
			if (columnTypes[i].equals("TEXT")) {
				sql += "'";
			}

			if (i != columns.length - 1) {
				sql += ",";
			} else {
				sql += ");";
			}
		}
		try {
			Statement statement = getConnection().createStatement();
			statement.executeUpdate(sql);
			statement.close();
			closeConnection();
		} catch (Exception e) {
			System.out
					.println("[ERROR] During Insert New Customer Into Customer Table:");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Select data from the table
	 * 
	 * @param columnNr
	 *            what column in table to search in
	 * @param searchString
	 *            search value to search for
	 * @param rangeSelection
	 *            select range of columns
	 * @param tblName
	 *            The table name
	 * @param columns
	 *            An array of columns that the table is made of
	 */
	public ResultSet selectDataFromTable(String tblName, String[] columns, String searchString, int columnNr, boolean rangeSelection) {

		Statement statement = null;
		ResultSet result = null;
		try {
			// super.getConnection().setAutoCommit(false);
			statement = getConnection().createStatement();

			if (searchString != null) {
				if (!rangeSelection) {
					
					String sql = "SELECT ID,";

					for (int i = 0; i < columns.length; i++) {

						sql += columns[i];

						if (i != columns.length - 1) {
							sql += ",";
						} else {
							sql += "";
						}
					}

					sql += " FROM " + tblName + " WHERE " + columns[columnNr]
							+ " = " + searchString + ";";

					System.out.println(sql);

					result = statement.executeQuery(sql);

					// ("SELECT ID,cont,ssNbr,Forname,Lastname,Address,PhoneNbr,Password,SmartParkID,Registered,Balance FROM Customer WHERE ssNbr = '"
					// + searchString + "';");

					
					
				} else {

					
					
					String[] query = null;
					try {
						query = searchString.split(":");
					} catch (Exception e) {
						System.out.println("[ERROR] During query split");
						System.err.println(e.getClass().getName() + ": "
								+ e.getMessage());
					}
					result = statement
							.executeQuery("SELECT ID,ssNbr,Position,StartStamp,StopStamp,LicensePlate,CarModel FROM "
									+ tblName
									+ " WHERE "
									+ c
									+ " BETWEEN "
									+ query[0] + " AND " + query[1] + ";");

				}
			} else {
				// result = statement.executeQuery("SELECT * FROM Customer;"
				// + searchValue);
				result = statement.executeQuery("SELECT * FROM " + tblName
						+ ";");
			}
		} catch (Exception e) {
			System.out.println("[ERROR] During Lookup Table");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

		try {
			result.close();
			statement.close();
			closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Update Customer data in Customer Table if exists. This method first finds
	 * a searchValue in the searchCol you specify and then changes the value in
	 * the whatCol you have specified to whatValue.
	 * 
	 * @param searchCol
	 *            What Column are you searching for?
	 * @param searchValue
	 *            What value should that column contain?
	 * @param whatCol
	 *            What Column do you want to change?
	 * @param whatValue
	 *            What value should that column be?
	 */
	public void updateTableData(String searchCol, String searchValue,
			String whatCol, String whatValue) {

		try {
			// super.getConnection().setAutoCommit(false);
			Statement statement = getConnection().createStatement();

			String sql = "UPDATE Customer set " + whatCol + " = '" + whatValue
					+ "' where " + searchCol + "=" + searchValue + ";";

			System.out.println(sql);
			statement.executeUpdate(sql);
			// super.getConnection().commit();

		} catch (Exception e) {
			System.out.println("[ERROR] During update customer table :");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
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

		String dbName = "test";
		String tblName = "Customer";
		String[] columns = { "cont", "ssNbr", "Forname", "Lastname", "Address",
				"PhoneNbr", "Password", "SmartParkID", "RegistrationDate",
				"Balance" };

		String[] columnTypes = { "INT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT",
				"TEXT", "TEXT", "TEXT", "REAL" };

		boolean[] notNull = { true, true, true, true, true, true, true, true,
				true, false };
		Database j = new Database(dbName);
		j.selectDataFromTable(tblName, columns, "'juhu'", 2, false);
		
	}
}
