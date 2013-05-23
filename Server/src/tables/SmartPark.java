package tables;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

import database.Database;

public class SmartPark extends Database {
	private long id;
	private static String deviceID; // The Device ID
	private String ssNbr; // Connected to a persons.
	private String position;
	private String startStamp;
	private String stopStamp;
	private String licensePlate;
	private String carModel; // Not needed atm, but may be needed in the future

	private final static String dbName = "test";
	private String tblName;
	
	private String sql;
	private Statement statement = null;
	private ResultSet result;
	
	private LinkedList<String> resultList;
	
	public enum Col {
		ID, ssNbr, Position, StartStamp, StopStamp, LicensePlate, CarModel
	}
	
	public SmartPark(String deviceID){
		super(dbName);
		SmartPark.deviceID = deviceID;
		this.tblName = "SmartPark_" + deviceID;
		
		this.resultList = new LinkedList<String>();
	}

	/**
	 * Constructor for smartpark
	 * @param ssNbr
	 * @param position
	 * @param startStamp
	 * @param stopStamp
	 * @param licensePlate
	 * @param carModel
	 */
	public SmartPark(String ssNbr, String position,
			String startStamp, String stopStamp, String licensePlate,
			String carModel) {
		super(dbName);
		this.ssNbr = ssNbr;
		this.position = position;
		this.startStamp = startStamp;
		this.stopStamp = stopStamp;
		this.licensePlate = licensePlate;
		this.carModel = carModel;

	}
	public void CreateSmartParkTable() {
		try {
			System.out.println("Create " + tblName);
			/* @formatter:off */
			sql = "CREATE TABLE " + tblName + " " 
					+ "(ID INTEGER PRIMARY KEY,"
					+ "ssNbr				TEXT		NOT NULL," 
					+ "Position				TEXT		NOT NULL,"
					+ "StartStamp			REAL		NOT NULL,"
					+ "StopStamp       		REAL				,"
					+ "LicensePlate    		TEXT		NOT NULL,"
					+ "CarModel				TEXT		NOT NULL)";
			/* @formatter:on */
			statement = super.getConnection().createStatement();
			statement.executeUpdate(sql);
			statement.close();
			super.closeConnection();
		} catch (Exception e) {
			System.out.println("[ERROR] During Create New SmartPark Table:");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out
				.println(tblName + " table successfully created in " + dbName);
	}

	public void InsertSmartParkData(SmartPark sp) {
		try {
			/* @formatter:off */
			sql = "INSERT INTO SmartPark_" + SmartPark.deviceID + " "
					+ "(ID,ssNbr,Position,StartStamp,StopStamp,LicensePlate,CarModel) "
					+ "VALUES (" + "NULL"			+ ",'"
								 + sp.ssNbr 		+ "','"
								 + sp.position 		+ "','"
								 + sp.startStamp 	+ "','"
								 + sp.stopStamp		+ "','"
								 + sp.licensePlate	+ "','"
								 + sp.carModel		+ "');";

			/* @formatter:on */
			statement = super.getConnection().createStatement();
			statement.executeUpdate(sql);
			statement.close();
			super.closeConnection();

		} catch (Exception e) {
			System.out
					.println("[ERROR] During Insert New Device Into SmartPark Table:");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		System.out.println(SmartPark.deviceID + " sucessfully inserted into " + dbName
				+ "." + tblName);
	}

	public void selectSmartPark( Col c, String searchValue, boolean rangeSelection) {
		try {
			statement = super.getConnection().createStatement();

			// result = statement.executeQuery("SELECT * FROM Customer;");
			
			if (searchValue != null){
				if(!rangeSelection){
					result = statement
							.executeQuery("SELECT ID,ssNbr,Position,StartStamp,StopStamp,LicensePlate,CarModel FROM SmartPark_" 
									+ SmartPark.deviceID + " WHERE " + c  + " = '"	+ searchValue + "';" );
				}else{
					String[] query = null;
					try{
						query = searchValue.split(":");
					}catch(Exception e){
						System.out.println("[ERROR] During query split");
						System.err.println(e.getClass().getName() + ": " + e.getMessage());
					}
//					System.out.println("SELECT ID,ssNbr,Position,StartStamp,StopStamp,LicensePlate,CarModel FROM SmartPark_" 
//							+ SmartPark.deviceID + " WHERE " + c + " BETWEEN " + query[0] + " AND " + query[1] + ";");
					result = statement.executeQuery("SELECT ID,ssNbr,Position,StartStamp,StopStamp,LicensePlate,CarModel FROM SmartPark_" 
							+ SmartPark.deviceID + " WHERE " + c + " BETWEEN " + query[0] + " AND " + query[1] + ";");
				}
				
			}
			
			else{
				result = statement
						.executeQuery("SELECT * FROM SmartPark_" + SmartPark.deviceID + ";");
			}
				
			resultList.clear();
			while (result.next()) {
				this.id 			= result.getLong("ID");
				this.ssNbr 			= result.getString("ssNbr");
				this.position 		= result.getString("Position");
				this.startStamp 	= result.getString("StartStamp");
				this.stopStamp 		= result.getString("StopStamp");
				this.licensePlate	= result.getString("LicensePlate");
				this.carModel 		= result.getString("CarModel");
				resultList.addLast(this.toString()+"\n");
			}
			System.out.println(resultList.toString());
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
	public void UpdateSmartParkData(Col searchCol, String searchValue,
			Col whatCol, String whatValue) {
		try {
			// super.getConnection().setAutoCommit(false);
			statement = super.getConnection().createStatement();

			sql = "UPDATE SmartPark-" + deviceID + " set " + whatCol + " = '" + whatValue
					+ "' where " + searchCol + "=" + searchValue + ";";
			System.out.println(sql);
			statement.executeUpdate(sql);
			// super.getConnection().commit();

		} catch (Exception e) {
			System.out.println("[ERROR] During update SmartPark table :");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
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
		SmartPark.deviceID = deviceID;
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
	 * Get Position(Long/Lat)
	 * 
	 * @return
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * Set Position (Long/Lat)
	 * 
	 * @param position
	 */
	public void setPosition(String position) {
		this.position = position;
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
		String string = "ID:"				+ this.id			 	+ ";"
						+ "deviceID:"		+ SmartPark.deviceID 	+ ";"
						+ "ssNbr:"			+ this.ssNbr			+ ";"
						+ "position:"		+ this.position 		+ ";"
						+ "startStamp:" 	+ this.startStamp  		+ ";"
						+ "stopStamp:"		+ this.stopStamp 		+ ";"
						+ "licensePlate:" 	+ this.licensePlate 	+ ";"
						+ "carModel:" 		+ this.carModel 		+ ";";
		/* @formatter:on */
		return string;
	}
	public static void main(String[] args) {
		for (String s: args) {
            switch (s) {
			case "CreateTable":
				SmartPark sp = new SmartPark("001First");
				sp.CreateSmartParkTable();
				sp.InsertSmartParkData(new SmartPark("2000", "Long/Lat", "1", "10", "OPH500", "Nissan"));
				sp.InsertSmartParkData(new SmartPark("2001", "Long/Lat", "3", "1", "MRO500", "Opel"));
				sp.InsertSmartParkData(new SmartPark("2003", "Long/Lat", "1", "null", "TTY500", "Toyota"));
				
				SmartPark sp0 = new SmartPark("002Second");
				sp0.CreateSmartParkTable();
				sp0.InsertSmartParkData(new SmartPark("2004", "Long/Lat", "2", "3", "TTY600", "Lexus"));
				sp0.InsertSmartParkData(new SmartPark("2005", "Long/Lat", "1", "null", "TTY800", "Mercedes"));
				sp0.InsertSmartParkData(new SmartPark("2006", "Long/Lat", "5", "5", "TTY900", "Kia"));
				
				SmartPark spnull = new SmartPark("000Null");
				spnull.CreateSmartParkTable();
				spnull.InsertSmartParkData(new SmartPark("1987", "Long/Lat", "4", "10", "MRO519", "Toyota Celica"));

				break;
			case "Print":
				System.out.println("Printing all SmartPark Tables in Database\n");
				new SmartPark("001First").selectSmartPark(null, null, false);
				System.out.println();
				new SmartPark("002Second").selectSmartPark(null, null, false);
				System.out.println();
				new SmartPark("000Null").selectSmartPark(null, null, false);
				break;
				
			default:
				System.out.println("Usage:\nCreateTable: To Create 3 SmartPark default tables\nPrint: to print all the created tables");
				break;
			}
        }

	}

}
