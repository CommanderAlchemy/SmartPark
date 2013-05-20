package database;

public class SmartPark extends Database {
	private String id;
	private String deviceID; // The Device ID
	private String ssNbr; // Connected to a persons.
	private String position;
	private String startStamp;
	private String stopStamp;
	private String licensePlate;
	private String carModel; // Not needed atm, but may be needed in the future

	private static String dbName = "test";
	private static String tblName = "SmartPark";

	/**
	 * Constructor for SmartParkDevice
	 * 
	 * @param deviceID
	 *            long deviceID
	 * @param connectedTo
	 *            long Connected to
	 * @param latitude
	 *            long latitude
	 * @param longitude
	 *            long longitude
	 */
	public SmartPark(String deviceID, String ssNbr, String position,
			String startStamp, String stopStamp, String licensePlate,
			String carModel) {
		super(dbName, tblName + "_" + deviceID);
		this.deviceID = deviceID;
		this.ssNbr = ssNbr;
		this.position = position;
		this.startStamp = startStamp;
		this.stopStamp = stopStamp;
		this.licensePlate = licensePlate;
		this.carModel = carModel;

	}

	/**
	 * Get ID
	 * 
	 * @return
	 */
	public String getID() {
		return id;
	}

	/**
	 * Set ID
	 * 
	 * @param iD
	 */
	public void setID(String id) {
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
		this.deviceID = deviceID;
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
	 * To string method, write out all information of the current object.
	 */
	public String toString() {
		/* @formatter:off */
		String string = "ID: "		+ this.id
				+ " deviceID: "		+ this.deviceID
				+ " ssNbr: "		+ this.ssNbr
				+ " position: "		+ this.position 
				+ " startStamp: " 	+ this.startStamp 
				+ " stopStamp: "	+ this.stopStamp
				+ " licensePlate: " + this.licensePlate
				+ " carModel: " 	+ this.carModel;
		/* @formatter:on */
		return string;
	}

}
