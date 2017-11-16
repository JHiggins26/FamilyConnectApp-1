package familyconnect.familyconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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


public class DisplayActivitiesTab extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private static ListView scrollView;
    private TextView loadingText;
    private RequestQueue queue;
    private boolean GET = false;
    protected List<String> jsonArray;
    public static List<Activity> activityList, nonCompletedActivityList;
    private static String activityDetailsTitle, activityWeatherIcon, activityWeatherSummary,
            activityWeatherLow, activityWeatherHigh, activityCategory, activityGroup, activityComplete;
    private static long activityId;
    private TextView completedActivitiesBtn;
    private ImageView starBtn;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.displayactivitiestab, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        scrollView = rootView.findViewById(R.id.activityScroll);
        loadingText = rootView.findViewById(R.id.loading);

        completedActivitiesBtn = rootView.findViewById(R.id.completedActivities);
        completedActivitiesBtn.setOnClickListener(this);

        starBtn = rootView.findViewById(R.id.star_icon);
        starBtn.setOnClickListener(this);

        jsonArray = new ArrayList<String>();
        activityList = new ArrayList<Activity>();
        nonCompletedActivityList = new ArrayList<Activity>();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent createActivityPage = new Intent(getActivity(), CreateActivity.class);
                DisplayActivitiesTab.this.startActivity(createActivityPage);
            }
        });

        return rootView;
    }


    @Override
    public void onRefresh() {

        loadingText.setText("Loading Activities...");

        //Clear JSON and Activity array to Sync the list
        jsonArray.clear();
        activityList.clear();

        GET = true;

        DisplayActivitiesTab.FamilyConnectFetchTask taskGet = new DisplayActivitiesTab.FamilyConnectFetchTask();
        String uriGet ="https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/activities";
        taskGet.execute(uriGet);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            loadingText.setText("Loading Activities...");

            //Clear JSON and Activity array to Sync the list
            jsonArray.clear();
            activityList.clear();

            GET = true;

            DisplayActivitiesTab.FamilyConnectFetchTask taskGet = new DisplayActivitiesTab.FamilyConnectFetchTask();
            String uriGet ="https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/activities";
            taskGet.execute(uriGet);

            if(CreateActivitiesTab.getIsCreated()) {

                CreateActivitiesTab.getName().setText("");
                CreateActivitiesTab.getDate().setText("");
                CreateActivitiesTab.getTime().setText("");
                CreateActivitiesTab.getWeather().setText("");
                CreateActivitiesTab.getCategory().setText("");
                CreateActivitiesTab.getGroup().setText("");

                DatePickerFragment.setSpecialDateFormat("");
                TimePickerFragment.setSpecialFormatTime("");

                CreateActivitiesTab.setCreated(false);
            }
        }
        else {
            //Do Nothing
        }
    }

    @Override
    public void onClick(View v) {
        Intent completedActivityPage = new Intent(getActivity(), CompletedActivities.class);
        DisplayActivitiesTab.this.startActivity(completedActivityPage);
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

            Log.v("FamilyConnect", "URL = " + params[0]);

            //GET REQUEST
            if (GET) {

                try {
                    String line;
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.addRequestProperty("X-Email", UserLoginActivity.getEmail());
                    connection.addRequestProperty("X-User-Token", UserLoginActivity.getToken());
                    connection.setDoOutput(true);
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

                        Activity activity = new Activity(activities.getId(), activities.getActivitieName(),"ICON", activities.getCondition(),
                                activities.getTempLow(), activities.getTempHi(), activities.getCategory(), "N/A", activities.getUrl());

                        activityList.add(0, activity);

                        //A check to only display Activities that are not completed yet
                        if(activities.getUrl().matches("false")) {

                            nonCompletedActivityList.add(0, activity);

                            //JsonArray is just for the Individual Activity Title on the Activity Details Page
                            jsonArray.add(0, activities.getActivitieName());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

                return null;
            }


            @Override
            protected void onPostExecute (Bitmap bitmap){
                super.onPostExecute(bitmap);

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

                //Deletes the Loading Text when Activities appear
                loadingText.setText("");

                if(nonCompletedActivityList.size() == 0) {
                    loadingText.setText("No Activities");
                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                            R.layout.displayactivitylist, R.id.listText, jsonArray);

                scrollView.setAdapter(arrayAdapter);

                scrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int itemPosition = position;
                        String  itemValue = (String) scrollView.getItemAtPosition(position);

                        activityId = nonCompletedActivityList.get(itemPosition).getId();
                        activityDetailsTitle = nonCompletedActivityList.get(itemPosition).getName();
                        activityWeatherIcon = nonCompletedActivityList.get(itemPosition).getWeatherIcon();
                        activityWeatherSummary = nonCompletedActivityList.get(itemPosition).getWeatherSummary();
                        activityWeatherLow = nonCompletedActivityList.get(itemPosition).getTempLow();
                        activityWeatherHigh = nonCompletedActivityList.get(itemPosition).getTempHigh();
                        activityCategory = nonCompletedActivityList.get(itemPosition).getCategory();
                        activityGroup = nonCompletedActivityList.get(itemPosition).getGroup();
                        activityComplete = nonCompletedActivityList.get(itemPosition).getCompleted();

                        Intent showActivityPage = new Intent(getActivity(), ActivityDetails.class);
                        DisplayActivitiesTab.this.startActivity(showActivityPage);
                    }
                });

            }
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
    public static String getActivityComplete() { return activityComplete; }
    public static List getActivityList() { return activityList; }

}
