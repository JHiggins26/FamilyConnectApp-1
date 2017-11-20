package familyconnect.familyconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import java.util.Scanner;
import java.util.Timer;

import familyconnect.familyconnect.Widgets.DatePickerFragment;
import familyconnect.familyconnect.Widgets.TimePickerFragment;
import familyconnect.familyconnect.json.FamilyConnectActivitiesHttpResponse;


public class HomeTab extends Fragment implements View.OnClickListener {

    private TextView username, degrees;
    private ImageButton settings;
    private PopupWindow settingsWindow;
    private LayoutInflater layoutInflater;

    //All are part of ONE ACTIVITY BUTTON (BG, IMG, IMG, TEXT)
    private ImageView viewActivities1, viewActivities2, viewActivities3;
    private TextView viewActivities4;

    //All are part of ONE CALENDAR BUTTON (BG, IMG, IMG, TEXT)
    private ImageView viewCalendar1, viewCalendar2, viewCalendar3;
    private TextView viewCalendar4;

    private Animation animRotate, animScale;
    private AnimationSet setAnim;
    private ImageButton dailyBtn;

    private MediaPlayer dailyActivitySound;

    private boolean isWeather = false;
    private RequestQueue queue;
    private double longitude, latitude;
    private GPSLocation gps;
    private static String temperature = " ", summary, icon;
    private static boolean RUN_ONCE = true;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hometab, container, false);

        queue = Volley.newRequestQueue(getActivity());

        degrees = rootView.findViewById(R.id.degrees);

        settings = rootView.findViewById(R.id.settings_icon);
        settings.setOnClickListener(this);

        username = rootView.findViewById(R.id.usernameTitle);
        username.setText("Welcome, " + UserLoginActivity.getUsername() + "!");

        //All are part of ONE ACTIVITY BUTTON (BG, IMG, IMG, TEXT)
        viewActivities1 = rootView.findViewById(R.id.listBGimage);
        viewActivities1.setOnClickListener(this);
        viewActivities2 = rootView.findViewById(R.id.arrow);
        viewActivities2.setOnClickListener(this);
        viewActivities3 = rootView.findViewById(R.id.activity_icon);
        viewActivities3.setOnClickListener(this);
        viewActivities4 = rootView.findViewById(R.id.listText);
        viewActivities4.setOnClickListener(this);

        //All are part of ONE CALENDAR BUTTON (BG, IMG, IMG, TEXT)
        viewCalendar1 = rootView.findViewById(R.id.calendarBGimage);
        viewCalendar1.setOnClickListener(this);
        viewCalendar2 = rootView.findViewById(R.id.calendar_arrow);
        viewCalendar2.setOnClickListener(this);
        viewCalendar3 = rootView.findViewById(R.id.calendar_icon);
        viewCalendar3.setOnClickListener(this);
        viewCalendar4 = rootView.findViewById(R.id.calendar_text);
        viewCalendar4.setOnClickListener(this);


        dailyBtn = rootView.findViewById(R.id.suggest_daily_activity_icon);
        dailyBtn.setOnClickListener(this);

        //Animations
        animRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
        animScale = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_scale);

        setAnim = new AnimationSet(false);
        setAnim.addAnimation(animRotate);
        setAnim.addAnimation(animScale);

        dailyActivitySound = MediaPlayer.create(getActivity(), R.raw.daily_activity_sound);

        getGPSLocation();
        getWeather();


        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if (RUN_ONCE) {
                RUN_ONCE = false;
            }
            else  {
                getGPSLocation();
                getWeather();
            }
        }
        else {
            //Do Nothing
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.settings_icon:
                layoutInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.settings_menu, null);

                settingsWindow = new PopupWindow(container, 400, 400, true);
                settingsWindow.showAtLocation(v, Gravity.NO_GRAVITY, (int)settings.getX()-300, (int)settings.getY()+320);

                TextView logOut = container.findViewById(R.id.logOut);
                logOut.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getActivity());
                        }
                        builder.setTitle("Log Out")
                                .setMessage("Are you sure you want to Log Out?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        RUN_ONCE = true;
                                        Intent loginPage = new Intent(getActivity(), UserLoginActivity.class);
                                        HomeTab.this.startActivity(loginPage);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        settingsWindow.dismiss();

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        settingsWindow.dismiss();
                        return true;
                    }
                });

                break;

            case (R.id.listBGimage):
                GroupedActivities.getViewPager().setCurrentItem(1);
                break;

            case R.id.arrow:
                GroupedActivities.getViewPager().setCurrentItem(1);
                break;

            case R.id.activity_icon:
                GroupedActivities.getViewPager().setCurrentItem(1);
                break;

            case R.id.listText:
                GroupedActivities.getViewPager().setCurrentItem(1);
                break;

            case (R.id.calendarBGimage):

                break;

            case R.id.calendar_arrow:

                break;

            case R.id.calendar_icon:

                break;

            case R.id.calendar_text:

                break;

            case R.id.suggest_daily_activity_icon:

                v.startAnimation(setAnim);
                dailyActivitySound.start();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                Intent suggestedActivityPage = new Intent(getActivity(), SuggestedDailyActivity.class);
                                HomeTab.this.startActivity(suggestedActivityPage);
                            }
                        }, 1200);
                break;

        }
    }


    public void getWeather() {

        isWeather = true;

        HomeTab.FamilyConnectFetchTask taskPost = new HomeTab.FamilyConnectFetchTask();
        //URI REQUEST: DOMAIN   /KEY   /LAT,LON
        String uriGetForecast = "https://api.darksky.net/forecast/879da94f7c402798f1bf303383070b27/" + latitude + "," + longitude;

        taskPost.execute(uriGetForecast);
    }


    public void getGPSLocation() {

        gps = new GPSLocation(getActivity());
        Location location = gps.getLocation();

        if(location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            /*Toast.makeText(getActivity(), "Your Location is - \nLat: " +
                    latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();*/
        }
        else {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
            // Setting Dialog Title
            alertDialog.setTitle("GPS Settings");
            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
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

            //GET REQUEST FOR WEATHER
            if (isWeather) {

                final JSONObject jsonObject = new JSONObject();

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, params[0], jsonObject,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    JSONObject hourly = response.getJSONObject("hourly");
                                    summary = hourly.getString("summary");
                                    //icon = hourly.getString("icon");

                                    JSONArray data = hourly.getJSONArray("data");

                                    //JSONObject summaryObj = data.getJSONObject(0);
                                    //summary = summaryObj.getString("summary");

                                    JSONObject iconObj = data.getJSONObject(1);
                                    icon = iconObj.getString("icon");

                                    JSONObject tempObj = data.getJSONObject(2);
                                    temperature = tempObj.getString("temperature");

                                    degrees.setText(((int)Double.parseDouble(temperature)) + " °F");

                                    Log.v("Summary", summary + ", ICON " + icon);

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


    public static String getTemperature() {
        return temperature;
    }

    public static String getIcon() {
        return icon;
    }

    public static String getSummary() {
        return summary;
    }

    public static boolean getRunOnce() {
        return RUN_ONCE;
    }

    public static void setRunOnce(boolean RUN_ONCE) {
        HomeTab.RUN_ONCE = RUN_ONCE;
    }
}
