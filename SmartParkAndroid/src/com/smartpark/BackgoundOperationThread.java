package com.smartpark;

import android.util.Log;

public class BackgoundOperationThread extends Thread {
	
	/* keeps a list of booleans to determine if all activities have been
	destroyed so that the thread wont continue running for ever. */
	public static boolean mainActivity = true;
	public static boolean settingsActivity = true;
	public static boolean loginActivity = true;
	
	private static long shutdownTime = 0; // 0 = never
	
	
	// Debugging control
	public static boolean D = Ref.D;

	
	
	public BackgoundOperationThread(){
		
		
		
	}
	
	@Override
	public void run() {
		while (true) {

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (Ref.D) {
				if (mainActivity || settingsActivity || loginActivity) {
					Log.d("BackThread", "thread is running");
				} else {
					
					Log.d("BackThread", "thread is no longer running");
				}
			}

		}
		
	}
}
