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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import familyconnect.familyconnect.json.FamilyConnectHttpResponse;

public class ActivityDetails extends AppCompatActivity implements View.OnClickListener {

    private TextView activityTitle, weatherSummary, weatherTemp;
    private Button buttonDelete, buttonComplete;
    private ImageView weatherIcon;
    private boolean PUT, DELETE = false;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityTitle = (TextView) findViewById(R.id.activityDetailsTitle);
        activityTitle.setText(DisplayActivitiesTab.getActivityDetailsTitle() + " Details");

        weatherIcon = (ImageView) findViewById(R.id.weatherImage);
        weatherSummary = (TextView) findViewById(R.id.weatherSummary);
        weatherTemp = (TextView) findViewById(R.id.weatherTemp);

        buttonDelete = (Button) findViewById(R.id.x_button);
        buttonComplete = (Button) findViewById(R.id.check_button);

        buttonDelete.setOnClickListener(this);
        buttonComplete.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);


//        setWeatherImage(DisplayActivitiesTab.getActivityWeatherIcon());
//        weatherTemp.setText(DisplayActivitiesTab.getActivityWeatherTemp());
//        weatherSummary.setText(DisplayActivitiesTab.getActivityWeatherSummary());

        setWeatherImage("partly-cloudy-day");
        weatherTemp.setText("75 °F");
        weatherSummary.setText("Partly Cloudy");


    }

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
                                ActivityDetails.FamilyConnectFetchTask taskGet = new ActivityDetails.FamilyConnectFetchTask();
                                String uriDelete ="https://family-connect-ggc-2017.herokuapp.com/users/1/activities";
                                taskGet.execute(uriDelete);

                                DELETE = true;
                                PUT = false;
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

                break;
        }
    }

    public void setWeatherImage(String condition) {

        //"clear-day", "clear-night", "partly-cloudy-day","partly-cloudy-night", "cloudy", "rain", "sleet", "snow", "wind","fog"

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

//            case "rain":
//
//                weatherIcon.setImageResource(R.drawable.);
//                break;
//
//            case "sleet":
//
//                weatherIcon.setImageResource(R.drawable.);
//                break;
//
//            case "snow":
//
//                weatherIcon.setImageResource(R.drawable.);
//                break;
//
//            case "wind":
//
//                weatherIcon.setImageResource(R.drawable.);
//                break;
//
//            case "fog":
//
//                weatherIcon.setImageResource(R.drawable.);
//                break;

            default:

               // weatherIcon.setImageResource(R.drawable.);
                break;
        }
    }


    private class FamilyConnectFetchTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v("FamilyConnect", "URL = " + params[0]);

            //PUT REQUEST
            if (PUT) {

                final JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("user_name", "JawanHiggins");
                    jsonObject.put("email", "Jawan@yahoo.com");
                    jsonObject.put("created_at", "2017-10-31T01:05:55.429Z");
                    jsonObject.put("updated_at", "2017-10-31T01:05:55.429Z");

                }  catch (JSONException je) {
                    je.printStackTrace();
                }

                JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, params[0] + "/3", jsonObject,
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
                        headers.put("Accept", "application/json");

                        return headers;
                    }

                    @Override
                    public byte[] getBody() {

                        try {
                            //Log.i("json", jsonObject.toString());
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
            else if (DELETE) {

                final JSONObject jsonObject = new JSONObject();

                JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE, params[0] + "/6", jsonObject,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                Toast.makeText(getApplicationContext(),activityTitle.getText(), Toast.LENGTH_SHORT).show();

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
                );

                queue.add(deleteRequest);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            Intent displayActivityPage = new Intent(ActivityDetails.this, GroupedActivities.class);
            ActivityDetails.this.startActivity(displayActivityPage);

        }

    }

}
