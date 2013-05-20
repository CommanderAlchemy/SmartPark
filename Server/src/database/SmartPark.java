package database;

public class SmartPark extends Database {
	private long deviceID; // The Device ID
	private long connecteTo; // Connected to what personID

	// Current Position
	private long latitude;
	private long longitude;

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
	public SmartPark(long deviceID, long connectedTo, long latitude,
			long longitude) {
		//TODO Fix this.
		super(null,null);
		this.deviceID = deviceID;
		this.connecteTo = connectedTo;
		this.latitude = latitude;
		this.longitude = longitude;

	}

	/**
	 * Get Device ID
	 * 
	 * @return long deviceID
	 */
	public long getDeviceID() {
		return deviceID;
	}

	/**
	 * Set DeviceID
	 * 
	 * @param deviceID
	 *            long deviceID
	 */
	public void setDeviceID(long deviceID) {
		this.deviceID = deviceID;
	}

	/**
	 * Get ConnectedTo Returns the owner of the SmartParkDevice
	 * 
	 * @return
	 */
	public long getConnectedTo() {
		return connecteTo;
	}

	/**
	 * Set ConnectedTo Sets the owner of the SmartParkDevice
	 * 
	 * @param connecteTo
	 */
	public void setConnecteTo(long connecteTo) {
		this.connecteTo = connecteTo;
	}

	/**
	 * Get Latitude get the current latitude position (long)
	 * 
	 * @return long
	 */
	public long getLatitude() {
		return latitude;
	}

	/**
	 * Set Latitude position
	 * 
	 * @param latitude
	 */
	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

	/**
	 * Get Longitude position (long)
	 * 
	 * @return
	 */
	public long getLongitude() {
		return longitude;
	}

	/**
	 * set Longitude position
	 * 
	 * @param longitude
	 *            long
	 */
	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	public String toString() {
		return "SmartPark";
	}

}
