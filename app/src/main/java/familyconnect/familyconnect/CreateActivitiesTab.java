package familyconnect.familyconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.support.design.widget.Snackbar;
import android.widget.Toast;

import familyconnect.familyconnect.Widgets.DatePickerFragment;
import familyconnect.familyconnect.Widgets.TimePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class CreateActivitiesTab extends Fragment implements View.OnClickListener {

    private static EditText name, date, time, weather, category, group = null;

    private String readName, readDate, readTime, readAllWeather, readWeatherDegrees, readWeatherSummary,
            readWeatherIcon, readCategory, readGroup = null;
    private boolean POST, GET = false;
    private static boolean isCreated = false;
    private RequestQueue queue;
    private double longitude, latitude;
    private GPSLocation gps;

    private static Button weatherButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.createactivitiestab, container, false);

        name = rootView.findViewById(R.id.activityName);
        date = rootView.findViewById(R.id.activityDate);
        time = rootView.findViewById(R.id.activityTime);
        weather = rootView.findViewById(R.id.activityWeather);
        category = rootView.findViewById(R.id.activityCategory);
        group = rootView.findViewById(R.id.activityGroup);
        weatherButton = rootView.findViewById(R.id.weatherButton);

        //Allows Text Fields to be clicked
        date.setOnClickListener(this);
        time.setOnClickListener(this);

        //Invisible Weather Button
        weatherButton.setOnClickListener(this);
        weatherButton.setVisibility(View.GONE);

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        getGPSLocation();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                readName = name.getText().toString();
                readDate = date.getText().toString();
                readTime = time.getText().toString();
                readAllWeather = weather.getText().toString();
                readCategory = category.getText().toString();
                readGroup = group.getText().toString();


                if (readName.matches("") || readDate.matches("") || readTime.matches("") || readAllWeather.matches("") ||
                        readAllWeather.matches("Loading Temperature...") || readWeatherDegrees != null || readWeatherIcon != null ||
                        readCategory.matches("") || readGroup.matches("")) {

                    Snackbar.make(view, "Please fill out activity fields!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    isCreated = true;

                    FamilyConnectFetchTask taskPost = new FamilyConnectFetchTask();
                    String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users/1/activities";
                    //String uriPost ="https://myprojects-mikeh87.c9users.io/users/1/activities";
                    taskPost.execute(uriPost);
                    POST = true;
                    GET = false;

                    Snackbar.make(view, "Activity Created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.activityDate:
                showDatePickerDialog(v);
                break;

            case R.id.activityTime:
                showTimePickerDialog(v);
                break;

            case R.id.weatherButton:
                showWeatherDialog();
                break;
        }
    }


    public void showWeatherDialog() {
        FamilyConnectFetchTask taskPost = new FamilyConnectFetchTask();
        //URI REQUEST: DOMAIN   /KEY   /LAT,LON   ,YYYY-MM-DD   T   HH:MM:SS
        String uriGet = "https://api.darksky.net/forecast/879da94f7c402798f1bf303383070b27/" + latitude + "," + longitude + "," +
                DatePickerFragment.getSpecialDateFormat() + "T" + TimePickerFragment.getSpecialFormatTime();

        Log.v("URI" ,uriGet);
        taskPost.execute(uriGet);

        if((!date.getText().toString().matches("")) && (!time.getText().toString().matches(""))) {
            weather.setText("Loading Temperature...");
        }

        GET = true;
        POST = false;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");
    }


    public void getGPSLocation() {

        gps = new GPSLocation(getActivity().getApplicationContext());
        Location location = gps.getLocation();

        if(location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Toast.makeText(getActivity().getApplicationContext(), "Your Location is - \nLat: " +
                    latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }
        else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            // Setting Dialog Title
            alertDialog.setTitle("GPS Settings");
            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(intent);
                }
            });
            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }
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

                    // Read in the data
                    int user_id = 1;
                    String activityName = readName;
                    String activityDate = readDate;
                    String activityTime = readTime;
                    String activityWeatherDegrees = readWeatherDegrees;
                    String activityWeatherSummary = readWeatherSummary;
                    String activityWeatherIcon = readWeatherIcon;
                    String activityCategory = readCategory;
                    String activityGroupName = readGroup;
                    String created = "2017-10-29T22:12:17.391Z";
                    String updated = "2017-10-29T22:12:17.391Z";

                    String urlParameters = "activitie_name=" + activityName + "&user_id=" + user_id +
                            "&created_at=" + created + "&updated_at=" + updated;

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

            //GET REQUEST FOR WEATHER
            if (GET) {

                final JSONObject jsonObject = new JSONObject();

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, params[0], jsonObject,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    JSONObject currently = response.getJSONObject("currently");

                                    readWeatherDegrees = String.valueOf(currently.getInt("temperature"));
                                    readWeatherSummary = currently.getString("summary");
                                    readWeatherIcon = currently.getString("icon");

                                    weather.setText("The temperature at " + TimePickerFragment.getTime() + " is " + readWeatherDegrees + " °F" + "\n" +
                                    "Condition: " + readWeatherSummary);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                queue.add(getRequest);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }
    }


    public static EditText getName() {
        return name;
    }

    public static void setName(EditText name) {
        CreateActivitiesTab.name = name;
    }

    public static EditText getDate() {
        return date;
    }

    public static void setDate(EditText date) {
        CreateActivitiesTab.date = date;
    }

    public static EditText getTime() {
        return time;
    }

    public static void setTime(EditText time) {
        CreateActivitiesTab.time = time;
    }

    public static EditText getWeather() {
        return weather;
    }

    public static void setWeather(EditText weather) {
        CreateActivitiesTab.weather = weather;
    }

    public static EditText getCategory() {
        return category;
    }

    public static void setCategory(EditText category) {
        CreateActivitiesTab.category = category;
    }

    public static EditText getGroup() {
        return group;
    }

    public static void setGroup(EditText group) {
        CreateActivitiesTab.group = group;
    }

    public static void setCreated(boolean created) {
        isCreated = created;
    }

    public static boolean getIsCreated() {
        return isCreated;
    }

    public static Button getWeatherButton() {
        return weatherButton;
    }

}

