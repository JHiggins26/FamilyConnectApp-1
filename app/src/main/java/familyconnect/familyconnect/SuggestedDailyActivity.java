package familyconnect.familyconnect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import familyconnect.familyconnect.Widgets.DatePickerFragment;
import familyconnect.familyconnect.Widgets.TimePickerFragment;
import familyconnect.familyconnect.json.FamilyConnectActivitiesHttpResponse;
import familyconnect.familyconnect.json.FamilyConnectHttpResponse;

public class SuggestedDailyActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean isActivity, isActivityFound = false;
    private TextView weatherTemp, weatherCondition, weatherSummary, activityTitle, tapSuggestion;
    private ImageButton activityImage;

    private String activityName, activityCondition, activityTempLow, activityTempHigh;

    private ArrayList<String> suggestedList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_daily);

        weatherTemp = findViewById(R.id.weather_temp);
        weatherCondition = findViewById(R.id.weather_condition);
        weatherSummary = findViewById(R.id.weather_summary);
        activityTitle = findViewById(R.id.activity_title);
        tapSuggestion = findViewById(R.id.tap_suggestion);
        tapSuggestion.setOnClickListener(this);
        activityImage = findViewById(R.id.activity_image);
        activityImage.setOnClickListener(this);


        filterActivity();
    }


    @Override
    public void onClick(View v) {

        Intent refreshPage = new Intent(SuggestedDailyActivity.this, SuggestedDailyActivity.class);
        SuggestedDailyActivity.this.startActivity(refreshPage);
    }


    public void filterActivity() {

        isActivity = true;

        SuggestedDailyActivity.FamilyConnectFetchTask taskPost = new SuggestedDailyActivity.FamilyConnectFetchTask();
        String uriGetActivity ="https://family-connect-ggc-2017.herokuapp.com/users";

        taskPost.execute(uriGetActivity);
    }


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
            if (isActivity && HomeTab.getTemperature() != null) {

                try {
                    String line;
                    URL url = new URL(params[0] + "/" + UserLoginActivity.getID() + "/activities");
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
                                activities.getTempLow(), activities.getTempHi(), activities.getCategory(), "N/A", activities.getIsCompleted());

                        //Filter temperature
                        if(Double.parseDouble(HomeTab.getTemperature()) > Double.parseDouble(activities.getTempLow()) &&
                                Double.parseDouble(HomeTab.getTemperature()) < Double.parseDouble(activities.getTempHi())) {

                            //Filter Weather Status
                            if(HomeTab.getIcon().equals(activities.getIcon())) {

                                //Filter If Activity Has Not Been Completed
                                if(!activities.getIsCompleted())
                                    isActivityFound = true;
                                    suggestedList.add(activity.toString());
                            }
                        }
                    }

                    if(suggestedList.size() > 0) {
                        String splitList[] = suggestedList.toString().split("\n");

                        Random randomActivity = new Random();
                        int value = randomActivity.nextInt(suggestedList.size());

                        String splitActivityAttributes[] = splitList[value].substring(1, splitList[value].length()).split(",");

                        activityName = splitActivityAttributes[0];
                        activityCondition = splitActivityAttributes[2];
                        activityTempLow = splitActivityAttributes[3];
                        activityTempHigh = splitActivityAttributes[4];

                        Log.v("SUGGESTED HIGH: ", activityCondition);
                        Log.v("SUGGESTED ACTIVITY: ", splitList[value].substring(1, splitList[value].length()));
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

            if(isActivityFound) {
                weatherTemp.setText("Temperature: " + "High: " + ((int) Double.parseDouble(activityTempHigh)) + "°F " + "Low: " + ((int) Double.parseDouble(activityTempLow)) + "°F");
                weatherCondition.setText("Weather Condition:" + activityCondition);
                weatherSummary.setText("Weather Summary: " + HomeTab.getSummary());
                activityTitle.setText(activityName);
            }
            else {
                activityTitle.setText("No Activities Found Today");
            }
        }
    }

    @Override
    public void onBackPressed() {
        HomeTab.setRunOnce(true);
        Intent homePage = new Intent(SuggestedDailyActivity.this, GroupedActivities.class);
        SuggestedDailyActivity.this.startActivity(homePage);

    }

}
