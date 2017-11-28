package familyconnect.familyconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;
import java.util.Scanner;
import familyconnect.familyconnect.json.FamilyConnectActivitiesHttpResponse;

/**
 * DisplayActivitiesTab.java - a class that displays all the activities that are created and not completed.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class DisplayActivitiesTab extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView scrollView;
    private TextView groupsTitle, loadingText;
    private TextView completedActivitiesBtn;
    private ImageView starBtn;
    private boolean GET = false;
    protected List<String> jsonArray;
    public static List<Activity> activityList, nonCompletedActivityList;
    private static String activityDetailsTitle, activityWeatherIcon, activityWeatherSummary,
            activityWeatherLow, activityWeatherHigh, activityCategory, activityGroup;
    private static boolean activityComplete;
    private static long activityId;


    /**
     * @method onCreate()
     *
     * This method creates the android activity and initializes each instance variable.
     *
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.displayactivitiestab, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        scrollView = rootView.findViewById(R.id.activityScroll);
        groupsTitle = rootView.findViewById(R.id.groupsTitle);
        loadingText = rootView.findViewById(R.id.loading);
        completedActivitiesBtn = rootView.findViewById(R.id.completedActivities);
        starBtn = rootView.findViewById(R.id.star_icon);

        jsonArray = new ArrayList<String>();
        activityList = new ArrayList<Activity>();
        nonCompletedActivityList = new ArrayList<Activity>();

        completedActivitiesBtn.setOnClickListener(this);
        starBtn.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);


        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent createActivityPage = new Intent(getActivity(), CreateActivity.class);
                DisplayActivitiesTab.this.startActivity(createActivityPage);
            }
        });

        return rootView;
    }

    /**
     * @method onRefresh()
     *
     * This method refreshes the activity display list to update the list.
     */
    @Override
    public void onRefresh() {

        loadingText.setText("Loading Activities...");

        //Clear JSON and Activity array to Sync the list
        jsonArray.clear();
        activityList.clear();
        nonCompletedActivityList.clear();

        GET = true;

        if(GroupsTab.getGroupID() == 0) {
            loadingText.setText("No Activities");
            groupsTitle.setText("Group Activities");
            swipeRefreshLayout.setRefreshing(false);

            Toast.makeText(getActivity(), "Please consider joining or creating a group first!",
                    Toast.LENGTH_LONG).show();
        }
        else {
            DisplayActivitiesTab.FamilyConnectFetchTask taskGet = new DisplayActivitiesTab.FamilyConnectFetchTask();
            String uriGet = "https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/" + GroupsTab.getGroupID() + "/activities";
            taskGet.execute(uriGet);
        }

    }

    /**
     * @method setUserVisibleHint()
     *
     * This method notifies the DOM when the Display Tab is shown.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            //Clear JSON and Activity array to Sync the list
            jsonArray.clear();
            activityList.clear();
            nonCompletedActivityList.clear();

            loadingText.setText("Loading Activities...");

            if(GroupsTab.getGroupID() == 0) {
                loadingText.setText("No Activities");
                groupsTitle.setText("Group Activities");
                scrollView.requestLayout();

                Toast.makeText(getActivity(), "Please consider joining or creating a group first!",
                        Toast.LENGTH_LONG).show();
            }
            else {
                GET = true;

                groupsTitle.setText(GroupsTab.getGroupsDropdownValue() + " Group Activities");

                DisplayActivitiesTab.FamilyConnectFetchTask taskGet = new DisplayActivitiesTab.FamilyConnectFetchTask();
                String uriGet = "https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/" + GroupsTab.getGroupID() + "/activities";
                taskGet.execute(uriGet);
            }

        }
        else {
            //Do Nothing
        }
    }

    /**
     * @method onClick()
     *
     * This method listens for buttons that are clicked.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent completedActivityPage = new Intent(getActivity(), CompletedActivities.class);
        DisplayActivitiesTab.this.startActivity(completedActivityPage);
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

            Log.v("FamilyConnect", "URI = " + params[0]);

            //GET REQUEST
            if (GET) {

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

                        Activity activity;

                        if(GroupsTab.getGroupID() == 0) {
                            activity = new Activity(activities.getId(), activities.getActivitieName(), activities.getIcon(), activities.getCondition(),
                                    activities.getTempLow(), activities.getTempHi(), activities.getCategory(), "N/A", activities.getIsCompleted());
                        }
                        else {
                            activity = new Activity(activities.getId(), activities.getActivitieName(), activities.getIcon(), activities.getCondition(),
                                    activities.getTempLow(), activities.getTempHi(), activities.getCategory(), GroupsTab.getGroupsDropdownValue(), activities.getIsCompleted());
                        }

                        activityList.add(0, activity);

                        //A check to only display Activities that are not completed yet
                        if(activities.getIsCompleted() == false) {

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

                // Stopping swipe refresh
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
    public static String getActivityWeatherSummary() { return activityWeatherSummary; }
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
    public static List getActivityList() { return activityList; }
    public static List getNonCompletedActivityList() {
        return nonCompletedActivityList;
    }
}
