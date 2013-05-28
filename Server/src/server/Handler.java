package server;

import java.rmi.server.LoaderHandler;
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
	 * 
	 * @param clientThread
	 */
	public Handler(ClientThread clientThread) {
		this.clientThread = clientThread;
		clientThread.sendMessage("ConnectionACK;0:0");
		System.out.println("--> Handler send This Message: "
				+ "ConnectionACK;0:0");
	}

	/**
	 * Command Handler
	 * 
	 * @param message
	 */
	public void checkCommand(String message) {
		String inData[] = message.split(";");

		System.out.println("<-- Handler Got This Message:" + message);

		switch (inData[0]) {
		case "AutoLogin":
			System.out.println(" --- AutoLogin --- ");
			String data[] = inData[1].split(":");
			if (data[1].equals("true")) {
				if (autoLogin(data[0])) {
					System.out.println("--> Handler send This Message: "
							+ "LoginACK;Accepted:" + controller);
					clientThread.sendMessage("LoginACK;Accepted:" + controller);
				} else {
					clientThread.sendMessage("LoginACK;Denied:false");
				}
			} else {
				System.out.println("--> Handler send This Message: "
						+ "LoginACK;Denied:false");
				clientThread.sendMessage("LoginACK;Denied:false");
			}

			break;
		case "Login":
			System.out.println(" --- Login --- ");
			this.passwordAccepted = login(inData[1]);

			if (passwordAccepted) {
				System.out.println("--> Handler send This Message: "
						+ "LoginACK;Accepted:" + controller);
				clientThread.sendMessage("LoginACK;Accepted:" + controller);
			} else {
				System.out.println("--> Handler send This Message: "
						+ "LoginACK;Denied:false");
				clientThread.sendMessage("LoginACK;Denied:false");
			}

			break;
		case "Close Connection":
			System.out.println(" --- Close Connection --- ");

			clientThread.sendMessage("CloseACK");
			clientThread.closeConnecton();
			break;

		case "QueryHistory":
			System.out.println(" --- QueryHistory --- ");

			if (passwordAccepted)
				queryHistory(inData[1]);

			break;

		case "StartPark":
			System.out.println(" --- StartPark --- ");

			if (passwordAccepted) {
				this.smartpark = new SmartPark(customer.getSmartParkID());
				smartpark.startParking(inData[1]);
			}
			// TODO Fix StartPark That will query the database
			// Possibly query the query() method
			break;

		case "StopPark":
			System.out.println(" --- StopPark --- ");

			if (passwordAccepted) {
				this.smartpark = new SmartPark(customer.getSmartParkID());
				smartpark.stopParking(inData[1]);
			}
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

	public boolean autoLogin(String param) {
		if (!param.equals("error")) {
			this.customer = new Customer(param);

			if (customer.getPassword() != null) {
				if (customer.isController())
					this.controller = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Login Method, checks user and if correct password is supplied.
	 * 
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
	 * 
	 * @param param
	 */
	public void queryHistory(String searchString) {
		LinkedList<String> resultList = new LinkedList<String>();
		this.smartpark = new SmartPark(customer.getSmartParkID());

		this.smartpark.selectSmartPark(searchString, 4, true);
		resultList = smartpark.getResultList();
		clientThread.sendMessage(resultList.toString());
	}

	public void calculateRange() {

	}
}
