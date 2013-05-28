package com.smartpark.background;

import android.app.Activity;

public class Ref {

	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/*
	 * A COLLECTION OF REFERENCES THAT MOST OF THE CLASSES NEED TO OPERATE. THIS
	 * IS INSPIERED BY THE ANDROID R.CLASS THAT HOUSES ALL REFERENCES FOR THE
	 * COMPONENTS ON THE DIFFERENT LAYOUTS. THE ALTERNATIVE WAS TO ALWAYS PASS
	 * REFERENCES TO OTHER CLASSES.
	 */
	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	// === Runtime references===============================================
	/*
	 * The application context can't invoke all needed method, in those cases we
	 * use a reference for an Activity class
	 */
	// Reference to the currently active activity
	public static Activity activeActivity;

	// /////////////////////////////////////////////////////////////////////

}
