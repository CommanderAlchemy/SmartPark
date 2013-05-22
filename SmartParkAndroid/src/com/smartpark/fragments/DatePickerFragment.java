package com.smartpark.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.smartpark.activities.MainActivity;
import com.smartpark.background.Ref;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	// Debug stuff
	private static final boolean D = Ref.D;
	private static final String TAG = "DatePickerFragment";

	// Latest date picked
	int year = 0, month = 0, day = 0;

	// ///////////////////////////////////////////////////

	public DatePickerFragment() {
		super();
		Calendar c = Calendar.getInstance();
		this.year = c.get(Calendar.YEAR);
		this.month = c.get(Calendar.MONTH);
		this.day = c.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, this.year, this.month,
				this.day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		Log.i(TAG, "++ onDateSet ++");
		/*
		 * we are saving the date to use this as the default next time the user
		 * pushes the button.
		 */
		this.year = year;
		this.month = month;
		this.day = day;

		if (getTag().equals("From Date")) {
			((MainActivity) Ref.activeActivity).OnClickBtnDateEvent(getDate(),
					MainActivity.BUTTON_FROM_DATE);
		} else if (getTag().equals("To Date")) {
			((MainActivity) Ref.activeActivity).OnClickBtnDateEvent(getDate(),
					MainActivity.BUTTON_TO_DATE);
		}
	}

	public int[] getDate() {
		int date[] = new int[3];
		date[0] = this.day;
		date[1] = this.month;
		date[2] = this.year;
		return date;
	}
	
	public void setDate(int[] date) {
		this.day = date[0];
		this.month = date[1];
		this.year = date[2];
	}
	
}