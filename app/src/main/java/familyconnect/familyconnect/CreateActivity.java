package familyconnect.familyconnect;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import io.apptik.widget.MultiSlider;

/**
 * CreateActivity.java - a class that creates each activity based on the weather condition chosen.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class CreateActivity extends AppCompatActivity {

    private EditText name;
    private TextView tempPercent, tempStatus, weatherConditionDropdownText;
    private Switch in_out_switch;
    private Spinner weatherConditionDropdown;
    private MultiSlider weatherBar;
    private int tempHigh, tempLow, rightProgressValue, leftProgressValue;
    private boolean POST = false;
    private String weatherConditionDropdownValue;
    private boolean isOutdoor = true;
    private String weatherIcon;

    /**
     * @method onCreate()
     *
     * This method creates the android activity and initializes each instance variable.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        in_out_switch = findViewById(R.id.in_out_switch);
        name = findViewById(R.id.activityName);
        weatherConditionDropdown = findViewById(R.id.weatherConditionDropdown);
        weatherConditionDropdownText = findViewById(R.id.weatherDropdownText);
        weatherBar = findViewById(R.id.weatherBar);
        tempPercent = findViewById(R.id.tempPercent);
        tempStatus = findViewById(R.id.tempStatus);

        setIndoorOutdoor();
        setWeatherConditionDropdown();
        setTemperatureDegrees();
        createActivity();
    }

    /**
     * @method createActivity()
     *
     * This method creates each activity when all conditions are met.
     */
    public void createActivity() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking to see if User Inputs are filled out
                if (name.getText().toString().matches("") ||
                        (weatherConditionDropdownText.getText().toString().matches("Select a weather condition") && isOutdoor == true)) {

                    Snackbar.make(view, "Please fill out activity fields!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    if(GroupsTab.getGroupID() == 0) {
                        Toast.makeText(CreateActivity.this, "Please consider joining or creating a group first!",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        CreateActivity.FamilyConnectFetchTask taskPost = new CreateActivity.FamilyConnectFetchTask();
                        String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/" + GroupsTab.getGroupID() + "/activities";
                        taskPost.execute(uriPost);
                        POST = true;

                        name.setText("");
                        Snackbar.make(view, "Activity Created", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
        });
    }

    /**
     * @method setIndoorOutdoor()
     *
     * This method checks if the activity is an indoor or outdoor activity.
     */
    public void setIndoorOutdoor() {

        in_out_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    in_out_switch.setText("Indoor Activity");
                    weatherBar.setEnabled(false);
                    tempStatus.setBackgroundColor(Color.WHITE);
                    tempStatus.setText("Temperature Disabled Indoors");

                    weatherConditionDropdown.setSelection(0);
                    weatherConditionDropdown.setEnabled(false);
                    weatherConditionDropdownText.setText("Select a weather condition");
                    weatherConditionDropdownText.setBackgroundColor(Color.parseColor("#0c59cf"));
                    weatherConditionDropdownText.getBackground().setAlpha(80);
                    weatherConditionDropdownValue = "Indoors";
                    weatherIcon = "Indoors";
                    tempHigh = 0;
                    tempLow = 0;
                    isOutdoor = false;
                }
                else {
                    in_out_switch.setText("Outdoor Activity");
                    weatherBar.setEnabled(true);
                    tempStatus.setText("Set Outdoor Temperature");

                    weatherConditionDropdown.setEnabled(true);
                    weatherConditionDropdownText.getBackground().setAlpha(255);
                    weatherConditionDropdownText.setText("Select a weather condition");
                    weatherConditionDropdownValue = "";
                    tempHigh = rightProgressValue;
                    tempLow = leftProgressValue;
                    isOutdoor = true;
                }
            }
        });
    }

    /**
     * @method setWeatherConditionDropdown
     *
     * This method sets the value of the weather selected in the dropdown.
     */
    public void setWeatherConditionDropdown() {

        ArrayList<String> options = new ArrayList<String>();
        options.add("");
        options.add("Partly Cloudy Day (Common)"); options.add("Partly Cloudy Night (Common)");
        options.add("Clear Day"); options.add("Clear Night");
        options.add("Cloudy"); options.add("Rain");
        options.add("Sleet"); options.add("Snow");
        options.add("Wind"); options.add("Fog");

        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        weatherConditionDropdown.setAdapter(adapterList);

        weatherConditionDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                weatherConditionDropdownValue = weatherConditionDropdown.getSelectedItem().toString();

                if(weatherConditionDropdownValue.equals("Partly Cloudy Day (Common)")) {
                    weatherConditionDropdownValue = "Partly Cloudy Day";
                    weatherIcon = "partly-cloudy-day";
                }
                else if(weatherConditionDropdownValue.equals("Partly Cloudy Night (Common)")) {
                    weatherConditionDropdownValue = "Partly Cloudy Night";
                    weatherIcon = "partly-cloudy-night";
                }
                else if(weatherConditionDropdownValue.equals("Clear Day")) { weatherIcon = "clear-day"; }
                else if(weatherConditionDropdownValue.equals("Clear Night")) { weatherIcon = "clear-night"; }
                else if(weatherConditionDropdownValue.equals("Cloudy")) { weatherIcon = "cloudy"; }
                else if(weatherConditionDropdownValue.equals("Rain")) { weatherIcon = "rain"; }
                else if(weatherConditionDropdownValue.equals("Sleet")) { weatherIcon = "sleet"; }
                else if(weatherConditionDropdownValue.equals("Snow")) { weatherIcon = "snow"; }
                else if(weatherConditionDropdownValue.equals("Wind")) { weatherIcon = "wind"; }
                else if(weatherConditionDropdownValue.equals("Fog")) { weatherIcon = "fog"; }

                int index = weatherConditionDropdown.getSelectedItemPosition();

                if(index > 0) {
                    weatherConditionDropdownText.setText("");
                    weatherConditionDropdownText.setBackgroundColor(Color.WHITE);
                }
                else {
                    if(isOutdoor) {
                        weatherConditionDropdownText.setText("Select a weather condition");
                        weatherConditionDropdownText.setBackgroundColor(Color.parseColor("#0c59cf"));
                    }
                    else {
                        weatherConditionDropdownValue = "Indoors";
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * @method setTemperatureDegrees()
     *
     * This method sets the temperature of the activity based on the slider values.
     */
    public void setTemperatureDegrees() {

        tempHigh = 100;
        tempLow = 0;
        rightProgressValue = 100;
        leftProgressValue = 0;

        weatherBar.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {

                //LEFT HANDLE
                if (thumbIndex == 0) {
                    leftProgressValue = value;

                    if(leftProgressValue >= 1) {
                        leftProgressValue++;
                    }
                }
                //RIGHT HANDLE
                else {
                    rightProgressValue = value;

                    if(rightProgressValue <= 99) {
                        rightProgressValue--;
                    }
                }

                if (rightProgressValue <= 35) {
                    tempStatus.setBackgroundColor(Color.parseColor("#8EE5FF"));
                    tempStatus.setText("COLD");
                    tempHigh = rightProgressValue;
                    tempLow = leftProgressValue;
                }
                if (leftProgressValue >= 0 && rightProgressValue > 35) {
                    tempStatus.setBackgroundColor(Color.parseColor("#8EE5FF"));
                    tempStatus.setText("COLD OR HIGHER");
                    tempHigh = rightProgressValue;
                    tempLow = leftProgressValue;
                }
                if (leftProgressValue > 35) {
                    tempStatus.setBackgroundColor(Color.parseColor("#6AB3FF"));
                    tempStatus.setText("COOL OR HIGHER");
                    tempHigh = rightProgressValue;
                    tempLow = leftProgressValue;
                }
                if (leftProgressValue > 35 && rightProgressValue <= 65) {
                    tempStatus.setBackgroundColor(Color.parseColor("#6AB3FF"));
                    tempStatus.setText("COOL");
                    tempHigh = rightProgressValue;
                    tempLow = leftProgressValue;
                }
                if (leftProgressValue > 65) {
                    tempStatus.setBackgroundColor(Color.parseColor("#FF8154"));
                    tempStatus.setText("WARM OR HIGHER");
                    tempHigh = rightProgressValue;
                    tempLow = leftProgressValue;
                }
                if (leftProgressValue > 65 && rightProgressValue <= 80) {
                    tempStatus.setBackgroundColor(Color.parseColor("#FF8154"));
                    tempStatus.setText("WARM");
                    tempHigh = rightProgressValue;
                    tempLow = leftProgressValue;
                }
                if (leftProgressValue > 80) {
                    tempStatus.setBackgroundColor(Color.parseColor("#FF1C15"));
                    tempStatus.setText("HOT");
                    tempHigh = rightProgressValue;
                    tempLow = leftProgressValue;
                }

                tempPercent.setText("Low: " + leftProgressValue + " °F" + " / " + "High: " + rightProgressValue + " °F");
            }
        });
    }

    /**
     * @class FamilyConnectFetchTask
     *
     * This class performs an Async Task that calls the Restful Api
     *
     */
    private class FamilyConnectFetchTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v("FamilyConnect", "URI = " + params[0]);

            //POST REQUEST FOR CREATING ACTIVITY
            if (POST) {

                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty("X-User-Email", UserLoginActivity.getEmail());
                    connection.addRequestProperty("X-User-Token", UserLoginActivity.getToken());
                    connection.setDoOutput(true);

                    // Read in the data to PUT in the database
                    int user_id = UserLoginActivity.getID();
                    String activityName = name.getText().toString();
                    String activityCategory = in_out_switch.getText().toString();
                    String activityWeatherCondition = weatherConditionDropdownValue;
                    int high = tempHigh;
                    int low = tempLow;
                    boolean completed = false;
                    String icon = weatherIcon;

                    String urlParameters = "activitie_name=" + activityName + "&user_id=" + user_id + "&condition=" + activityWeatherCondition
                            + "&category=" + activityCategory +  "&tempHi=" + high + "&tempLow=" + low + "&isCompleted=" + completed + "&icon=" + icon;

                    DataOutputStream dataStream = new DataOutputStream(connection.getOutputStream());
                    dataStream.write(urlParameters.getBytes());
                    dataStream.flush();
                    dataStream.close();

                    int responseCode = connection.getResponseCode();
                    String output = "Request URL " + url;
                    output += System.getProperty("line.separator") + "Request Parameters: " + urlParameters;
                    output += System.getProperty("line.separator") + "Response Code " + responseCode;

                    Log.v("FamilyConnect", "POST = " + output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }
    }

    /**
     * @method dispatchTouchEvent
     *
     * This method hides the keyboard window when touched outside the keyboards coordinates.
     *
     * @param event
     * @return dispatchTouchEvent(ev)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(event);
    }
}
