package familyconnect.familyconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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
import java.util.List;
import java.util.Scanner;

import familyconnect.familyconnect.Widgets.DatePickerFragment;
import familyconnect.familyconnect.Widgets.TimePickerFragment;
import familyconnect.familyconnect.json.FamilyConnectActivitiesHttpResponse;

public class CompletedActivities extends AppCompatActivity {

    private static ListView scrollView;
    private List<String> completedActivityNames;
    private List<Activity> completedActivityList;
    private static String activityDetailsTitle, activityWeatherIcon, activityWeatherSummary,
            activityWeatherLow, activityWeatherHigh, activityCategory, activityGroup;
    private static boolean activityComplete;
    private static long activityId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_activities);

        scrollView = (ListView) findViewById(R.id.activityScroll);

        completedActivityList = new ArrayList<Activity>();
        completedActivityNames = new ArrayList<String>();


        completedActivityNames.clear();
        completedActivityList.clear();

        for(int i = 0; i < DisplayActivitiesTab.getActivityList().size(); i++) {

            if(DisplayActivitiesTab.activityList.get(i).getCompleted() == true) {

                completedActivityNames.add(DisplayActivitiesTab.activityList.get(i).getName());

                Activity activity = new Activity(DisplayActivitiesTab.activityList.get(i).getId(), DisplayActivitiesTab.activityList.get(i).getName(),
                        DisplayActivitiesTab.activityList.get(i).getWeatherIcon(), DisplayActivitiesTab.activityList.get(i).getWeatherSummary(),
                        DisplayActivitiesTab.activityList.get(i).getTempLow(), DisplayActivitiesTab.activityList.get(i).getTempHigh(),
                        DisplayActivitiesTab.activityList.get(i).getCategory(),"N/A", DisplayActivitiesTab.activityList.get(i).getCompleted());

                completedActivityList.add(activity);
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CompletedActivities.this,
                R.layout.completedactivitylist, R.id.listText, completedActivityNames);

        scrollView.setAdapter(arrayAdapter);

        scrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String  itemValue = (String) scrollView.getItemAtPosition(position);

                activityId = completedActivityList.get(itemPosition).getId();
                activityDetailsTitle = completedActivityList.get(itemPosition).getName();
                activityWeatherIcon = completedActivityList.get(itemPosition).getWeatherIcon();
                activityWeatherSummary = completedActivityList.get(itemPosition).getWeatherSummary();
                activityWeatherLow = completedActivityList.get(itemPosition).getTempLow();
                activityWeatherHigh = completedActivityList.get(itemPosition).getTempHigh();
                activityCategory = completedActivityList.get(itemPosition).getCategory();
                activityGroup = completedActivityList.get(itemPosition).getGroup();
                activityComplete = completedActivityList.get(itemPosition).getCompleted();

                Intent showCompletedActivityPage = new Intent(CompletedActivities.this, CompletedActivityDetails.class);
                CompletedActivities.this.startActivity(showCompletedActivityPage);
            }
        });

    }

    public static long getActivityId() {
        return activityId;
    }
    public static String getActivityDetailsTitle() {
        return activityDetailsTitle;
    }
    public static String getActivityWeatherIcon() {
        return activityWeatherIcon;
    }
    public static String getActivityWeatherSummary() {
        return activityWeatherSummary;
    }
    public static String getActivityWeatherLow() {
        return activityWeatherLow;
    }
    public static String getActivityWeatherHigh() {
        return activityWeatherHigh;
    }
    public static String getActivityCategory() {
        return activityCategory;
    }
    public static String getActivityGroup() { return activityGroup; }
    public static boolean getActivityComplete() { return activityComplete; }

}
