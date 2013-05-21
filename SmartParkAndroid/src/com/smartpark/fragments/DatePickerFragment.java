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
		this.year = year;
		this.month = month;
		this.day = day;
		
		String monthStr;
		
		switch (month) {
		case 1:monthStr = "Jan"; break;
		case 2:monthStr = "Feb"; break;
		case 3:monthStr = "Mar"; break;
		case 4:monthStr = "Apr"; break;
		case 5:monthStr = "Maj"; break;
		case 6:monthStr = "Jun"; break;
		case 7:monthStr = "Jul"; break;
		case 8:monthStr = "Aug"; break;
		case 9:monthStr = "Sep"; break;
		case 10:monthStr = "Okt";break;
		case 11:monthStr = "Nov";break;
		case 12:monthStr = "Dec";break;
		default:monthStr = "Unknown"; break;
		}
		
		String date = day + ". " + monthStr + " " + year;
		
		if(getTag().equals("From Date")){
			((MainActivity)Ref.activeActivity).OnClickBtnDateEvent(date, 1);
		}else if(getTag().equals("To Date")){
			((MainActivity)Ref.activeActivity).OnClickBtnDateEvent(date, 2);
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