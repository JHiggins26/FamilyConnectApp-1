package familyconnect.familyconnect;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import familyconnect.familyconnect.Widgets.TimePickerFragment;
import io.apptik.widget.MultiSlider;

public class CreateActivity extends AppCompatActivity {

    private static EditText name;
    private static TextView tempPercent, tempStatus, weatherConditionDropdownText, groupDropdownText, categoryDropdownText;
    private String categoryDropdownValue, groupDropdownValue, weatherConditionDropdownValue;
    private static ToggleButton in_out;
    private static Spinner weatherConditionDropdown, groupDropdown, categoryDropdown;
    private static MultiSlider weatherBar;
    private static View tempCircle;
    private static int progressValue = 0, tempHigh = 0, tempLow = 0, rightProgressValue = 0, leftProgressValue = 0;
    private boolean isProgressValue = false;
    private boolean POST = false;
    private static boolean isCreated = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        name = (EditText) findViewById(R.id.activityName);
        in_out = (ToggleButton) findViewById(R.id.in_out_Button);

        categoryDropdown = (Spinner) findViewById(R.id.categoryDropdown);
        categoryDropdownText = (TextView) findViewById(R.id.categoryDropdownText);

        groupDropdown = (Spinner) findViewById(R.id.groupDropdown);
        groupDropdownText = (TextView) findViewById(R.id.groupDropdownText);

        weatherConditionDropdown = (Spinner) findViewById(R.id.weatherConditionDropdown);
        weatherConditionDropdownText = (TextView) findViewById(R.id.weatherDropdownText);

        weatherBar = (MultiSlider) findViewById(R.id.weatherBar);
        tempPercent = (TextView) findViewById(R.id.tempPercent);
        tempStatus = (TextView) findViewById(R.id.tempStatus);


        //tempCircle = findViewById(R.id.tempCircle);

        setCategoryDropdown();
        setGroupDropdown();
        setWeatherConditionDropdown();
        getTemperatureDegrees();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking to see if User Inputs are filled out
                if (name.getText().toString().matches("") ||
                        weatherConditionDropdownText.getText().toString().matches("How's the weather\\?") ||
                        isProgressValue == false || groupDropdownText.getText().toString().matches("Select a group") ||
                        categoryDropdownText.getText().toString().matches("Select a category")) {

                    Snackbar.make(view, "Please fill out activity fields!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    isCreated = true;

                    CreateActivity.FamilyConnectFetchTask taskPost = new CreateActivity.FamilyConnectFetchTask();
                    String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users/1/activities";
                    //String uriPost ="https://myprojects-mikeh87.c9users.io/users/1/activities";
                    taskPost.execute(uriPost);
                    POST = true;

                    Snackbar.make(view, "Activity Created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();             }
            }
        });
    }

    public void getTemperatureDegrees() {

        weatherBar.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {

                isProgressValue = true;

                //LEFT HANDLE
                if (thumbIndex == 0) {
                    leftProgressValue = value;
                    leftProgressValue++;
                }
                //RIGHT HANDLE
                else {
                    rightProgressValue = value;
                    rightProgressValue--;
                }


                if(rightProgressValue <= 35) {
                    tempStatus.setBackgroundColor(Color.parseColor("#8EE5FF"));
                    tempStatus.setText("COLD");
                    tempHigh = 35;
                    tempLow = 0;
                }
                if(leftProgressValue >= 0 && rightProgressValue > 35) {
                    tempStatus.setBackgroundColor(Color.parseColor("#8EE5FF"));
                    tempStatus.setText("COLD OR HIGHER");
                    tempHigh = 35;
                    tempLow = 0;
                }
                if(leftProgressValue > 35 ) {
                    tempStatus.setBackgroundColor(Color.parseColor("#6AB3FF"));
                    tempStatus.setText("COOL OR HIGHER");
                    tempHigh = 35;
                    tempLow = 0;
                }
                if(leftProgressValue > 35 && rightProgressValue <= 65) {
                    tempStatus.setBackgroundColor(Color.parseColor("#6AB3FF"));
                    tempStatus.setText("COOL");
                    tempHigh = 65;
                    tempLow = 36;
                }
                if(leftProgressValue > 65) {
                    tempStatus.setBackgroundColor(Color.parseColor("#FF8154"));
                    tempStatus.setText("WARM OR HIGHER");
                    tempHigh = 65;
                    tempLow = 36;
                }
                if(leftProgressValue > 65 && rightProgressValue <= 80) {
                    tempStatus.setBackgroundColor(Color.parseColor("#FF8154"));
                    tempStatus.setText("WARM");
                    tempHigh = 80;
                    tempLow = 66;
                }
                if(leftProgressValue > 80) {
                    tempStatus.setBackgroundColor(Color.parseColor("#FF1C15"));
                    tempStatus.setText("HOT");
                    tempHigh = 80;
                    tempLow = 66;
                }


                tempPercent.setText("Low: " + leftProgressValue + " °F" + " / " + "High: " + rightProgressValue + " °F");
            }
        });
    }


    public void setCategoryDropdown() {

        ArrayList<String> options = new ArrayList<String>();
        options.add("");
        options.add("Category 1"); options.add("Category 2");
        options.add("Category 3"); options.add("Category 4");
        options.add("Category 5"); options.add("Category 6");

        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        categoryDropdown.setAdapter(adapterList);

        categoryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) view).setTextColor(Color.WHITE);
                ((TextView) view).setGravity(Gravity.CENTER);
                categoryDropdownValue = ((TextView) view).getText().toString();

                int index = categoryDropdown.getSelectedItemPosition();

                if(index > 0) {
                    categoryDropdownText.setText("");
                }
                else {
                    categoryDropdownText.setText("Select a category");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }


    public void setGroupDropdown() {

        ArrayList<String> options = new ArrayList<String>();
        options.add("");
        options.add("Group 1"); options.add("Group 2");
        options.add("Group 3"); options.add("Group 4");
        options.add("Group 5"); options.add("Create a Group");

        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        groupDropdown.setAdapter(adapterList);

        groupDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) view).setTextColor(Color.WHITE);
                ((TextView) view).setGravity(Gravity.CENTER);
                groupDropdownValue = ((TextView) view).getText().toString();

                int index = groupDropdown.getSelectedItemPosition();

                if(index > 0) {
                    groupDropdownText.setText("");
                }
                else {
                    groupDropdownText.setText("Select a group");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }


    public void setWeatherConditionDropdown() {
        //"clear-day", "clear-night", "partly-cloudy-day","partly-cloudy-night", "cloudy", "rain", "sleet", "snow", "wind","fog"

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

                ((TextView) view).setTextColor(Color.WHITE);
                ((TextView) view).setGravity(Gravity.CENTER);
                weatherConditionDropdownValue = ((TextView) view).getText().toString();

                int index = weatherConditionDropdown.getSelectedItemPosition();

                if(index > 0) {
                    weatherConditionDropdownText.setText("");
                }
                else {
                    weatherConditionDropdownText.setText("How's the weather?");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }


    /*public void getTemperatureDegrees() {

        weatherBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                progressValue = progress;
                isProgressValue = true;

                if(progressValue <= 35) {

                    tempStatus.setBackgroundColor(Color.parseColor("#8EE5FF"));
                    tempStatus.setText("COLD");
                    tempHigh = 35;
                    tempLow = 0;
                }
                else if(progressValue > 35 && progressValue <= 65) {
                    tempStatus.setBackgroundColor(Color.parseColor("#6AB3FF"));
                    tempStatus.setText("COOL");
                    tempHigh = 65;
                    tempLow = 36;
                }
                else if(progressValue > 65 && progressValue <= 80) {
                    tempStatus.setBackgroundColor(Color.parseColor("#FF8154"));
                    tempStatus.setText("WARM");
                    tempHigh = 80;
                    tempLow = 66;
                }
                else if(progressValue > 80 && progressValue <= 100) {
                    tempStatus.setBackgroundColor(Color.parseColor("#FF1C15"));
                    tempStatus.setText("HOT");
                    tempHigh = 100;
                    tempLow = 81;
                }

                tempPercent.setText(progressValue + "/" + weatherBar.getMax() + " °F");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tempPercent.setText(progressValue + "/" + weatherBar.getMax() + " °F");
            }
        });
    }*/



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

                    // Read in the data to PUT in the database
                    int user_id = 1;
                    String activityName = name.getText().toString();
                    String activityCategory = categoryDropdownValue;
                    String activityGroupName = groupDropdownValue;
                    String activityWeatherCondition = weatherConditionDropdownValue;
                    String activityWeatherDegrees = progressValue + "";
                    int high = tempHigh;
                    int low = tempLow;
                    String site = "";
                    String created = "2017-10-29T22:12:17.391Z";
                    String updated = "2017-10-29T22:12:17.391Z";

                    String urlParameters = "activitie_name=" + activityName + "&user_id=" + user_id +
                            "&created_at=" + created + "&updated_at=" + updated + "&condition=" + activityWeatherCondition
                            + "&category=" + activityCategory +  "&tempHi=" + high + "&tempLow=" + low + "&url=" + site;


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




}
