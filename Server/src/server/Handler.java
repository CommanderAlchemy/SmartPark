package server;

import java.util.LinkedList;

import tables.Customer;
import tables.ParkingLots;
import tables.SmartPark;

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
							+ "AutoLoginACK;Accepted:" + controller);
					this.passwordAccepted = true;
					 clientThread.sendMessage("AutoLoginACK;Accepted:" +
					 controller);
				} else {
					 clientThread.sendMessage("AutoLoginACK;Denied:false");
				}
			} else {
				System.out.println("--> Handler send This Message: "
						+ "AutoLoginACK;Denied:false");
				clientThread.sendMessage("AutoLoginACK;Denied:false");
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

		case "History":
			System.out.println(" --- History --- ");
			
			if (passwordAccepted)
				queryHistory(inData[1]);

			break;

		case "StartPark":
			System.out.println(" --- StartPark --- ");

			if (passwordAccepted) {
				System.out.println("password accepted");
				String[] param = inData[1].split(":");
				// Get longitude and latitude and search for parking
				System.out.println("looking for parkingplace....");
				String parkingLot = new ParkingLots().searchForParking(
						param[1], param[2]);
				if (!parkingLot.equals("ParkingLotNotFound")) {
					this.smartpark = new SmartPark(customer.getSmartParkID());
					String parkID = this.smartpark.startParking(inData[1], parkingLot);
					clientThread.sendMessage("StartParkACK;" + parkingLot
							+ ":" + parkID);
				} else {
					clientThread
							.sendMessage("StartParckACK;ParkingLotNotFound");
				}
			}
			break;

		case "StopPark":
			System.out.println(" --- StopPark --- ");

			if (passwordAccepted) {
				this.smartpark = new SmartPark(customer.getSmartParkID());
				boolean error = smartpark.stopParking(inData[1]);
				clientThread.sendMessage("StopParkACK;" + !error);
			}
			break;
			
		case "DemoPark":
			System.out.println(" --- DEMO StartPark --- ");
			String[] param = inData[1].split(":");
			String parkingLot = new ParkingLots().searchForParking(param[1], param[2]);
			if (!parkingLot.equals("ParkingLotNotFound")) {
				// TODO INSERT LAST PARAM!!!!
				this.smartpark = new SmartPark(param[11]);
				String parkID = this.smartpark.startParking(inData[1], parkingLot);
				clientThread.sendMessage("StartParkACK;" + parkingLot
						+ ":" + parkID);
			}
		break;
		
		case "CheckPark":
			System.out.println("---- Check Parkings----");
			LinkedList<String> resultList = new LinkedList<String>();
			this.smartpark = new SmartPark("001First");
			smartpark.selectSmartPark(null, 0, false);
			if(Double.parseDouble(smartpark.getStopStamp()) > 0){
				resultList.add(smartpark.serialize());
			}
			this.smartpark = new SmartPark("002Second");
			smartpark.selectSmartPark(null, 0, false);
			if(Double.parseDouble(smartpark.getStopStamp()) > 0){
				resultList.add(smartpark.serialize());
			}
			this.smartpark = new SmartPark("003Third");
			smartpark.selectSmartPark(null, 0, false);
			if(Double.parseDouble(smartpark.getStopStamp()) > 0){
				resultList.add(smartpark.serialize());
			}
			this.smartpark = new SmartPark("004Fourth");
			smartpark.selectSmartPark(null, 0, false);
			if(Double.parseDouble(smartpark.getStopStamp()) > 0){
				resultList.add(smartpark.serialize());
			}
			this.smartpark = new SmartPark("005Fith");
			smartpark.selectSmartPark(null, 0, false);
			if(Double.parseDouble(smartpark.getStopStamp()) > 0){
				resultList.add(smartpark.serialize());
			}
			this.smartpark = new SmartPark("006Sixth");
			smartpark.selectSmartPark(null, 0, false);
			if(Double.parseDouble(smartpark.getStopStamp()) > 0){
				resultList.add(smartpark.serialize());
			}
			while (resultList.size() > 0) {
				 clientThread.sendMessage("CheckParkACK;" + resultList.removeFirst());
			}
			clientThread.sendMessage("CheckParkACK;endl");
			
			
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
	public void queryHistory(String param) {
		LinkedList<String> resultList = new LinkedList<String>();
		this.smartpark = new SmartPark(customer.getSmartParkID());
		this.smartpark.selectSmartPark(param, 4, true);
		System.out.println();
		resultList = smartpark.getResultList();

		while (resultList.size() > 0) {
			 clientThread.sendMessage("HistoryACK;" + resultList.removeFirst());
		}
		clientThread.sendMessage("HistoryACK;endl");
	}

	public static void main(String[] args) {
		Handler hand = new Handler(null);
		hand.checkCommand("Login;820620:saeed");
		hand.checkCommand("QueryHistory;");
	}
}
