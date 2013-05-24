package tables;

import java.util.ArrayList;
import java.util.Arrays;

import database.Database;

public class ParkingLots extends Database {

	private long ID;
	private long price;
	private String ticketHours;
	private String freeHours;
	private ArrayList<String> longitude;
	private ArrayList<String> latitude;
	
	private static String dbName = "test";
	private static String tblName = "ParkingLots";
	
	
	public ParkingLots(String dbName) {
		super(dbName);
	}


	public long getID() {
		return ID;
	}

	public long getPrice() {
		return price;
	}


	public void setPrice(long price) {
		this.price = price;
	}


	public String getTicketHours() {
		return ticketHours;
	}


	public void setTicketHours(String ticketHours) {
		this.ticketHours = ticketHours;
	}


	public String getFreeHours() {
		return freeHours;
	}


	public void setFreeHours(String freeHours) {
		this.freeHours = freeHours;
	}


	public ArrayList<String> getLongitude() {
		return longitude;
	}


	public void setLongitude(ArrayList<String> longitude) {
		this.longitude = longitude;
	}


	public ArrayList<String> getLatitude() {
		return latitude;
	}


	public void setLatitude(ArrayList<String> latitude) {
		this.latitude = latitude;
	}


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
