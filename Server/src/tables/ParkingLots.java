package tables;

import java.util.ArrayList;

import database.Database;

public class ParkingLots extends Database {

	private long ID;
	private long price;
	private String company;
	private String smsQuery;
	private String ticketHours;
	private String freeHours;
	private ArrayList<String> longitude;
	private ArrayList<String> latitude;

	private static String dbName = "test";
	private static String tblName = "ParkingLots";

	/**
	 * ParkingLots Constructor
	 * @param dbName
	 */
	public ParkingLots(String dbName) {
		super(dbName);
	}

	/**
	 * GetID
	 * @return
	 */
	public long getID() {
		return ID;
	}

	/**
	 * SetID
	 * @param iD
	 */
	public void setID(long iD) {
		ID = iD;
	}

	/**
	 * GetPrice
	 * @return
	 */
	public long getPrice() {
		return price;
	}

	/**
	 * SetPrice
	 * @param price
	 */
	public void setPrice(long price) {
		this.price = price;
	}

	/**
	 * Get Company
	 * @return
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * Set Company
	 * @param company
	 */
	public void setCompany(String company) {
		this.company = company;
	}

	/**
	 * Get SmsQuery
	 * @return
	 */
	public String getSmsQuery() {
		return smsQuery;
	}

	/**
	 * Set SmsQuery
	 * @param smsQuery
	 */
	public void setSmsQuery(String smsQuery) {
		this.smsQuery = smsQuery;
	}

	/**
	 * Get TicketHours
	 * @return
	 */
	public String getTicketHours() {
		return ticketHours;
	}

	/**
	 * set TicketHours
	 * @param ticketHours
	 */
	public void setTicketHours(String ticketHours) {
		this.ticketHours = ticketHours;
	}

	/**
	 * Get FreeHours
	 * @return
	 */
	public String getFreeHours() {
		return freeHours;
	}

	/**
	 * Set FreeHours
	 * @param freeHours
	 */
	public void setFreeHours(String freeHours) {
		this.freeHours = freeHours;
	}

	/**
	 * Get Longitude
	 * @return
	 */
	public ArrayList<String> getLongitude() {
		return longitude;
	}

	/**
	 * Set Longitude
	 * @param longitude
	 */
	public void setLongitude(ArrayList<String> longitude) {
		this.longitude = longitude;
	}

	/**
	 * Get Latitude
	 * @return
	 */
	public ArrayList<String> getLatitude() {
		return latitude;
	}

	/**
	 * SetLatitude
	 * @param latitude
	 */
	public void setLatitude(ArrayList<String> latitude) {
		this.latitude = latitude;
	}

	/**
	 * Get dbName
	 * @return
	 */
	public static String getdbName() {
		return dbName;
	}

	/**
	 * Set dbName
	 * @param dbName
	 */
	public static void setdbName(String dbName) {
		ParkingLots.dbName = dbName;
	}

	/**
	 * Get TblName
	 * @return
	 */
	public static String getTblName() {
		return tblName;
	}

	/**
	 * Set TblName
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
