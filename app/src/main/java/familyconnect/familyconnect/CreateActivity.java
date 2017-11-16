package familyconnect.familyconnect;

import android.app.*;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import familyconnect.familyconnect.Widgets.TimePickerFragment;
import io.apptik.widget.MultiSlider;

public class CreateActivity extends AppCompatActivity {

    private static EditText name;
    private static TextView tempPercent, tempStatus, weatherConditionDropdownText, groupDropdownText;
    private static Switch in_out_switch;
    private static Spinner weatherConditionDropdown, groupDropdown;
    private static MultiSlider weatherBar;
    private static View tempCircle;
    private static int tempHigh, tempLow, rightProgressValue, leftProgressValue;
    private boolean isProgressValue = false;
    private boolean POST = false;
    private static boolean isCreated = false;
    private String groupDropdownValue, weatherConditionDropdownValue;
    private static boolean isUpdated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        in_out_switch = (Switch) findViewById(R.id.in_out_switch);

        name = (EditText) findViewById(R.id.activityName);

        groupDropdown = (Spinner) findViewById(R.id.groupDropdown);
        groupDropdownText = (TextView) findViewById(R.id.groupDropdownText);

        weatherConditionDropdown = (Spinner) findViewById(R.id.weatherConditionDropdown);
        weatherConditionDropdownText = (TextView) findViewById(R.id.weatherDropdownText);

        weatherBar = (MultiSlider) findViewById(R.id.weatherBar);
        tempPercent = (TextView) findViewById(R.id.tempPercent);
        tempStatus = (TextView) findViewById(R.id.tempStatus);


        setIndoorOutdoor();
        setGroupDropdown();
        setWeatherConditionDropdown();
        getTemperatureDegrees();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking to see if User Inputs are filled out
                if (name.getText().toString().matches("") ||
                        groupDropdownText.getText().toString().matches("Select a group") ||
                        weatherConditionDropdownText.getText().toString().matches("Select a weather condition")) {

                    Snackbar.make(view, "Please fill out activity fields!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    isCreated = true;

                    CreateActivity.FamilyConnectFetchTask taskPost = new CreateActivity.FamilyConnectFetchTask();
                    String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/activities";
                    taskPost.execute(uriPost);
                    POST = true;

                    Snackbar.make(view, "Activity Created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    DisplayActivitiesTab tab = new DisplayActivitiesTab();

                   // Intent displayActivityPage = new Intent(CreateActivity.this, GroupedActivities.class);
                    //CreateActivity.this.startActivity(displayActivityPage);

                   // displayActivityPage.putExtra("DisplayActivities", 2);
                   // startActivity(displayActivityPage);
                    isUpdated = true;



                }
            }
        });
    }


    public void setIndoorOutdoor() {

        in_out_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    in_out_switch.setText("Indoor Activity");
                    weatherBar.setEnabled(false);
                    tempStatus.setBackgroundColor(Color.WHITE);
                    tempStatus.setText("Temperature Disabled Indoors");

                    tempHigh = 0;
                    tempLow = 0;
                }
                else {
                    in_out_switch.setText("Outdoor Activity");
                    weatherBar.setEnabled(true);
                    tempStatus.setText("Set Outdoor Temperature");

                    tempHigh = rightProgressValue;
                    tempLow = leftProgressValue;
                }
            }
        });
    }


    public void setGroupDropdown() {

        ArrayList<String> options = new ArrayList<String>();
        options.add("");
        options.add("Group 1"); options.add("Group 2");
        options.add("Group 3"); options.add("Group 4");
        options.add("Group 5"); options.add("Create a Group");

        final ArrayAdapter<String> adapterList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        groupDropdown.setAdapter(adapterList);

        groupDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                groupDropdownText.setBackgroundColor(Color.WHITE);
                groupDropdownValue = groupDropdown.getSelectedItem().toString();

                int index = groupDropdown.getSelectedItemPosition();

                if(index > 0) {
                    groupDropdownText.setText("");
                }
                else {
                    groupDropdownText.setText("Select a group");
                    groupDropdownText.setBackgroundColor(Color.parseColor("#0c59cf"));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }


    public void setWeatherConditionDropdown() {

        ArrayList<String> options = new ArrayList<String>();
        options.add("");
        options.add("Clear Day"); options.add("Clear Night");
        options.add("Partly Cloudy Day"); options.add("Partly Cloudy Night");
        options.add("Cloudy"); options.add("Rain");
        options.add("Sleet"); options.add("Snow");
        options.add("Wind"); options.add("Fog");

        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        weatherConditionDropdown.setAdapter(adapterList);

        weatherConditionDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                weatherConditionDropdownText.setBackgroundColor(Color.WHITE);
                weatherConditionDropdownValue = weatherConditionDropdown.getSelectedItem().toString();

                int index = weatherConditionDropdown.getSelectedItemPosition();

                if(index > 0) {
                    weatherConditionDropdownText.setText("");
                }
                else {
                    weatherConditionDropdownText.setText("Select a weather condition");
                    weatherConditionDropdownText.setBackgroundColor(Color.parseColor("#0c59cf"));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }


    public void getTemperatureDegrees() {

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


    private class FamilyConnectFetchTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v("FamilyConnect", "URL = " + params[0]);

            //POST REQUEST FOR CREATING ACTIVITY
            if (POST) {

                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.addRequestProperty("X-Email", UserLoginActivity.getEmail());
                    connection.addRequestProperty("X-User-Token", UserLoginActivity.getToken());
                    connection.setDoOutput(true);

                    // Read in the data to PUT in the database
                    int user_id = 1;
                    String activityName = name.getText().toString();
                    String activityCategory = in_out_switch.getText().toString();
                    String activityGroupName = groupDropdownValue;
                    String activityWeatherCondition = weatherConditionDropdownValue;
                    int high = tempHigh;
                    int low = tempLow;
                    String completed = "false";
                    String site = "";
                    String created = "2017-10-29T22:12:17.391Z";
                    String updated = "2017-10-29T22:12:17.391Z";

                    String urlParameters = "activitie_name=" + activityName + "&user_id=" + user_id +
                            "&created_at=" + created + "&updated_at=" + updated + "&condition=" + activityWeatherCondition
                            + "&category=" + activityCategory +  "&tempHi=" + high + "&tempLow=" + low + "&url=" + completed;

                    connection.setDoOutput(true);

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static boolean getIsUpdated() {
        return isUpdated;
    }

    public static void setIsUpdated(boolean isUpdated) {
        CreateActivity.isUpdated = isUpdated;
    }

}
