package com.smartpark.background;

import java.util.HashMap;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.smartpark.fragments.UserHistoryFragment;
import com.smartpark.fragments.UserSmartParkFragment;

public class HistoryThread extends Thread{
	
	private boolean run = true;
	private boolean running = false;
	private HashMap<String, View> viewReferences;
	private UserHistoryFragment userSmartParkFragment;
	private SharedPreferences mainPreference;
	private String TAG = "HistoryThread";

	
	public HistoryThread(HashMap<String, View> viewReferences,
			UserHistoryFragment userHistoryFragment,
			SharedPreferences mainPreference){
		
		this.viewReferences = viewReferences;
		this.userSmartParkFragment = userHistoryFragment;
		this.mainPreference = mainPreference;
		
	}
	
	
	@Override
	public void run() {
		running = true;
		while (run) {
			
			
			
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Log.w(TAG , "Error in sleep2: ", e);
			}
		}
		running = false;
	}
	// =========================
	public boolean isRunning() {
		return running;
	}
	public void stopThread() {
		run = false;
	}
}
