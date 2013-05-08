package com.smartpark;

import java.io.Serializable;

import android.util.Log;

public class BackgoundOperationThread extends Thread implements Runnable, Serializable {

	@Override
	public void run() {
		while(true){
			
			Log.d("BackThread", "thread is running");
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
		}
	}
}
