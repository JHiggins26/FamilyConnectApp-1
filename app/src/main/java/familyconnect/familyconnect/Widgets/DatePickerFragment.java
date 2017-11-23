package familyconnect.familyconnect.Widgets;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

import familyconnect.familyconnect.CreateActivitiesTab;
import familyconnect.familyconnect.R;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static Date dateRepresentation;
    private static String specialDateFormat, uniformDateFormat;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        uniformDateFormat = (month+1) + "-" + day + "-" + year;

        //[YYYY]-[MM]-[DD]

        //              BOTH BAD            DAY BAD             MONTH BAD            //BOTH GOOD
        //TEST CAES:    1999 - 01 -01   /   1999 - 10 - 01  /   1999 - 01 - 10   /   1999 - 10 - 10

        ////Format Month numbers
        if((month+1) > 0 && (month+1) < 10) {
            //If the month is bad and day is good
            specialDateFormat = year + "-" + "0" + (month+1) + "-" + day;

            if(day > 0 && day < 10) {
                //If the month and day is bad
                specialDateFormat = year + "-" + "0" + (month+1) + "-" + "0" + day;

            }
        }
        else if((month+1) >= 10) {

            if(day > 0 && day < 10) {
                //If the month is good and day is bad
                specialDateFormat = year + "-" + (month+1) + "-" + "0" + day;
            }
            else{
                //If the month and day is good
                specialDateFormat = "" + year + "-" + (month+1) + "-" + day;
            }
        }

        Log.v("DATE", ""+specialDateFormat);

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");

    }


    public static String getSpecialDateFormat() {
        return specialDateFormat;
    }

    public static void setSpecialDateFormat(String  specialDateFormat) {
        DatePickerFragment.specialDateFormat = specialDateFormat;
    }

    public static String getUniformDateFormat() {
        return uniformDateFormat;
    }

}