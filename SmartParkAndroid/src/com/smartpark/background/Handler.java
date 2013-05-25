package com.smartpark.background;

import com.smartpark.activities.LoginActivity;

public class Handler {

	
	public void getMessageFromBT(String inData) {
//		String message[] = inData.split(";");
		
		
		
	}

	public void getMessageFromTCP(String inData) {
		String message[] = inData.split(";");
		
		
		if(message[0].equals("LoginACK")){
			LoginActivity.setMessage(inData);
		}
		

		
		
	}

}
