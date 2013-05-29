package com.smartpark.background;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
		if(inData.equals("engineOff")){
			bgThread.startPark("ADT-435", "Renault");
		}else if(inData.equals("engineOn")){
			bgThread.stopPark("ADT-435", "Renault");
		}
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
		} else if (message[0].equals("StartParkACK")) {
			if (message[1].length() > 0) {
				mainPreference.edit().putString("parkID", message[1]);
			}
			// Example string received
			String parkeringLot = "20:QPark:smsQuery:9,18:18,9:55.242342:26.42345:parkID";
			String[] park = parkeringLot.split(":");

			Editor edit = mainPreference.edit();
			edit.putBoolean("isParking", true);
			edit.putString("price", park[0]);
			edit.putString("company", park[1]);
			edit.putString("smsQuery", park[2]);
			edit.putString("ticketHours", park[3]);
			edit.putString("freeHours", park[4]);
			edit.putString("longtitude", park[5]);
			edit.putString("latitude", park[6]);
			edit.putString("parkID", park[7]);
			edit.commit();
			// TODO send parkinLot data to the

		}

	}

	// ================================================================
	public void setBackgroundThread(BackgroundOperationThread bgThread) {
		this.bgThread = bgThread;
	}

}
