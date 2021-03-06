package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

abstract public class Database {
	private String dbName;
	private Connection connection;
	protected ResultSet result;
	private Statement statement;

	// private static final String create = "CREATE TABLE";
	protected Database(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * Initialize a connection to the database
	 */
	private void initConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbName
					+ ".db");
			System.out.println("Opened database succsessfully");

		} catch (Exception e) {
			System.out.println("[ERROR] During initConnection():");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Close the connection to the database
	 */
	protected void closeConnection() {
		try {
			this.connection.close();
			connection = null;
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
		if (connection == null) {
			System.out.println("Creating new connection to " + dbName);
			initConnection();
		}

		return connection;
	}

	/**
	 * Set connection to the database
	 * 
	 * @param c
	 */
	private void setConnection(Connection c) {
		this.connection = c;
	}

	protected ResultSet getResult() {
		return result;
	}

	protected void setResult(ResultSet result) {
		this.result = result;
	}

	protected Statement getStatement() {
		return statement;
	}

	protected void setStatement(Statement statement) {
		this.statement = statement;
	}

	/**
	 * Create table in the database
	 * 
	 * @return
	 */
	protected String createTable(String tblName, String[] columns,
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

		System.out.println("\n[SQL Query]		" + sql + "\n");

		String errorStack = "";
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			System.out.println("[ERROR] During create" + tblName + " :");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			errorStack = e.getMessage();
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
	protected void insertIntoTable(String tblName, String[] columns,
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

		System.out.println("\n[SQL Query]		" + sql + "\n");
		;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate(sql);
			statement.close();
			closeConnection();
		} catch (Exception e) {
			System.out.println("[ERROR] During insert" + tblName + " :");
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
	protected ResultSet selectDataFromTable(String tblName, String[] columns,
			String searchString, int columnNr, boolean rangeSelection) {

		try {
			statement = getConnection().createStatement();

			if (searchString != null) {
				if (!rangeSelection) {

					String sql = "SELECT ID,";

					for (int i = 0; i < columns.length; i++) {

						sql += columns[i];

						if (i != columns.length - 1) {
							sql += ",";
						}
					}

					sql += " FROM " + tblName + " WHERE " + columns[columnNr]
							+ " = \"" + searchString + "\";";

					System.out.println("\n[SQL Query]		" + sql + "\n");
					// sql =
					// "SELECT ID,ssNbr,Longitude,Latitude,StartStamp,StopStamp,LicensePlate,CarModel,ParkID FROM SmartPark_001First WHERE ParkID =8;";
					result = statement.executeQuery(sql);

				} else {

					String[] query = null;
					try {
						query = searchString.split(":");
					} catch (Exception e) {
						System.out.println("[ERROR] During query split");
						System.err.println(e.getClass().getName() + ": "
								+ e.getMessage());
					}

					String sql = "SELECT ID, ";

					for (int i = 0; i < columns.length; i++) {

						sql += columns[i];

						if (i != columns.length - 1) {
							sql += ",";
						} else {
							sql += "";
						}
					}

					sql += " FROM " + tblName + " WHERE " + columns[columnNr]
							+ " BETWEEN " + query[0] + " AND " + query[1] + ";";

					System.out.println("\n[SQL Query]		" + sql + "\n");
					result = statement.executeQuery(sql);

				}
			} else {

				System.out.println("\n[SQL Query]		" + "SELECT * FROM "
						+ tblName + ";\n");
				result = statement.executeQuery("SELECT * FROM " + tblName
						+ ";");

			}
		} catch (Exception e) {
			System.out.println("[ERROR] During lookup " + tblName + " :");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
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
	protected void updateTableData(String tblName, String searchColumn,
			String searchValue, String whatColumn, String whatValue) {

		try {
			statement = getConnection().createStatement();

			String sql = "UPDATE " + tblName + " set " + whatColumn + " = '"
					+ whatValue + "' WHERE " + searchColumn + "=\""
					+ searchValue + "\";";

			System.out.println("\n[SQL Query]		" + sql + "\n");
			statement.executeUpdate(sql);
			statement.close();

		} catch (Exception e) {
			System.out.println("[ERROR] During update" + tblName + " :");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			closeConnection();
		}
	}
}
