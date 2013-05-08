package com.smartpark;

import com.smartpark.bluetooth.BlueController;
import com.smartpark.tcp.TCPClient;

public class References {

	// ACOLLECTION OF REFERENCES THAT MOST OF THE CLASSES NEED TO OPERATE
	// THIS IS INSPIERED BY THE ANDROID R.CLASS THAT HOUSES ALL REFERENCES
	// FOR THE COMPONENTS ON THE DIFFERENT LAYOUTS.
	// THE ALTERNATIVE WAS TO ALWAYS PASS REFERENCES TO OTHER CLASSES.
	// TWO OF THE MOST IMPORTATNT CLASSES ARE BACKGROUNDTHREAD-REF AND
	// CLIENTTHREAD, SINCE ALL CLASSES WILL NEEDS THEIR SERVICES TO OPERATE.

	public static TCPClient client;
	public static Thread clientThread;
	public static Thread backgroundThread;
	public static BlueController bluetooth;

	

}
