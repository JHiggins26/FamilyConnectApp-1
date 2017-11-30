package familyconnect.familyconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import familyconnect.familyconnect.Widgets.DatePickerFragment;
import familyconnect.familyconnect.Widgets.TimePickerFragment;
import familyconnect.familyconnect.json.FamilyConnectActivitiesHttpResponse;

/**
 * SuggestedFutureActivity.java - a class that suggest a future activity based on the weather condition.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class SuggestedFutureActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean isActivity, isActivityFound = false;
    private TextView date, weatherTemp, weatherCondition, weatherSummary, activityTitle, tapSuggestion;
    private ImageButton activityImage;

    private String activityName, activityCondition, activityTempLow, activityTempHigh;

    private ArrayList<String> suggestedList = new ArrayList<String>();

    private MediaPlayer dailyActivitySound;

    private int nextActivity = 0;
    private boolean runOnce = false;

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
        setContentView(R.layout.activity_suggested_future);

        date = findViewById(R.id.date_text);
        weatherTemp = findViewById(R.id.weather_temp);
        weatherCondition = findViewById(R.id.weather_condition);
        weatherSummary = findViewById(R.id.weather_summary);
        activityTitle = findViewById(R.id.activity_title);
        tapSuggestion = findViewById(R.id.tap_suggestion);
        tapSuggestion.setOnClickListener(this);
        activityImage = findViewById(R.id.activity_image);
        activityImage.setOnClickListener(this);

        runOnce = true;
        filterActivity();
    }


    /**
     * @method onClick()
     *
     * This method provides a specific functionality for each button that is clicked
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        filterActivity();
        runOnce = false;

        dailyActivitySound = MediaPlayer.create(this, R.raw.daily_activity_sound);

        try {
            dailyActivitySound.start();

        }catch (Exception e) {

            if(dailyActivitySound != null) {
                dailyActivitySound.reset();
                dailyActivitySound.release();
            }
        }
    }


    /**
     * @method filterActivity()
     *
     * This method sends a request to filter the activities based on the weather conditions.
     */
    public void filterActivity() {

        isActivity = true;

        if(GroupsTab.getGroupID() == 0) {
            Toast.makeText(SuggestedFutureActivity.this, "Please consider joining or creating a group first!",
                    Toast.LENGTH_LONG).show();
        }
        else {
            SuggestedFutureActivity.FamilyConnectFetchTask taskPost = new SuggestedFutureActivity.FamilyConnectFetchTask();
            String uriGetActivity = "https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/" + GroupsTab.getGroupID() + "/activities";

            taskPost.execute(uriGetActivity);
        }
    }


    /**
     * @class FamilyConnectFetchTask
     *
     * This class performs an Async Task that calls the Restful Api
     *
     */
    private class FamilyConnectFetchTask extends AsyncTask<String, Void, Bitmap> {

        //Converts JSON string into a Activity object
        private FamilyConnectActivitiesHttpResponse getTask(String json){
            Gson gson = new GsonBuilder().create();

            if(json.charAt(0) == ','){
                json = json.substring(1);
            }
            FamilyConnectActivitiesHttpResponse activity = gson.fromJson(json, FamilyConnectActivitiesHttpResponse.class);
            return activity;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            //GET REQUEST FOR ALL ACTIVITIES
            if (isActivity && HomeTab.getFutureTemperature() != null) {

                try {
                    String line;
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.addRequestProperty("Content-Type", "application/json");
                    connection.addRequestProperty("X-User-Email", UserLoginActivity.getEmail());
                    connection.addRequestProperty("X-User-Token", UserLoginActivity.getToken());

                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder json = new StringBuilder();


                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }

                    //Trims braces at the beginning and end of the string
                    String jsonTrim = json.toString().substring(1,json.toString().length()-1);

                    Log.v("String", "" + jsonTrim);

                    Scanner scanJ = new Scanner(jsonTrim);
                    scanJ.useDelimiter("[}]");

                    ArrayList<FamilyConnectActivitiesHttpResponse> act = new ArrayList<FamilyConnectActivitiesHttpResponse>();

                    while(scanJ.hasNext()){
                        act.add(getTask(scanJ.next() + "}"));
                    }

                    for(FamilyConnectActivitiesHttpResponse activities : act) {

                        Activity activity = new Activity(activities.getId(), activities.getActivitieName(), activities.getIcon(), activities.getCondition(),
                                activities.getTempLow(), activities.getTempHi(), activities.getCategory(), GroupsTab.getGroupsDropdownValue(), activities.getIsCompleted());

                        //Filter temperature
                        if((HomeTab.getFutureTemperature() >= Double.parseDouble(activities.getTempLow()) &&
                                HomeTab.getFutureTemperature() <= Double.parseDouble(activities.getTempHi()))
                                || (activities.getCondition().equals("Indoors"))) {

                            //Filter Weather Status
                            if(HomeTab.getFutureIcon().equals(activities.getIcon()) || activities.getIcon().equals("Indoors")) {

                                //Filter If Activity Has Not Been Completed
                                if(!activities.getIsCompleted()) {
                                    isActivityFound = true;
                                    suggestedList.add(activity.toString());
                                }
                            }
                        }
                    }

                    if(suggestedList.size() > 0) {
                        String splitList[] = suggestedList.toString().split("\n");

                        if(runOnce) {
                            Random randomActivity = new Random();
                            int value = randomActivity.nextInt(suggestedList.size());
                            nextActivity = value;
                        }

                        String splitActivityAttributes[] = splitList[nextActivity].substring(1, splitList[nextActivity].length()).split(",");

                        activityName = splitActivityAttributes[0];
                        activityCondition = splitActivityAttributes[2];
                        activityTempLow = splitActivityAttributes[3];
                        activityTempHigh = splitActivityAttributes[4];

                        nextActivity++;

                        if(nextActivity >= suggestedList.size()) {
                            nextActivity = 0;
                        }
                    }
                    else {
                        isActivityFound = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            date.setText("Date: " + DatePickerFragment.getUniformDateFormat() + " @ " + TimePickerFragment.getTime());

            if(isActivityFound) {
                weatherSummary.setText("Weather Summary: " + HomeTab.getFutureSummary() + "\nHigh: " + HomeTab.getFutureTemperatureHigh() + "°F" +
                        " Low: " + HomeTab.getFutureTemperatureLow() + "°F" + "\nCurrently: " + HomeTab.getFutureTemperature() + "°F");
                weatherTemp.setText("Activity Temperature: " + "High: " + ((int) Double.parseDouble(activityTempHigh)) + "°F " + "Low: " + ((int) Double.parseDouble(activityTempLow)) + "°F");
                weatherCondition.setText("Activity Weather Condition:" + activityCondition);
                activityTitle.setText(activityName);
            }
            else {
                activityTitle.setText("No Activities Found Today");
            }
        }
    }

    /**
     * @method onBackPressed()
     *
     * This method is sets the page when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        HomeTab.setRunOnce(true);
        Intent homePage = new Intent(SuggestedFutureActivity.this, GroupedActivities.class);
        SuggestedFutureActivity.this.startActivity(homePage);

    }
}
