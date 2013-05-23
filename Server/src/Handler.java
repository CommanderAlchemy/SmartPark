import database.*;
import database.SmartPark.Col;

public class Handler {

	private String command = "";
	private String commandParameters = "";
	private ClientThread clientThread;

	// Login
	private String ssNbr;
	private boolean passwordAccepted = false;
	private boolean controller = false;
	
	// Customer Table
	private Customer customer;
	
	// SmartParkDevice Table
	private SmartPark smartpark;
	
	private String[] inputParam;

	// private LinkedList<String> buffer = new LinkedList<String>();

	public Handler(ClientThread clientThread) {
		this.clientThread = clientThread;
		// this.customer = new Customer();
	}

	// private void setTask(String task){
	// buffer.addLast(task);
	// }

	public void checkCommand(String message) {
		String inData[] = message.split(";");
		
		System.out.println("Handler Got This Message:" + message);
		System.out.println(command);
		System.out.println(commandParameters);
		
		switch (inData[0]) {
		case "Login":

			this.passwordAccepted = login(inData[1]);

			if (passwordAccepted){
				clientThread.sendMessage("LoginACK;Accepted:" + controller);
			}else{
				clientThread.sendMessage("LoginACK;Denied:false");
			}

			break;
		case "Close Connection":
			clientThread.sendMessage("CloseACK");
			clientThread.closeConnecton();
			break;

		case "Query":
			if (passwordAccepted)
				query(message);

			break;
		case "echo":
			clientThread.sendMessage("echoACK");
			break;
		default:
			break;
		}
	}

	public void setPasswordAccepted(boolean passwordAccepted) {
		this.passwordAccepted = passwordAccepted;
	}

	public boolean getPasswordAccepted() {
		return this.passwordAccepted;
	}

	// Login;
	public boolean login(String param) {
		inputParam = param.split(":");
		this.customer = new Customer(inputParam[0]);
		
		if (customer.getPassword() != null)
			if (inputParam[1].equals(customer.getPassword())) {
				this.ssNbr = inputParam[0];
				
				if (customer.getCont() == 1)
					this.controller = true;
				return true;
			}

		return false;
	}

	public String query(String param) {
		inputParam = param.split(":");
		this.smartpark = new SmartPark(customer.getSmartParkID());
		smartpark.selectSmartPark(this.smartpark, Col.StopStamp,param,true);
		
		return null;
	}

	// public boolean login(String commandoParameters){
	// System.out.println("Login: " + commandoParameters);
	// String[] information = commandoParameters.split(":");
	// System.out.println(information[0] + " " + information[1]);
	// if(db.checkUsername(information[0])){
	// // System.out.println("Username OK");
	// if(db.checkPassword(information[1])){
	// // System.out.println("Login Succes");
	// return true;
	// }
	// } //Skickar userName
	// return false;
	// }
}
