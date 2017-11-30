package familyconnect.familyconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * ActivityDetails.java - a class that displays the details of each activity selected.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class ActivityDetails extends AppCompatActivity implements View.OnClickListener {

    private TextView activityTitle, weatherSummary, highTemp, lowTemp, group, category;
    private Button buttonDelete, buttonComplete;
    private ImageView weatherIcon;
    private boolean PUT, DELETE = false;
    private RequestQueue queue;


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
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityTitle = findViewById(R.id.activityDetailsTitle);
        weatherIcon = findViewById(R.id.weatherImage);
        weatherSummary = findViewById(R.id.weatherSummary);
        lowTemp = findViewById(R.id.low_temp_desc);
        highTemp = findViewById(R.id.high_temp_desc);
        group = findViewById(R.id.groupDesc);
        category = findViewById(R.id.categoryDesc);
        buttonDelete = findViewById(R.id.x_button);
        buttonComplete = findViewById(R.id.check_button);

        buttonDelete.setOnClickListener(this);
        buttonComplete.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);

        activityTitle.setText(DisplayActivitiesTab.getActivityDetailsTitle() + " Details");
        setWeatherImage(DisplayActivitiesTab.getActivityWeatherIcon());
        weatherSummary.setText(DisplayActivitiesTab.getActivityWeatherSummary());
        lowTemp.setText(DisplayActivitiesTab.getActivityWeatherLow() + " °F");
        highTemp.setText(DisplayActivitiesTab.getActivityWeatherHigh() + " °F");
        category.setText(DisplayActivitiesTab.getActivityCategory());
        group.setText(DisplayActivitiesTab.getActivityGroup());
    }

    /**
     * @method onClick()
     *
     * This method is a click listener that listens for what buttons are pressed.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.x_button:

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Delete Activity")
                        .setMessage("Are you sure you want to delete this " + DisplayActivitiesTab.getActivityDetailsTitle() + " activity?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DELETE = true;
                                PUT = false;

                                ActivityDetails.FamilyConnectFetchTask taskGet = new ActivityDetails.FamilyConnectFetchTask();
                                String uriDelete ="https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/" + GroupsTab.getGroupID() + "/activities";
                                taskGet.execute(uriDelete);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;

            case R.id.check_button:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Complete Activity")
                        .setMessage("Are you sure you want to complete this " + DisplayActivitiesTab.getActivityDetailsTitle() + " activity?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DELETE = false;
                                PUT = true;

                                ActivityDetails.FamilyConnectFetchTask taskGet = new ActivityDetails.FamilyConnectFetchTask();
                                String uriPut ="https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/" + GroupsTab.getGroupID() + "/activities";
                                taskGet.execute(uriPut);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
        }
    }

    /**
     * @method setWeatherImage()
     *
     * This method sets the icon that corresponds to what weather condition is chosen.
     *
     * @param condition
     */
    public void setWeatherImage(String condition) {

        switch (condition) {

            case "clear-day":

                weatherIcon.setImageResource(R.drawable.clear_day_icon);
                break;

            case "clear-night":

                weatherIcon.setImageResource(R.drawable.clear_night_icon);
                break;

            case "partly-cloudy-day":

                weatherIcon.setImageResource(R.drawable.partly_cloudy_day_icon);
                break;

            case "partly-cloudy-night":

                weatherIcon.setImageResource(R.drawable.partly_cloudy_night_icon);
                break;

            case "cloudy":

                weatherIcon.setImageResource(R.drawable.cloudy_icon);
                break;

            case "rain":

                weatherIcon.setImageResource(R.drawable.rain_icon);
                break;

            case "sleet":

                weatherIcon.setImageResource(R.drawable.sleet_icon);
                break;

            case "snow":

                weatherIcon.setImageResource(R.drawable.snow_icon);
                break;

            case "wind":

                weatherIcon.setImageResource(R.drawable.wind_icon);
                break;

            case "fog":

                weatherIcon.setImageResource(R.drawable.fog_icon);
                break;

            case "Indoors":

                weatherIcon.setImageResource(R.drawable.indoor_icon);
                break;
        }
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

            //PUT REQUEST
            if (PUT) {

                final JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("isCompleted", true);

                } catch (JSONException je) {
                    je.printStackTrace();
                }

                JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT,
                        params[0] + "/" + DisplayActivitiesTab.getActivityId(), jsonObject,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // response
                                Log.d("PUT", response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }
                ) {

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("X-User-Email", UserLoginActivity.getEmail());
                        headers.put("X-User-Token", UserLoginActivity.getToken());

                        return headers;
                    }

                    @Override
                    public byte[] getBody() {

                        try {
                            return jsonObject.toString().getBytes("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };

                queue.add(putRequest);
            }

            //DELETE REQUEST
            if (DELETE) {

                final JSONObject jsonObject = new JSONObject();

                JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE,
                        params[0] + "/" + DisplayActivitiesTab.getActivityId(), jsonObject,

                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                // response
                                Log.d("DELETE", response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("X-User-Email", UserLoginActivity.getEmail());
                        headers.put("X-User-Token", UserLoginActivity.getToken());

                        return headers;
                    }
                };

                queue.add(deleteRequest);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            HomeTab.setRunOnce(true);
            Intent displayActivityPage = new Intent(ActivityDetails.this, GroupedActivities.class);
            ActivityDetails.this.startActivity(displayActivityPage);
        }
    }
}
