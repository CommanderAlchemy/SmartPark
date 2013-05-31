
package tables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

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
	private LinkedList<String> resultList;

	// == Settings for the Table ========================

	private static String dbName = "test";
	private static String tblName = "ParkingLots";
	private static String[] columns = { "price", "company", "smsQuery",
			"ticketHours", "freeHours", "longitude", "latitude" };

	private String[] columnTypes = { "REAL", "TEXT", "TEXT", "TEXT", "TEXT",
			"TEXT", "TEXT" };

	boolean[] notNull = { true, true, false, true, false, true, true };

	// --------------------------------------------------

	/**
	 * ParkingLots Constructor
	 * 
	 * @param dbName
	 */
	public ParkingLots() {
		super(dbName);
		this.resultList = new LinkedList<String>();
	}

	/**
	 * 
	 * @param price
	 * @param company
	 * @param smsQuery
	 * @param ticketHours
	 * @param freeHours
	 * @param longitude
	 * @param latitude
	 */
	public ParkingLots(long price, String company, String smsQuery,
			String ticketHours, String freeHours, String longitude,
			String latitude) {
		this();
		this.price = price;
		this.company = company;
		this.smsQuery = smsQuery;
		this.ticketHours = ticketHours;
		this.freeHours = freeHours;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	/**
	 * Create ParkingLots Table
	 */
	public String CreateParkingLotsTable() {
		String error = createTable(tblName, columns, columnTypes, notNull);
		if (error.length() == 0) {
			System.out.println(tblName + " table successfully created in "
					+ dbName);
		}
		return error;
	}

	public void commit() {
		String[] columnData = { Long.toString(price), company, smsQuery,
				ticketHours, freeHours, longitude, latitude };
		insertIntoTable(tblName, columns, columnTypes, columnData);
	}

	/**
	 * Insert Data Into Table
	 * 
	 * @param columnData
	 */
	public void InsertParkingLotsData(String[] columnData) {

		insertIntoTable(tblName, columns, columnTypes, columnData);

		System.out.println(columnData.toString());
	}

	/**
	 * 
	 * @param searchString
	 * @param columnNr
	 * @return
	 */
	public void selectParkingLots(String searchString, int columnNr,
			boolean rangeSelection) {

		ResultSet result = selectDataFromTable(tblName, columns, searchString,
				columnNr, rangeSelection);

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
				System.out.println("[RESULT: ParkingLots]		" + this.toString());
				resultList.addLast(this.serialize());
			}
			System.out.println();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String searchForParking(String latitude, String longitude) {
		double[] range;
		int radious = 6371;
		double distance = -1;

		System.out.println("PARKINGLOTS, long= " + longitude + "lat= " + latitude);
		double inputlongitude = Double.parseDouble(longitude);
		double inputlatitude = Double.parseDouble(latitude);
		
		// select the whole database over parkinglots
		// create an array search the array and find a matching parkinglot
		// return a string in format xx:xx:xx:xx
		// latitute - lotLatitude

		/*
		 * var R = 6371; // km var d = Math.acos(Math.sin(lat1)*Math.sin(lat2) +
		 * Math.cos(lat1)*Math.cos(lat2) * Math.cos(lon2-lon1)) * R;
		 */
		
		selectParkingLots(null, 0, false);
		range = new double[resultList.size()];
		String parkinglong;
		String parkinglat;
		int i = 0;
		System.out.println(resultList.getFirst());
		for (String s : resultList) {
			
			String[] stringArray = s.split(":");
			parkinglong = stringArray[6];//TODO 5
			parkinglat = stringArray[5];//TODO 6
			System.out.println("fejl 3");
			System.out.println("Long:" + parkinglong);
			System.out.println("Lat:" + parkinglat);
		
			distance = Math.acos(Math.sin(Math.toRadians(inputlatitude)) * 
					Math.sin(Math.toRadians(Double.parseDouble(parkinglat)))	+ 
							Math.cos(Math.toRadians(inputlatitude))	* 
							Math.cos(Math.toRadians(Double.parseDouble(parkinglat)))	* 
									Math.cos(Math.toRadians(Double.parseDouble(parkinglong)) - Math.toRadians(inputlongitude)))* 
									radious * 1000;
			
			range[i] = distance;
			i ++;
		}
		
		int index = (int) getMin(range);
		
		if(index == -1)
			return "ParkingLotNotFound";
		
		System.out.println("THIS WAS THE RANGE: " + range[index]);
		if(range[index] <= 100){
			return resultList.get(index);
		}
		
		
		return "ParkingLotNotFound";
	}

	public double getMin(double[] array) {
		if (array.length > 0) {
			int index = 0;
			for (int i = 0; i < array.length; i++) {
				if (array[index] > array[i]) {
					index = i;
				}
			}
			return index;
		}
		return -1;
	}

	/**
	 * UpdateParkingLotsData
	 * 
	 * @param searchCol
	 * @param searchValue
	 * @param whatCol
	 * @param whatValue
	 */
	public void updateParkingLotsData(String searchCol, String searchValue,
			String whatCol, String whatValue) {
		updateTableData(tblName, searchCol, searchValue, whatCol, whatValue);
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
				+ "Price: "			+ this.price			 	+ ";"
				+ "Company: "		+ this.company				+ ";"
				+ "SMSQuery: "		+ this.smsQuery				+ ":"
				+ "TicketHours: "	+ this.ticketHours			+ ";"
				+ "Freehours: "		+ this.freeHours 			+ ";"
				+ "Longitude: "		+ this.longitude.toString()	+ ";"
				+ "Latitude: " 		+ this.latitude.toString()	+ ";";
		
	}

	public String serialize() {
		return this.price + ":" 
				+ this.company + ":" 
				+ this.smsQuery + ":" 
				+ this.ticketHours + ":" 
				+ this.freeHours + ":" 
				+ this.longitude + ":" 
				+ this.latitude; 
	}
		/* @formatter:on */
	
	public static void main(String[] args) {
//		 ParkingLots pl = new ParkingLots(25, "MAH", "0762361910:Hello", "08-18",
//		 "18-08", "55.61619893", "12.98591274");
//		 ParkingLots p2 = new ParkingLots(25, "Kranen", "0762361910:Kranen", "08-18",
//				 "18-08", "55.615371", "12.98500");
//		 
//		 ParkingLots p3 = new ParkingLots(25, "Ubåten", "0762361910:Ubåten", "08-18",
//				 "18-08", "55.614971", "12.98526");
//		 pl.CreateParkingLotsTable();
//		 pl.commit();
//		 
//		 p2.CreateParkingLotsTable();
//		 p2.commit();
//		 
//		 p3.CreateParkingLotsTable();
//		 p3.commit();
		new ParkingLots().selectParkingLots(null, 0, false);
	}

}
