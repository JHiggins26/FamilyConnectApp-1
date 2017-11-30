package familyconnect.familyconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


/**
 * CompletedActivites.java - a class displays the activities that are marked completed.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class CompletedActivities extends AppCompatActivity {

    private ListView scrollView;
    private TextView groupsTitle, completedText;
    private List<String> completedActivityNames;
    private List<Activity> completedActivityList;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_activities);

        scrollView = findViewById(R.id.activityScroll);
        groupsTitle = findViewById(R.id.groupsTitle);
        completedText = findViewById(R.id.no_completed_activity);

        completedActivityList = new ArrayList<Activity>();
        completedActivityNames = new ArrayList<String>();

        if(GroupsTab.getGroupID() == 0) {
            groupsTitle.setText("Activity Group");
        }
        else {
            groupsTitle.setText(GroupsTab.getGroupsDropdownValue() + " Group");

        }

        displayCompletedActivities();
    }

    /**
     * @method displayCompletedActivities
     *
     * This method displays all activities that are marked completed.
     */
    public void displayCompletedActivities() {

        int countCompleted = 0;

        completedActivityNames.clear();
        completedActivityList.clear();

        if(DisplayActivitiesTab.getActivityList().size() == 0) {
            completedText.setText("No Completed Activities");
        }

        for(int i = 0; i < DisplayActivitiesTab.getActivityList().size(); i++) {

            if(DisplayActivitiesTab.activityList.get(i).getCompleted() == true) {

                completedActivityNames.add(DisplayActivitiesTab.activityList.get(i).getName());

                Activity activity;

                if(GroupsTab.getGroupID() == 0) {
                    activity = new Activity(DisplayActivitiesTab.activityList.get(i).getId(), DisplayActivitiesTab.activityList.get(i).getName(),
                            DisplayActivitiesTab.activityList.get(i).getWeatherIcon(), DisplayActivitiesTab.activityList.get(i).getWeatherSummary(),
                            DisplayActivitiesTab.activityList.get(i).getTempLow(), DisplayActivitiesTab.activityList.get(i).getTempHigh(),
                            DisplayActivitiesTab.activityList.get(i).getCategory(), "N/A", DisplayActivitiesTab.activityList.get(i).getCompleted());
                }
                else {
                    activity = new Activity(DisplayActivitiesTab.activityList.get(i).getId(), DisplayActivitiesTab.activityList.get(i).getName(),
                            DisplayActivitiesTab.activityList.get(i).getWeatherIcon(), DisplayActivitiesTab.activityList.get(i).getWeatherSummary(),
                            DisplayActivitiesTab.activityList.get(i).getTempLow(), DisplayActivitiesTab.activityList.get(i).getTempHigh(),
                            DisplayActivitiesTab.activityList.get(i).getCategory(), GroupsTab.getGroupsDropdownValue(), DisplayActivitiesTab.activityList.get(i).getCompleted());
                }

                completedActivityList.add(activity);

                completedText.setText("");

                countCompleted++;
            }
            else {
                if(countCompleted > 0) {
                    //Do Nothing
                }
                else {
                    completedText.setText("No Completed Activities");
                }
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
