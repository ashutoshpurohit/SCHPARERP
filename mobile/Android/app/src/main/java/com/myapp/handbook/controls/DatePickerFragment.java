package com.myapp.handbook.controls;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by SAshutosh on 10/16/2016.
 */

public class DatePickerFragment extends DialogFragment {

    DatePickerDialog.OnDateSetListener dateSetListener;
    public void setDateSetListener(DatePickerDialog.OnDateSetListener listener){
        this.dateSetListener=listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }
}
