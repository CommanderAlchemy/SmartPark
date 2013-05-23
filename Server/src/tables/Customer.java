package tables;

import java.sql.ResultSet;
import java.sql.Statement;

import database.Database;

public class Customer extends Database {
	private long id;
	private int cont;
	private String ssNbr;
	private String forname;
	private String lastname;
	private String phoneNbr;
	private String address;
	private String password;
	private String smartParkID;
	private String registered;
	private long balance;

	private static String dbName = "test";
	private static String tblName = "Customer";

	private String sql;
	private Statement statement = null;
	private ResultSet result;

	/*
	 * Avail columns in the customer table
	 */
	public enum Col {
		ID, cont, ssNbr, Forname, Lastname, Address, PhoneNbr, Password, SmartparkID, Registered, Balance
	}

	/**
	 * Constructor for Customer
	 * 
	 * @param id
	 *            Long SocialSecurityNumber
	 * @param forename
	 *            String name
	 * @param lastname
	 *            String name
	 */
	/**
	 * Empty Constructor to read customer data from database
	 */
	public Customer() {
		super(dbName);

	}
	public Customer(String ssNbr){
		this();
		selectCustomer(ssNbr);
	}

	/**
	 * Constuctor to read and write customer data to the database
	 * 
	 * @param ssNbr
	 * @param forename
	 * @param lastname
	 * @param adress
	 * @param phoneNbr
	 */
	public Customer(int cont, String ssNbr, String forename, String lastname,
			String address, String phoneNbr, String password,
			String smartParkID, String registered) {
		this();
		this.cont = cont;
		this.ssNbr = ssNbr;
		this.forname = forename;
		this.lastname = lastname;
		this.address = address;
		this.phoneNbr = phoneNbr;
		this.password = password;
		this.smartParkID = smartParkID;
		this.registered = registered;

		/*
		 * Find customer and update DB * IF table does not exist create new
		 * table * IF customer does not exist create new entry
		 */

	}

	public void CreateCustomerTable() {
		try {
			/* @formatter:off */
			sql = "CREATE TABLE " + tblName + " " + "(ID INTEGER PRIMARY KEY,"
					+ "cont					INT			NOT NULL," + "ssNbr				TEXT		NOT NULL,"
					+ "Forname				TEXT		NOT NULL,"
					+ "Lastname				TEXT		NOT NULL,"
					+ "Address         		TEXT		NOT NULL,"
					+ "PhoneNbr       		TEXT		NOT NULL,"
					+ "Password				TEXT		NOT NULL,"
					+ "SmartParkID			TEXT		NOT NULL,"
					+ "Registered			TEXT		NOT NULL," + "Balance				REAL)";
			/* @formatter:on */
			statement = super.getConnection().createStatement();
			statement.executeUpdate(sql);
			statement.close();
			super.closeConnection();
		} catch (Exception e) {
			System.out.println("[ERROR] During Create New Customer Table:");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out
				.println(tblName + " table successfully created in " + dbName);
	}

	public void InsertCustomerData(Customer c) {
		try {
			/* @formatter:off */
			sql = "INSERT INTO Customer "
					+ "(ID,cont,ssNbr,ForName,Lastname,Address,PhoneNbr,Password,SmartParkID,Registered,Balance) "
					+ "VALUES (" + "NULL" + "," + c.cont + ",'" + c.ssNbr
					+ "','" + c.forname + "','" + c.lastname + "','"
					+ c.address + "','" + c.phoneNbr + "','" + c.password
					+ "','" + c.smartParkID + "','" + c.registered + "',"
					+ c.balance + ");";

			/* @formatter:on */
			statement = super.getConnection().createStatement();
			statement.executeUpdate(sql);
			statement.close();
			super.closeConnection();

		} catch (Exception e) {
			System.out
					.println("[ERROR] During Insert New Customer Into Customer Table:");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out.println(c.forname + " sucessfully inserted into " + dbName
				+ "." + tblName);
	}

	public void selectCustomer(String searchValue) {
		try {
			// super.getConnection().setAutoCommit(false);
			statement = super.getConnection().createStatement();

			// result = statement.executeQuery("SELECT * FROM Customer;");
			if (searchValue != null) {
				result = statement
						.executeQuery("SELECT ID,cont,ssNbr,Forname,Lastname,Address,PhoneNbr,Password,SmartParkID,Registered,Balance FROM Customer WHERE ssNbr = '"
								+ searchValue + "';");
			} else {
				result = statement.executeQuery("SELECT * FROM Customer;"
						+ searchValue);
			}

			while (result.next()) {
				this.id = result.getInt("ID");
				this.cont = result.getInt("cont");
				this.ssNbr = result.getString("ssNbr");
				this.forname = result.getString("Forname");
				this.lastname = result.getString("Lastname");
				this.address = result.getString("Address");
				this.phoneNbr = result.getString("PhoneNbr");
				this.password = result.getString("Password");
				this.smartParkID = result.getString("SmartParkID");
				this.registered = result.getString("Registered");
				this.balance = result.getLong("balance");
				System.out.println(this.toString());
			}
			result.close();
			statement.close();
			super.closeConnection();

		} catch (Exception e) {
			System.out.println("[ERROR] During Lookup Table");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Update Customer data in Customer Table if exists
	 * 
	 * @param searchCol
	 *            What Column are you searching after?
	 * @param searchValue
	 *            What value should that column be?
	 * @param whatCol
	 *            What Column do you want to change?
	 * @param whatValue
	 *            What value should that column be?
	 */
	public void UpdateCustomerTable(Col searchCol, String searchValue,
			Col whatCol, String whatValue) {
		try {
			// super.getConnection().setAutoCommit(false);
			statement = super.getConnection().createStatement();

			sql = "UPDATE Customer set " + whatCol + " = '" + whatValue
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
	 * Set Row
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get Row
	 * 
	 * @return
	 */
	public long getId() {
		return id;
	}

	public int getCont() {
		return cont;
	}

	public void setCont(int cont) {
		this.cont = cont;
	}

	/**
	 * Get SocialSecurityNumber
	 * 
	 * @return
	 */
	public void setSsNbr(String ssNbr) {
		this.ssNbr = ssNbr;
	}

	/**
	 * Set SocialSecurityNumber
	 * 
	 * @return
	 */
	public String getSsNbr() {
		return ssNbr;
	}

	/**
	 * Get Forname (String)
	 * 
	 * @return
	 */
	public String getForname() {
		return forname;
	}

	/**
	 * Set Forname
	 * 
	 * @param forname
	 *            String forname
	 */
	public void setForename(String forname) {
		this.forname = forname;
	}

	/**
	 * Get Lastname (String)
	 * 
	 * @return
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * Set Lastname
	 * 
	 * @param lastname
	 *            String lastname
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * Set Password
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get Password
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set SmartParkID
	 * 
	 * @param smartParkID
	 */
	public void setSmartParkID(String smartParkID) {
		this.smartParkID = smartParkID;
	}

	/**
	 * Get SmartParkID
	 * 
	 * @return
	 */
	public String getSmartParkID() {
		return smartParkID;
	}

	/**
	 * Set Register date
	 * 
	 * @param registered
	 */
	public void setRegistered(String registered) {
		this.registered = registered;
	}

	/**
	 * Get Register date
	 * 
	 * @return
	 */
	public String getRegistered() {
		return registered;
	}

	/**
	 * Get Balance
	 * 
	 * @return
	 */
	public long getBalance() {
		return balance;
	}

	/**
	 * Set Balance
	 * 
	 * @param balance
	 */
	public void setBalance(long balance) {
		this.balance = balance;
	}

	/**
	 * Tostring method. Create a string with all the current data in the object.
	 */
	public String toString() {
		/* @formatter:off */
		String string = "ID: " + this.id + " controller: " + this.cont
				+ " ssNbr: " + this.ssNbr + " Name: " + this.forname
				+ " Lastname: " + this.lastname + " Address: " + this.address
				+ " PhoneNbr: " + this.phoneNbr + " Password: " + this.password
				+ " SmartParkID: " + this.smartParkID + " Registered: "
				+ this.registered + " Balance: " + this.balance;
		/* @formatter:on */
		return string;
	}

	public static void main(String[] args) {

		for (String s: args) {
            switch (s) {
			case "CreateTable":
				Customer c = new Customer();
				c.CreateCustomerTable();
				c.InsertCustomerData(new Customer(1, "910611", "Artur", "Olech","Snödroppsgatan3", "0762361910", "artur", "001First", "Today"));
				c.InsertCustomerData(new Customer(0, "820620", "Saeed", "Ghasemi","Hyllie", "0763150074", "saeed", "002Second", "Tomorrow"));
				c.InsertCustomerData(new Customer(0, "na", "Truls", "Haraldsson","Trelleborg", "some number", "truls", "003Third", "Never"));

				break;
			case "Print":
				System.out.println("Printing all customer Tables in Database\n");
				c = new Customer();
				c.selectCustomer(null);
				break;

			default:
				System.out.println("Usage:\nCreateTable: To Create 3 customer default inserts\nPrint: to print all the created tables");
				break;
			}
        }

	}
}