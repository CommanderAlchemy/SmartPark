package server;

import java.util.LinkedList;

import tables.Customer;
import tables.ParkingLots;
import tables.SmartPark;
import tables.SmartPark.Col;


public class Handler {

	// Login
	private String ssNbr;
	private boolean passwordAccepted = false;
	private boolean controller = false;

	// Customer
	private Customer customer;

	// SmartParkDevice
	private SmartPark smartpark;
	
	// ParkingLots
	private ParkingLots parkinglots;

	// Server Connection Thread to the client.
	private ClientThread clientThread;

	/**
	 * Constructor
	 * @param clientThread
	 */
	public Handler(ClientThread clientThread) {
		this.clientThread = clientThread;
	}

	/**
	 * Command Handler
	 * @param message
	 */
	public void checkCommand(String message) {
		String inData[] = message.split(";");

		System.out.println("Handler Got This Message:" + message);

		switch (inData[0]) {
		case "Login":

			this.passwordAccepted = login(inData[1]);

			if (passwordAccepted) {
				clientThread.sendMessage("LoginACK;Accepted:" + controller);
			} else {
				clientThread.sendMessage("LoginACK;Denied:false");
			}

			break;
		case "Close Connection":
			clientThread.sendMessage("CloseACK");
			clientThread.closeConnecton();
			break;

		case "Query":
			if (passwordAccepted)
				query(inData[1]);

			break;
			
		case "StartPark":
				// TODO Fix StartPark That will query the database
				// Possibly query the query() method
			break;
			
		case "StopPark":
				// TODO Fix StopPark That will query the database
				// Possibly query the query() method
			break;
			
		case "echo":
			clientThread.sendMessage("echoACK");
			break;
		default:
			break;
		}
	}

	/**
	 * Login Method, checks user and if correct password is supplied.
	 * @param param
	 * @return
	 */
	public boolean login(String param) {
		String[] inputParam = param.split(":");
		this.customer = new Customer(inputParam[0]);

		if (customer.getPassword() != null)
			if (inputParam[1].equals(customer.getPassword())) {
				this.ssNbr = inputParam[0];

				if (customer.isController())
					this.controller = true;
				return true;
			}

		return false;
	}

	/**
	 * Queries the database for information.
	 * @param param
	 */
	public void  query(String searchString) {
		LinkedList<String> resultList = new LinkedList<String>();
		this.smartpark = new SmartPark(customer.getSmartParkID());

		this.smartpark.selectSmartPark(searchString, 4, true);
		resultList = smartpark.getResultList();
		clientThread.sendMessage(resultList.toString());

	}
}
