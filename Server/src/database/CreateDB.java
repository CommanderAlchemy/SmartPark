package database;

import java.sql.*;

public class CreateDB {
	private String dbName = null;
	private Connection c = null;
	private Statement statement = null;
	private static final String create = "CREATE TABLE";
	
	public CreateDB(String name, Object obj) {
		this.dbName = name;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".db");
			System.out.println("Opened database succsessfully");
			
			statement = c.createStatement();
			String sql = create + " " + obj.toString() + " ";
			
			statement.executeUpdate(sql);
			statement.close();
			c.close();
			System.out.println("Table created successfully");
				
			
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

	}

	public static void main(String[] args) {
		// Test Driver and create an empty database.
//		CreateDB db = new CreateDB("test", new Customer(910611,"Artur","Olech"));

	}
}
