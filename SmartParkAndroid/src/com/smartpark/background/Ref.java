package com.smartpark.background;

import android.app.Activity;

public class Ref {

	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/*
	 * A COLLECTION OF REFERENCES THAT MOST OF THE CLASSES NEED TO OPERATE. THIS
	 * IS INSPIERED BY THE ANDROID R.CLASS THAT HOUSES ALL REFERENCES FOR THE
	 * COMPONENTS ON THE DIFFERENT LAYOUTS. THE ALTERNATIVE WAS TO ALWAYS PASS
	 * REFERENCES TO OTHER CLASSES WHICH IS NOT POSSIBLE IN SOME CASES LIKE
	 * PASSING REFERENCES TO A SERVICE.
	 */
	// We have reduced those references to create a more object oriented
	// design!!
	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	// === Runtime references===============================================
	/*
	 * The application context can't invoke all needed method, in those cases we
	 * use a reference for an Activity class. This reference is static and can't
	 * reside in its own class or to create a static method that returns this
	 * reference in its own class. We figured it could just remain here.
	 */
	// Reference to the currently active activity
	public static Activity activeActivity;

	// /////////////////////////////////////////////////////////////////////

}
