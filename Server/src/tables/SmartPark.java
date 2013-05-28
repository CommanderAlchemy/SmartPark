package tables;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

import database.Database;

public class SmartPark extends Database {
	private long id;
	private String deviceID; // The Device ID
	private String ssNbr; // Connected to a persons.
	private String longitude;
	private String latitude;
	private String startStamp;
	private String stopStamp;
	private String licensePlate;
	private String carModel; // Not needed atm, but may be needed in the future

	private String sql;
	private Statement statement = null;
	private ResultSet result;

	private LinkedList<String> resultList;

	public enum Col {
		ID, ssNbr, Longitude, Latitude, StartStamp, StopStamp, LicensePlate, CarModel
	}

	// == Settings for the Table ========================

	private static String dbName = "test";
	private static String tblName;
	private static String[] columns = {"ssNbr", "Longitude", "Latitude",
			"StartStamp", "StopStamp", "LicensePlate", "CarModel" };

	private String[] columnTypes = { "TEXT", "TEXT", "TEXT", "TEXT", "TEXT",
			"TEXT", "TEXT" };

	boolean[] notNull = { true, true, true, true, false, true, true };
	

	// --------------------------------------------------


	public SmartPark(String deviceID) {
		super(dbName);
		this.deviceID = deviceID;
		this.tblName = "SmartPark_" + deviceID;
		this.resultList = new LinkedList<String>();
	}

	/**
	 * Constructor for smartpark
	 * 
	 * @param ssNbr
	 * @param position
	 * @param startStamp
	 * @param stopStamp
	 * @param licensePlate
	 * @param carModel
	 */
	public SmartPark(String ssNbr, String longitude, String latitude,
			String startStamp, String stopStamp, String licensePlate,
			String carModel) {
		super(dbName);
		this.ssNbr = ssNbr;
		this.longitude = longitude;
		this.latitude = latitude;
		this.startStamp = startStamp;
		this.stopStamp = stopStamp;
		this.licensePlate = licensePlate;
		this.carModel = carModel;

	}

	public String createSmartParkTable() {
		String error = createTable(tblName, columns, columnTypes, notNull);
		if (error.length() == 0) {
			System.out.println(tblName + " table successfully created in "
					+ dbName);
		}
		return error;
	}

	public void insertSmartParkData(String[] columnData) {
		insertIntoTable(tblName, columns, columnTypes, columnData);
		System.out.println(columnData.toString());
	}
	
	public void commit() {
		String[] columnData = {ssNbr,longitude,latitude,startStamp,stopStamp,licensePlate,carModel};
		insertIntoTable(tblName, columns, columnTypes, columnData);
	}
	
	public void selectSmartPark(String searchString, int columnNr,
			boolean rangeSelection) {

		ResultSet result = selectDataFromTable(tblName, columns, searchString,
				columnNr, rangeSelection);

		
		try {
			while (getResult().next()) {
				this.id = result.getInt("ID");
				this.ssNbr = result.getString("ssNbr");
				this.longitude = result.getString("Longitude");
				this.latitude = result.getString("Latitude");
				this.startStamp = result.getString("StartStamp");
				this.stopStamp = result.getString("StopStamp");
				this.licensePlate = result.getString("LicensePlate");
				this.carModel = result.getString("CarModel");
				resultList.addLast(this.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			getResult().close();
			getStatement().close();
			closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// String tblName, String[] columns,String searchString, int columnNr,
		// boolean rangeSelection

		// try {
		// statement = super.getConnection().createStatement();
		//
		// // result = statement.executeQuery("SELECT * FROM Customer;");
		//
		// if (searchValue != null) {
		// if (!rangeSelection) {
		// result = statement
		// .executeQuery("SELECT ID,ssNbr,Position,StartStamp,StopStamp,LicensePlate,CarModel FROM "
		// + deviceID
		// + " WHERE "
		// + c
		// + " = '" + searchValue + "';");
		// } else {
		// String[] query = null;
		// try {
		// query = searchValue.split(":");
		// } catch (Exception e) {
		// System.out.println("[ERROR] During query split");
		// System.err.println(e.getClass().getName() + ": "
		// + e.getMessage());
		// }
		// result = statement
		// .executeQuery("SELECT ID,ssNbr,Position,StartStamp,StopStamp,LicensePlate,CarModel FROM SmartPark_"
		// + deviceID
		// + " WHERE "
		// + c
		// + " BETWEEN "
		// + query[0]
		// + " AND "
		// + query[1] + ";");
		// }
		// }else {
		// result = statement.executeQuery("SELECT * FROM "
		// + deviceID + ";");
		// }
		//
		// resultList.clear();
		// while (result.next()) {
		// this.id = result.getLong("ID");
		// this.ssNbr = result.getString("ssNbr");
		// this.longitude = result.getString("Longitude");
		// this.latitude = result.getString("latitude");
		// this.startStamp = result.getString("StartStamp");
		// this.stopStamp = result.getString("StopStamp");
		// this.licensePlate = result.getString("LicensePlate");
		// this.carModel = result.getString("CarModel");
		// resultList.addLast(this.toString() + "\n");
		// }
		// System.out.println(resultList.toString());
		// result.close();
		// statement.close();
		// super.closeConnection();
		//
		// } catch (Exception e) {
		// System.out.println("[ERROR] During Lookup Table");
		// System.err.println(e.getClass().getName() + ": " + e.getMessage());
		// }
	}

	/**
	 * Update Customer data in Customer Table if exists
	 * 
	 * @param searchColumn
	 *            What Column are you searching after?
	 * @param searchValue
	 *            What value should that column be?
	 * @param whatColumn
	 *            What Column do you want to change?
	 * @param whatValue
	 *            What value should that column be?
	 */
	public void updateSmartParkData(String searchColumn, String searchValue,
			String whatColumn, String whatValue) {

		updateTableData(tblName, searchColumn, searchValue, whatColumn, whatValue);
	}
	/**
	 * StartParking
	 * @param param
	 */
	public void startParking(String param) {
		String[] inputParam = param.split(":");
		String[] columnData = {ssNbr,longitude,latitude,startStamp,stopStamp,licensePlate,carModel};
		this.id = countRows(tblName);
		this.latitude = inputParam[0];
		this.longitude = inputParam[1];
		selectSmartPark(Long.toString(id),0,false);
		insertIntoTable(tblName, columns, columnTypes, columnData);
	}
	
	/**
	 * StopParking
	 * @param param
	 */
	public void stopParking(String param){
		String [] inputParam = param.split(":");
		this.id = countRows(tblName);
		updateSmartParkData("ID", Long.toString(this.id), "StopStamp", inputParam[1]);
	}

	/**
	 * Get ID
	 * 
	 * @return
	 */
	public long getID() {
		return id;
	}

	/**
	 * Set ID
	 * 
	 * @param iD
	 */
	public void setID(long id) {
		this.id = id;
	}

	/**
	 * Get DeviceID
	 * 
	 * @return
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * Set DeviceID
	 * 
	 * @param deviceID
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = "Smartpark_" + deviceID;
	}

	/**
	 * Get ssNbr
	 * 
	 * @return
	 */
	public String getSsNbr() {
		return ssNbr;
	}

	/**
	 * Set ssNbr
	 * 
	 * @param ssNbr
	 */
	public void setSsNbr(String ssNbr) {
		this.ssNbr = ssNbr;
	}

	/**
	 * Get Longitude position
	 * 
	 * @return
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * Get longitude position
	 * 
	 * @return
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * Set Latitude position
	 * 
	 * @param latitude
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * Get Latitude position
	 * 
	 * @return
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * Get Start Stamp of parking
	 * 
	 * @return
	 */
	public String getStartStamp() {
		return startStamp;
	}

	/**
	 * Set Start Stamp of parking
	 * 
	 * @param startStamp
	 */
	public void setStartStamp(String startStamp) {
		this.startStamp = startStamp;
	}

	/**
	 * Get Stop Stamp of parking
	 * 
	 * @return
	 */
	public String getStopStamp() {
		return stopStamp;
	}

	/**
	 * Set Stop Stamp of parking
	 * 
	 * @param stopStamp
	 */
	public void setStopStamp(String stopStamp) {
		this.stopStamp = stopStamp;
	}

	/**
	 * Get LicensePlate
	 * 
	 * @return
	 */
	public String getLicensePlate() {
		return licensePlate;
	}

	/**
	 * Set LicensePlate
	 * 
	 * @param licensePlate
	 */
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	/**
	 * Get Car Model
	 * 
	 * @return
	 */
	public String getCarModel() {
		return carModel;
	}

	/**
	 * Set Car Model
	 * 
	 * @param carModel
	 */
	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}

	/**
	 * Get results after running the select method
	 * 
	 * @return
	 */
	public LinkedList<String> getResultList() {
		return resultList;
	}

	/**
	 * To string method, write out all information of the current object.
	 */
	public String toString() {
		/* @formatter:off */
		return "ID:"				+ this.id			 	+ ";"
				+ "deviceID:"		+ deviceID 				+ ";"
				+ "ssNbr:"			+ this.ssNbr			+ ";"
				+ "longitude:"		+ this.longitude 		+ ";"
				+ "latitude:"		+ this.latitude			+ ";"
				+ "startStamp:" 	+ this.startStamp  		+ ";"
				+ "stopStamp:"		+ this.stopStamp 		+ ";"
				+ "licensePlate:" 	+ this.licensePlate 	+ ";"
				+ "carModel:" 		+ this.carModel 		+ ";";
		/* @formatter:on */
	}

	public static void main(String[] args) {
//		SmartPark sp = new SmartPark("001First");
//		sp.createSmartParkTable();
//		sp.setSsNbr("910611");
//		sp.setLongitude("longitude");
//		sp.setLatitude("latitude");
//		sp.setStartStamp("start");
//		sp.setStopStamp("stop");
//		sp.setLicensePlate("OPH500");
//		sp.setCarModel("Nissan");
//		sp.updateSmartParkData("ID", "1", "ssNbr", "910611");
		for (String s : args) {
			
		}

	}

}