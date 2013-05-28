package com.smartpark.background;

import android.content.SharedPreferences;

import com.smartpark.activities.LoginActivity;
import com.smartpark.activities.MainActivity;
import com.smartpark.bluetooth.BlueController;
import com.smartpark.tcp.TCPController;

public class Handler {

	private final boolean D = MainActivity.D;
	private String TAG = "Handler";

	private BackgroundOperationThread bgThread;
	private BlueController btController;
	private TCPController tcpController;
	private SharedPreferences mainPreference;

	// ================================================================
	public Handler(BlueController btController, TCPController tcpController,
			SharedPreferences mainPreference) {
		this.btController = btController;
		this.tcpController = tcpController;
		this.mainPreference = mainPreference;
	}

	// ================================================================
	public void getMessageFromBT(String inData) {
		// String message[] = inData.split(";");

	}

	// ================================================================
	public void getMessageFromTCP(String inData) {
		String message[] = inData.split(";");

		if (message[0].equals("LoginACK")) {
			LoginActivity.setMessage(inData);
		} else if (message[0].equals("ConnectionACK")) {
			// Login the person
			String autoLogin = "AutoLogin;"
					+ mainPreference.getString("ssNbr", "error") + ":"
					+ mainPreference.getBoolean("loginState", false);
			BackgroundOperationThread.sendByTCP(autoLogin);
		}

	}

	// ================================================================
	public void setBackgroundThread(BackgroundOperationThread bgThread) {
		this.bgThread = bgThread;
	}

}
