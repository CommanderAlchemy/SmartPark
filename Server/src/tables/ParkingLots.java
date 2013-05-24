package tables;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.Database;

public class ParkingLots extends Database {

	private long ID;
	private long price;
	private String company;
	private String smsQuery;
	private String ticketHours;
	private String freeHours;
	private String longitude;
	private String latitude;
	
	// == Settings for the Table ========================

	private static String dbName = "test";
	private static String tblName = "ParkingLots";
	private static String[] columns = { "price", "company", "smsQuery",
			"ticketHours", "freeHours", "longitude", "latitude"};

	private String[] columnTypes = { "REAL", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT" };

	boolean[] notNull = { true, true, false, true, false, true, true };

	// --------------------------------------------------
	

	/**
	 * ParkingLots Constructor
	 * 
	 * @param dbName
	 */
	public ParkingLots(String dbName) {
		super(dbName);
	}

	public ParkingLots(long price, String company, String smsQuery,
			String ticketHours, String freeHours, String longitude,
			String latitude) {
		super(dbName);
		this.price = price;
		this.company = company;
		this.smsQuery = smsQuery;
		this.ticketHours = ticketHours;
		this.freeHours = freeHours;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public void CreateParkingLotsTable(){
		
	}
	
	public void InsertParkingLotsData(String[] columnData) {

		insertIntoTable(tblName, columns, columnTypes, columnData);

		System.out.println(columnData.toString());
	}
	
	public void selectParkingLots(String searchString, int columnNr) {

		ResultSet result = selectDataFromTable(tblName, columns, searchString,
				columnNr);

		try {
			while (result.next()) {
				this.ID = result.getLong("ID");
				this.price = result.getLong("price");
				this.company = result.getString("company");
				this.smsQuery = result.getString("smsQuery");
				this.ticketHours = result.getString("ticketHours");
				this.freeHours = result.getString("freeHours");
				this.longitude = result.getString("longitude");
				this.latitude = result.getString("latitude");
				System.out.println(this.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * GetID
	 * 
	 * @return
	 */
	public long getID() {
		return ID;
	}

	/**
	 * SetID
	 * 
	 * @param iD
	 */
	public void setID(long iD) {
		ID = iD;
	}

	/**
	 * GetPrice
	 * 
	 * @return
	 */
	public long getPrice() {
		return price;
	}

	/**
	 * SetPrice
	 * 
	 * @param price
	 */
	public void setPrice(long price) {
		this.price = price;
	}

	/**
	 * Get Company
	 * 
	 * @return
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * Set Company
	 * 
	 * @param company
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * Get SmsQuery
	 * 
	 * @return
	 */
	public String getSmsQuery() {
		return smsQuery;
	}

	/**
	 * Set SmsQuery
	 * 
	 * @param smsQuery
	 */
	public void setSmsQuery(String smsQuery) {
		this.smsQuery = smsQuery;
	}

	/**
	 * Get TicketHours
	 * 
	 * @return
	 */
	public String getTicketHours() {
		return ticketHours;
	}

	/**
	 * set TicketHours
	 * 
	 * @param ticketHours
	 */
	public void setTicketHours(String ticketHours) {
		this.ticketHours = ticketHours;
	}

	/**
	 * Get FreeHours
	 * 
	 * @return
	 */
	public String getFreeHours() {
		return freeHours;
	}

	/**
	 * Set FreeHours
	 * 
	 * @param freeHours
	 */
	public void setFreeHours(String freeHours) {
		this.freeHours = freeHours;
	}

	/**
	 * Get Longitude
	 * 
	 * @return
	 */
//	public ArrayList<String> getLongitude() {
//		return longitude;
//	}
//
//	/**
//	 * Set Longitude
//	 * 
//	 * @param longitude
//	 */
//	public void setLongitude(ArrayList<String> longitude) {
//		this.longitude = longitude;
//	}
//
//	/**
//	 * Get Latitude
//	 * 
//	 * @return
//	 */
//	public ArrayList<String> getLatitude() {
//		return latitude;
//	}
//
//	/**
//	 * SetLatitude
//	 * 
//	 * @param latitude
//	 */
//	public void setLatitude(ArrayList<String> latitude) {
//		this.latitude = latitude;
//	}

	/**
	 * Get dbName
	 * 
	 * @return
	 */
	public static String getdbName() {
		return dbName;
	}

	/**
	 * Set dbName
	 * 
	 * @param dbName
	 */
	public static void setdbName(String dbName) {
		ParkingLots.dbName = dbName;
	}

	/**
	 * Get TblName
	 * 
	 * @return
	 */
	public static String getTblName() {
		return tblName;
	}

	/**
	 * Set TblName
	 * 
	 * @param tblName
	 */
	public static void setTblName(String tblName) {
		ParkingLots.tblName = tblName;
	}

	/**
	 * This Table toString method
	 */
	@Override
	public String toString() {
		/* @formatter:off */
		return "ID:"				+ this.ID				 	+ ";"
				+ "deviceID:"		+ this.price			 	+ ";"
				+ "ssNbr:"			+ this.ticketHours			+ ";"
				+ "longitude:"		+ this.freeHours 			+ ";"
				+ "latitude:"		+ this.longitude.toString()	+ ";"
				+ "startStamp:" 	+ this.latitude.toString()	+ ";";
		/* @formatter:on */
	}

}
