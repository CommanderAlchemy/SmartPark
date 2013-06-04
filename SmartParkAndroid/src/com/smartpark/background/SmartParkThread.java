package com.smartpark.background;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.smartpark.activities.MainActivity;
import com.smartpark.bluetooth.BlueController;
import com.smartpark.fragments.UserSmartParkFragment;
import com.smartpark.tcp.TCPController;

public class SmartParkThread extends Thread {

	private static boolean D = MainActivity.D;
	private static final String TAG = "SmartParkThread";

	private boolean run = true;

	private HashMap<String, View> viewReferences;
	private UserSmartParkFragment userSmartParkFragment;
	private SharedPreferences mainPreference;

	public SmartParkThread(HashMap<String, View> viewReferences,
			UserSmartParkFragment userSmartParkFragment) {
		this.viewReferences = viewReferences;
		this.userSmartParkFragment = userSmartParkFragment;
	}

	public SmartParkThread(HashMap<String, View> viewReferences,
			UserSmartParkFragment userSmartParkFragment,
			SharedPreferences mainPreference) {
		this.viewReferences = viewReferences;
		this.userSmartParkFragment = userSmartParkFragment;
		this.mainPreference = mainPreference;

	}

	@Override
	public void run() {
		TCPController tcpController = BackgroundOperationThread
				.getTCPReference();

		while (run) {
			if (BlueController.isConnected()) {
				Ref.activeActivity.runOnUiThread(new Runnable() {
					public void run() {
						userSmartParkFragment.setLblBTText("BT(X)");
					}
				});
			} else {
				Ref.activeActivity.runOnUiThread(new Runnable() {
					public void run() {
						userSmartParkFragment.setLblBTText("BT( )");
					}
				});
			}

			if (tcpController.isConnected()) {
				Ref.activeActivity.runOnUiThread(new Runnable() {
					public void run() {
						userSmartParkFragment.setLblTCPText("TCP(X)");
					}
				});
			} else {
				Ref.activeActivity.runOnUiThread(new Runnable() {
					public void run() {
						userSmartParkFragment.setLblTCPText("TCP( )");
					}
				});
			}
			
//			System.out.println(BackgroundOperationThread.isParkingLotdataReceived());
//			System.out.println(BackgroundOperationThread.isParking());
			
			if (BackgroundOperationThread.isParkingLotdataReceived()) {
				Ref.activeActivity.runOnUiThread(new Runnable() {
					public void run() {

						userSmartParkFragment.setbtnParkText("Stop parking...");
						BackgroundOperationThread.sendByBT("a");
					}
				});
			} else {
				Ref.activeActivity.runOnUiThread(new Runnable() {
					public void run() {
						userSmartParkFragment.setbtnParkText("Park");
						BackgroundOperationThread.sendByBT("b");
					}
				});
			}

			if (BackgroundOperationThread.isParking()) {

				// StartPark;ssNbr:55.3452324:26.3423423:2342133424:0:ADT-435:Renault:0:0
				// StopPark;ssNbr:55.3452324:26.3423423:2342133424:2342143424:ADT-435:Renault:price:parkID
				// "price:QPark:smsQuery:9,18:18,9:55.242342:26.42345:parkID";

				Ref.activeActivity.runOnUiThread(new Runnable() {
					long duration;
					private double parkingPrice;
					float totalThisMonth;

					public void run() {

						Timestamp timestamp = new Timestamp(Long.parseLong(BackgroundOperationThread.startParkString[3]));
						Date date = new Date(timestamp.getTime());

						// S is the millisecond
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd' 'HH:MM:ss:S");

						System.out.println(simpleDateFormat.format(timestamp));
						System.out.println(simpleDateFormat.format(date));

						userSmartParkFragment
								.setLblParkedSinceShowText(simpleDateFormat.format(timestamp.getTime()));

						Calendar cal = Calendar.getInstance();
						duration = cal.getTimeInMillis()
								- Long.parseLong(BackgroundOperationThread.startParkString[3]);
						userSmartParkFragment
								.setLblDurationShowText(userSmartParkFragment
										.convertMilisToTime(duration));

						userSmartParkFragment
								.setlblPriceShowText(BackgroundOperationThread.parkingLot[0]
										+ " SEK/h");

						userSmartParkFragment
								.setlblTicketHoursShowText(BackgroundOperationThread.parkingLot[3]);

						userSmartParkFragment
								.setlblFreeHoursShowText(BackgroundOperationThread.parkingLot[4]);

						parkingPrice = Double
								.parseDouble(BackgroundOperationThread.parkingLot[0])
								* duration / 3600000;
						userSmartParkFragment.setlblPriceTillNowShowText(Double
								.toString(Math.round(parkingPrice)));

						totalThisMonth = mainPreference.getFloat(
								"totalThisMonth", 0);
						mainPreference.edit().putFloat("totalThisMonth",
								(float) (totalThisMonth + parkingPrice)).apply();
						userSmartParkFragment.setlblTotalPriceShowText(Float
								.toString(mainPreference.getFloat(
										"totalThisMonth", 0)));

					}
				});

			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Log.w(TAG, "Error in sleep2: ", e);
			}
		}
	}

	public void stopThread() {
		run = false;
	}

}
