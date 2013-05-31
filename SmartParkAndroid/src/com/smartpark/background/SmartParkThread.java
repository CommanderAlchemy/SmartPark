package com.smartpark.background;

import java.util.HashMap;

import com.smartpark.activities.MainActivity;
import com.smartpark.fragments.UserSmartParkFragment;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SmartParkThread extends Thread {
	
	private static boolean D = MainActivity.D;
	private static final String TAG = "SmartParkThread";

	private boolean run = true;
	
	private Button btnPark;
	private TextView lblCurrentTime;
	private TextView lblGPS;
	private TextView lblBT;
	private TextView lblParkedSinceShow;
	private TextView lblDurationShow;
	private TextView lblPriceNowShow;
	private TextView lblFreeTimeShow;
	private TextView lblHoursShow;
	private TextView lblPriceShow;
	private TextView lblTotalPriceShow;
	
	private HashMap<String, View> viewReferences;
	private UserSmartParkFragment userSmartParkFragment;
	private SharedPreferences mainPreference;
	
	
	
	public SmartParkThread(HashMap<String, View> viewReferences, UserSmartParkFragment userSmartParkFragment){
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
		
		
		

		while (run) {
			//TODO set the clock here
			//TODO set the GPS and BT states here
			
			if(BackgroundOperationThread.isParkingLotdataReceived()){
				userSmartParkFragment.setBtnParkTest("Stop parking...");
			}
			
			
			
			
			if (BackgroundOperationThread.isParking()) {
				Log.e(TAG, "parking");
				
				// TODO check to see if the parking-sequence have been aborted

				String parking = mainPreference.getString("StartPark", "0");
				String[] current_parking = parking.split(";")[1].split(":");
				// StartPark;xxxxxx:55.3452324:26.3423423:2342133424:0:ADT-435:Renault:0
				
				// "price:QPark:smsQuery:9,18:18,9:55.242342:26.42345:parkID";
				BackgroundOperationThread.parkingLot = message[1].split(":");
				

				String parkedSince = userSmartParkFragment.convertMilisToTime(current_parking[3]);
				String duration = "dfsfs";
				String price = "dfsfs";
				String ticketTime = "dfsfs";
				String freeTime = "dfsfs";
				String priceTillNow = "";
				String totalThisMonth = mainPreference.getString(
						"totalThisMonth", "0");

				// ((TextView) viewReferences.get()
				// .setText(screenStrings[i]);

			} else {
//				Log.e(TAG, "not parking");
				
				
				userSmartParkFragment.setBtnParkTest("Park");

				// ((TextView) viewReferences.get("lblTotalPriceShow"))
				// .setText(mainPreference.getString(
				// "MonthlyTotal", "0"));

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
