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
import com.smartpark.fragments.UserHistoryFragment;
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
		if (inData.equals("engineOff")) {
			bgThread.startPark("ADT-435", "Renault");
		} else if (inData.equals("engineOn")) {
			bgThread.stopPark("ADT-435", "Renault");
		}
	}

	
	
	
	
	
	
	// ================================================================
	public void getMessageFromTCP(String inData) {
		try{
		Log.e(TAG, "--> Received data = " + inData);
		String message[] = inData.split(";");

		if (message[0].equals("LoginACK")) {
			Log.e(TAG, message[1]);
			LoginActivity.setMessage(inData);
			//========================================
		} else if (message[0].equals("ConnectionACK")) {
			Log.e(TAG, message[1]);
			// Login the person
			String autoLogin = "AutoLogin;"
					+ mainPreference.getString("ssNbr", "error") + ":"
					+ mainPreference.getBoolean("loginState", false);
			BackgroundOperationThread.sendByTCP(autoLogin);
			//========================================
		} else if (message[0].equals("AutoLoginACK")) {
			Log.e(TAG, message[1]);
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
			//========================================
		} else if (message[0].equals("StartParkACK")) {
			// Indicates whether or not we have an ongoing parking sequence
			Log.e(TAG, message[1]);
			// Server responds with parking-lot data and a parkID for this
			// parking sequence			
			if (message[1].equals("ParkingLotNotFound")) {
				// TODO
				BackgroundOperationThread.cancelParkingSequence();
			} else {
				bgThread.setParkingInitiated();
				// "price:QPark:smsQuery:9,18:18,9:55.242342:26.42345:parkID";
				String[] startPark = message[1].split(":");
				
				Editor edit = mainPreference.edit();
				edit.putBoolean("isParking", true);
				edit.putString("price", startPark[0]);
				edit.putString("company", startPark[1]);
				edit.putString("smsQuery", startPark[2]);
				edit.putString("ticketHours", startPark[3]);
				edit.putString("freeHours", startPark[4]);
				edit.putString("longtitude", startPark[5]);
				edit.putString("latitude", startPark[6]);
				edit.putString("parkID", startPark[7]);
				edit.commit();
				
				// TODO
				
			}
			//========================================
		} else if (message[0].equals("StopParkACK")) {
			Log.e(TAG, message[1]);
			if (message[1].equals("true")) { // false means no error
				String stopPark = mainPreference.getString("LastParkingStop",
						"no data");
				if (!stopPark.equals("no data")) {
					BackgroundOperationThread.sendByTCP(stopPark);
					bgThread.setParkingEnded();
				}
			}
			//========================================
		} else if (message[0].equals("HistoryACK")) { // TODO
			Log.e(TAG, message[1]);
			
			if(message[1].equals("endl")){
				UserHistoryFragment.receiveDone();
			}
			
			
		}
		}catch(Exception e){
			Log.w(TAG, "Someone send us crapdata --> error on string.split");
		}

	}

	// ================================================================
	public void setBackgroundThread(BackgroundOperationThread bgThread) {
		this.bgThread = bgThread;
	}

}
