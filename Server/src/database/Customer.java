package database;

import java.sql.ResultSet;
import java.sql.Statement;

public class Customer extends Database {
	private long id;
	private long ssNbr;
	private long balance;
	private String forname;
	private String lastname;
	private String phoneNbr;
	private String adress;
	private String password;

	private static String dbName = "test";
	private static String tblName = "Customer";

	private String sql;
	private Statement statement = null;
	private ResultSet result;
	
	/*
	 * Avail columns in the customer table
	 */
	public enum Col{
		ID,ssNbr,Forname,Lastname,Address,PhoneNbr,Password,Balance,SmartparkID
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
		super(dbName, tblName);

	}

	/**
	 * Constuctor to read and write customer data to the database
	 * @param ssNbr
	 * @param forename
	 * @param lastname
	 * @param adress
	 * @param phoneNbr
	 */
	public Customer(long ssNbr, String forename, String lastname,
			String adress, String phoneNbr) {
		super(dbName, tblName);
		this.ssNbr = ssNbr;
		this.forname = forename;
		this.lastname = lastname;
		this.adress = adress;
		this.phoneNbr = phoneNbr;

		/*
		 * Find customer and update DB * IF table does not exist create new
		 * table * IF customer does not exist create new entry
		 */

	}

	/*@formatter:off*/
	public void CreateCustomerTable(){
		try{
			sql = "CREATE TABLE " + tblName + " " +
					"(ID INTEGER PRIMARY KEY," +
					"ssNbr			REAL		NOT NULL," +
					"Forname		TEXT		NOT NULL," +
					"Lastname		TEXT		NOT NULL," +
					"Adress         TEXT		NOT NULL," +
					"PhoneNbr       TEXT		NOT NULL," +
					"Password		TEXT		NOT NULL," +
					"Balance		REAL)";
			
			statement = super.getConnection().createStatement();
			statement.executeUpdate(sql);
			statement.close();
			super.closeConnection();
		} catch (Exception e) {
			System.out.println("[ERROR] During Create New Customer Table:");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out.println(tblName + " table successfully created in " + dbName);
	}

	public void InsertCustomerTable(Customer c) {
		try {
			sql = "INSERT INTO Customer "
					+ "(ID,ssNbr,ForName,Lastname,Adress,PhoneNbr,Password,Balance) "
					+ "VALUES (" + "NULL" + "," + 
					c.ssNbr + "," + 
					"'" + c.forname + "'," + 
					"'" + c.lastname + "'," + 
					"'" + c.adress + "'," + 
					"'" + c.phoneNbr + "'," + 
					// TODO fix this part here
					c.balance + "," +
					"'" + c.password + "');";

			super.getConnection().setAutoCommit(false);
			statement = super.getConnection().createStatement();
			statement.executeUpdate(sql);
			super.getConnection().commit();
			statement.close();
			super.closeConnection();
		} catch (Exception e) {
			System.out.println("[ERROR] During Insert New Customer Into Customer Table:");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out.println(c.forname + " sucessfully inserted into "+ dbName + "." + tblName);
	}
	private void selectCustomer(String searchValue) {
		try{
			super.getConnection().setAutoCommit(false);
			statement = super.getConnection().createStatement();
//			result = statement.executeQuery("SELECT * FROM Customer;");
			//TODO insert password statement.
			result = statement.executeQuery("SELECT ID,ssNbr,Forname,Lastname,Adress,PhoneNbr,balance FROM Customer WHERE ID = 3");
			while (result.next()){
				System.out.println(result.getRow());
				this.id = result.getInt("ID");
				this.ssNbr = result.getLong("ssNbr");
				this.forname = result.getString("Forname");
				this.lastname = result.getString("Lastname");
				this.adress = result.getString("Adress");
				this.phoneNbr = result.getString("PhoneNbr");
//				this.password = result.getString("Password);
				this.balance = result.getLong("balance");
			}
			System.out.println(this.toString());
			result.close();
			statement.close();
			super.closeConnection();
		
		}catch(Exception e){
			System.out.println("[ERROR] During Lookup Table");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	/**
	 * Update Customer data in Customer Table if exists
	 * @param searchCol What Column are you searching after?
	 * @param searchValue What value should that column be?
	 * @param whatCol What Column do you want to change?
	 * @param whatValue What value should that column be?
	 */
	public void UpdateCustomerTable(Col searchCol, String searchValue, Col whatCol, String whatValue){
		try {
			super.getConnection().setAutoCommit(false);
			statement = super.getConnection().createStatement();
			
			sql = "UPDATE Customer set " + whatCol + " = '"+ whatValue +"' where "+searchCol+"="+ searchValue +";";
			System.out.println(sql);
			statement.executeUpdate(sql);
			
			super.getConnection().commit();
			
			
		} catch (Exception e) {
			System.out.println("[ERROR] During update customer table :");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	/*@formatter:on*/

	/**
	 * Get Row 
	 * 
	 * @return
	 */
	public long getId() {
		return id;
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
	 * Get SocialSecurityNumber
	 * 
	 * @return
	 */
	public void setSsNbr(long ssNbr) {
		this.ssNbr = ssNbr;
	}

	/**
	 * Set SocialSecurityNumber
	 * 
	 * @return
	 */
	public long getSsNbr() {
		return ssNbr;
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
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Get Password
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Tostring method.
	 * Create a string with all the current data in the object.
	 */
	public String toString() {
		String string = "ID:" + this.id + " ssNbr:" + this.ssNbr + " Name:"
				+ this.forname + " Lastname:" + this.lastname + " Adress:"
				+ this.adress + " PhoneNbr:" + this.phoneNbr + " Balance:"
				+ this.balance;
		return string;
	}

	public static void main(String[] args) {
		Customer c = new Customer();
		
//		c.CreateCustomerTable();
//		c.InsertCustomerTable(new Customer(666, "forname", "lastname", "Snödroppsgatan3", "0762361910"));
//		c.InsertCustomerTable(new Customer(910611, "Artur", "Olech", "Snödroppsgatan3", "0762361910"));
//		c.InsertCustomerTable(new Customer(910611, "Truls", "Haraldsson", "Snödroppsgatan3", "1762361910"));
//		c.selectCustomer(null);
//		c.UpdateCustomerTable(Col.ID, "1", Col.balance, "666");
		c.selectCustomer(null);
	}
}
