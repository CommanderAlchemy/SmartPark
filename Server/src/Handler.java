import database.*;

public class Handler {

	private String command = "";
	private String commandParameters = "";
	private ClientThread clientThread;
	
	// Login
	private boolean loggedIn = false;
	// Customer Table
	private Customer customer;

	// private LinkedList<String> buffer = new LinkedList<String>();

	public Handler(ClientThread clientThread) {
		this.clientThread = clientThread;
//		this.customer = new Customer();
	}

	// private void setTask(String task){
	// buffer.addLast(task);
	// }

	public void checkCommand(String message) {
		command = message.substring(0, message.indexOf(';'));

		// if(message.length() != message.indexOf(";")+1){
		commandParameters = message.substring(message.indexOf(';') + 1);
		// }

		System.out.println(command);
		System.out.println(commandParameters);

		switch (command) {
		case "Login":
			System.out.println("command: " + command);
			System.out.println("Parameters: " + commandParameters);
			this.loggedIn = login(commandParameters);
			break;
		case "Close Connection":
			System.out.println("command: " + command);
			System.out.println("Parameters: " + commandParameters);
			clientThread.closeConnecton();
			break;
		default:
			break;
		}
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public boolean getLoggedIn() {
		return this.loggedIn;
	}

	// Login;
	public boolean login(String param) {
		String[] inputParam = param.split(":");
		this.customer = new Customer(inputParam[0]);
		
		if(customer.getPassword() != null)
			if(inputParam.equals(customer.getPassword()))
					return true;
			
		return false;
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
