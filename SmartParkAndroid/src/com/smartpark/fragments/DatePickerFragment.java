package com.smartpark.fragments;

import java.util.Calendar;

import com.smartpark.activities.MainActivity;
import com.smartpark.background.Ref;

import android.R;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	int year = 0, month = 0, day = 0;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (year == 0) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			this.year = c.get(Calendar.YEAR);
			this.month = c.get(Calendar.MONTH);
			this.day = c.get(Calendar.DAY_OF_MONTH);
		}

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, this.year, this.month,
				this.day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		/*
		 * we are saving the date to use this as the default next time the user
		 * pushes the button.
		 */
		this.year = year;
		this.month = month;
		this.day = day;
		
		if (getTag().equals("From Date")) {
			((MainActivity) Ref.activeActivity).OnClickBtnDateEvent(getDate(),
					1);
		} else if (getTag().equals("To Date")) {
			((MainActivity) Ref.activeActivity).OnClickBtnDateEvent(getDate(),
					2);
		}
	}

	public int[] getDate() {
		int date[] = new int[3];
		date[0] = this.day;
		date[1] = this.month;
		date[2] = this.year;
		return date;
	}
}