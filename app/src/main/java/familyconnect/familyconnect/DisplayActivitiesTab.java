package familyconnect.familyconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class DisplayActivitiesTab extends Fragment {

    private static ListView scrollView;
    //private TextView loadingText;
    private RequestQueue queue;
    private boolean GET = false;
    private List<String> jsonArray;
    private List<Activity> activityList;
    private static String activityDetailsTitle, activityWeatherTemp, activityWeatherSummary, activityWeatherIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.testconstraint, container, false);

        scrollView = rootView.findViewById(R.id.activityScroll);
        //loadingText = rootView.findViewById(R.id.loading);

        jsonArray = new ArrayList<String>();
        activityList = new ArrayList<Activity>();

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            //loadingText.setText("Loading Activities...");

            //Clear JSON and Activity array to Sync the list
            jsonArray.clear();
            activityList.clear();

            GET = true;

            DisplayActivitiesTab.FamilyConnectFetchTask taskGet = new DisplayActivitiesTab.FamilyConnectFetchTask();
            String uriGet ="https://family-connect-ggc-2017.herokuapp.com/users/1/activities";
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

                //Alternative GET Method

                try {
                    String line;
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
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

                    ArrayList<FamilyConnectActivitiesHttpResponse> rets = new ArrayList<FamilyConnectActivitiesHttpResponse>();

                    while(scanJ.hasNext()){
                        rets.add(getTask(scanJ.next() + "}"));
                    }

                    for(FamilyConnectActivitiesHttpResponse activities : rets) {

                        //JsonArray is just for the Individual Activity Title on the Activity Details Page
                        jsonArray.add(0, activities.getActivitieName());

                        Activity activity = new Activity(activities.getActivitieName(), "", "", "",
                                "", "", "", "");
                        activityList.add(0, activity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //Alternate GET Method (THE BEST WAY, HOWEVER BRACES THROW JSON PARSE ERROR)
                /*final JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, params[0], jsonObject,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // response
                                try {

                                    String name = response.toString().substring(1,response.toString().length()-1);
                                    String name2 = response.getString("activitie_name");

                                    list.setText(name2);


                                } catch (JSONException e) {

                                }



                                Log.d("GET", response.toString().substring(1,response.toString().length()-1));

                                //Format JSON into Object
                              /*  Gson gson = new Gson();
                                JsonReader jsonReader = new JsonReader(new StringReader(response.toString()));
                                jsonReader.setLenient(true);

                                FamilyConnectActivitiesHttpResponse httpResponse =
                                        gson.fromJson(jsonReader, FamilyConnectActivitiesHttpResponse.class);

                                //String activityName = httpResponse.getActivitieName();
                                //list.setText(activityName);
                                //Log.v("Activity Name", activityName);

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

                queue.add(getRequest);*/
            }

                return null;
            }


            @Override
            protected void onPostExecute (Bitmap bitmap){
                super.onPostExecute(bitmap);

                //Deletes the Loading Text when Activities appear
                //loadingText.setText("");

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                            R.layout.displayactivitylist, R.id.listText, jsonArray);

                scrollView.setAdapter(arrayAdapter);

                scrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int itemPosition = position;
                        String  itemValue = (String) scrollView.getItemAtPosition(position);

                        activityDetailsTitle = activityList.get(itemPosition).getName();
                        activityWeatherTemp = activityList.get(itemPosition).getWeatherDegrees();
                        activityWeatherSummary = activityList.get(itemPosition).getWeatherSummary();
                        activityWeatherIcon = activityList.get(itemPosition).getWeatherIcon();

                        //Toast.makeText(getActivity().getApplicationContext(),jsonArray.get(itemPosition), Toast.LENGTH_SHORT).show();


                        Intent showActivityPage = new Intent(getActivity(), ActivityDetails.class);
                        DisplayActivitiesTab.this.startActivity(showActivityPage);

                        //Toast.makeText(getActivity().getApplicationContext(),"Position :"+itemPosition+"  Activity : " +itemValue , Toast.LENGTH_LONG).show();
                    }
                });

            }
    }

    public static String getActivityDetailsTitle() {
        return activityDetailsTitle;
    }
    public static String getActivityWeatherTemp() {
        return activityWeatherTemp;
    }
    public static String getActivityWeatherSummary() {
        return activityWeatherSummary;
    }
    public static String getActivityWeatherIcon() {
        return activityWeatherIcon;
    }
}
