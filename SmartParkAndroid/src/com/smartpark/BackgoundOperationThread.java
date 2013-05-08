package com.smartpark;

import android.util.Log;

public class BackgoundOperationThread extends Thread {
	
	/* keeps a list of booleans to determine if all activities have been
	destroyed so that the thread wont continue running for ever. */
	public static boolean mainActivity = true;
	
	// Debugging control
	public static boolean D = false;

	
	
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
				if (!b) {
					Log.d("BackThread", "thread is no longer running");
				} else {
					Log.d("BackThread", "thread is running");
				}
			}

		}
	}
}
