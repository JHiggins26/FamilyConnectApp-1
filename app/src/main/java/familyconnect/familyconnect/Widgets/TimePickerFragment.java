package familyconnect.familyconnect.Widgets;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;
import android.widget.TimePicker;

import familyconnect.familyconnect.CreateActivitiesTab;
import familyconnect.familyconnect.HomeTab;
import familyconnect.familyconnect.R;
import familyconnect.familyconnect.SuggestedFutureActivity;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private static String time;
    private static String specialTimeFormat;
    private static String aMpM = "AM";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){

        //This variable plugs into the weather api URL
        specialTimeFormat = hourOfDay +":" + minute + ":00";

        aMpM = "AM";

        if(hourOfDay > 11)
        {
            aMpM = "PM";
        }

        //Make the 24 hour time format to 12 hour time format
        int currentHour;

        if(hourOfDay > 11) {
            currentHour = hourOfDay - 12;
        }
        else {
            currentHour = hourOfDay;
        }

        //              BOTH BAD      HOUR BAD       MIN BAD     //BOTH GOOD
        //TEST CASES:   01 : 01   /   01 : 10   /   10 : 01   /   10 : 10

        //FORMATTING REGULAR TIME
        if(currentHour == 0) {
            currentHour = 12;
        }

        if(currentHour > 0 && currentHour < 10) {
            //If hour is bad and minute is good
            time = "0" + currentHour +":" + minute + " " + aMpM;

            if(minute >= 0 && minute < 10) {
                //If hour and minute is bad
                time = "0" + currentHour +":" + "0" + minute + " " + aMpM;
            }
        }
        else if(currentHour >= 10){

            if (minute >= 0 && minute < 10) {
                //If hour is good and minute is bad
                time = currentHour + ":" + "0" + minute + " " + aMpM;
            }
            else {
                //If hour and minute is good
                time = currentHour +":" + minute + " " + aMpM;
            }
        }


        //FORMATTING 24-HOUR TIME
        if(hourOfDay == 0) {
            hourOfDay = 12;
        }

        if(hourOfDay > 0 && hourOfDay < 10) {
            //If hour is not good and minute is good
            specialTimeFormat = "0" + hourOfDay + ":" + minute + ":00";

            if(minute >= 0 && minute < 10) {
                //If hour is not good and minute is not good
                specialTimeFormat = "0" + hourOfDay + ":" + "0" + minute + ":00";
            }
        }
        else if(hourOfDay >= 10) {

            if (minute >= 0 && minute < 10) {
                //If hour is good and minute is not good
                specialTimeFormat = hourOfDay + ":" + "0" + minute + ":00";
            }
            else {
                //If hour and minute is good
                specialTimeFormat = hourOfDay + ":" + minute + ":00";
            }
        }

        HomeTab.getFutureBtn().performClick();

    }


    public static void setTime(String time) {
        TimePickerFragment.time = time;
    }

    public static String getAmPm() {
        return aMpM;
    }

    public static void setAmPm(String aMpM) {
        TimePickerFragment.aMpM = aMpM;
    }

    public static String getTime() {
        return time;
    }

    public static void setSpecialFormatTime(String specialTimeFormat) {TimePickerFragment.specialTimeFormat = specialTimeFormat; }

    public static String getSpecialFormatTime() {
        return specialTimeFormat;
    }
}