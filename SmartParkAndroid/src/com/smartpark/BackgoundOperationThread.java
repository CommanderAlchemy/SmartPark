package com.smartpark;

import java.io.Serializable;

import android.util.Log;

public class BackgoundOperationThread implements Runnable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		Log.d("BackThread", "thread is running");

	}

}
