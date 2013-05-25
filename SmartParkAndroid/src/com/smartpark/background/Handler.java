package com.smartpark.background;

import android.util.Log;

import com.smartpark.activities.LoginActivity;

public class Handler {

	private String TAG = "Handler";

	public void getMessageFromBT(String inData) {
//		String message[] = inData.split(";");

		
		
	}

	public void getMessageFromTCP(String inData) {
		String message[] = inData.split(";");

		if (message[0].equals("LoginACK")) {
//			synchronized (UserLoginTask log) {
//				Log.e("HANDLER", "------Waiting before Wait");
//				notifyAll();
//				Log.e("HANDLER", "----Waiting");
//				LoginActivity.setMessage(inData);
//			}
			Log.e(TAG , "----- before setting message");
			LoginActivity.setMessage(inData);
			Log.e(TAG , "----- after setting");


		}
	}
	
	
}
