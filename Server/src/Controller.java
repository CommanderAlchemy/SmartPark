
public class Controller {
	
	private String commando = "";
	private String commandoParameters = "";
	private ClientThread clientThread;
	
	public Controller(ClientThread clientThread){
		this.clientThread = clientThread;
	}
	
	public void checkCommand(String message){
		
		commando = message.substring(0, message.indexOf(';'));
		
//		if(message.length() != message.indexOf(";")+1){
			commandoParameters = message.substring(message.indexOf(';')+1);
//		}
		
//		System.out.println(commando);
//		System.out.println(commandoParameters);
		
		switch (commando) {
		case "Login":
//			System.out.println("Switch: " + commando);
//			System.out.println("Switch: " + commandoParameters);
			login(commandoParameters);
			break;
		case "Close Connection":
//			System.out.println("Switch: " + commando);
//			System.out.println("Switch: " + commandoParameters);
			clientThread.closeConnecton();
			break;
		default:
			break;
		}
	}
	
	public boolean login(String commandoParameters){
//		System.out.println("Login: " + commandoParameters);
//		String[] information = commandoParameters.split(":");
//		System.out.println(information[0] + " " + information[1]);
//		if(db.checkUsername(information[0])){
////			System.out.println("Username OK");
//			if(db.checkPassword(information[1])){
////				System.out.println("Login Succes");
//				return true;
//			}
//		} //Skickar userName
		return false;
	}
}
