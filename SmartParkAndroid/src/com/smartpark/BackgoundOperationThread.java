package com.smartpark;

import java.io.Serializable;

import android.util.Log;

public class BackgoundOperationThread extends Thread {

	public static boolean b = true;
	public static boolean d = false;

	@Override
	public void run() {
		while (true) {

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (d) {
				if (!b) {
					Log.d("BackThread", "thread is no longer running");
				} else {
					Log.d("BackThread", "thread is running");
				}
			}

		}
	}
}
