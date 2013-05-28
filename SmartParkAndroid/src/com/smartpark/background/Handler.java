package com.smartpark.background;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

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
		Log.e(TAG, "--> Received data = " + inData);
		String message[] = inData.split(";");

		if (message[0].equals("LoginACK")) {
			LoginActivity.setMessage(inData);
		} else if (message[0].equals("ConnectionACK")) {
			// Login the person
			String autoLogin = "AutoLogin;"
					+ mainPreference.getString("ssNbr", "error") + ":"
					+ mainPreference.getBoolean("loginState", false);
			BackgroundOperationThread.sendByTCP(autoLogin);
		} else if (message[0].equals("AutoLoginACK")) {
			String[] data = message[1].split(":");

			if (data[0].equals("Accepted")) {
				// ======== ALERTDIALOG START =========================
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						Ref.activeActivity);
				builder1.setTitle("Login Failed");
				builder1.setMessage("Server did not accept previously saved credentials!"
						+ "\n\nPlease try again...");
				builder1.setCancelable(true);
				builder1.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				AlertDialog alert = builder1.create();
				alert.show();
				// ======== ALERTDIALOG END =========================

				Intent i = new Intent(Ref.activeActivity, LoginActivity.class);
				i.putExtra("CancelAllowed", false);
				Ref.activeActivity.startActivityForResult(i,
						MainActivity.REQUEST_LOGIN);
			}
		}

	}

	// ================================================================
	public void setBackgroundThread(BackgroundOperationThread bgThread) {
		this.bgThread = bgThread;
	}

}
