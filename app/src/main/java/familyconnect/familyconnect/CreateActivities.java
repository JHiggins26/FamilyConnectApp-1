package familyconnect.familyconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import familyconnect.familyconnect.json.FamilyConnectHttpResponse;

import static android.R.id.list;

public class CreateActivities extends AppCompatActivity implements View.OnClickListener {

    private EditText name, time, weather, category, group;

    private ArrayList<Activity> activityList;

    private ImageView picture;
    private Button buttonLogout, buttonFetch, buttonPost, buttonPut, buttonDelete;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences prefs;
    private static boolean GET, POST, PUT, DELETE = false;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_activities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queue = Volley.newRequestQueue(this);

        name = (EditText) findViewById(R.id.activityName);
        time = (EditText) findViewById(R.id.activityTime);
        weather = (EditText) findViewById(R.id.activityWeather);
        category = (EditText) findViewById(R.id.activityCategory);
        group = (EditText) findViewById(R.id.activityGroup);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FamilyConnectFetchTask taskPost = new FamilyConnectFetchTask();
                String uriPost ="https://family-connect-ggc-2017.herokuapp.com/users/1/activities/";
                //String uriPost ="https://myprojects-mikeh87.c9users.io/users";
                taskPost.execute(uriPost);
                POST = true;

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    public void createActivity() {

        Log.v("ACTIVITY", "New Activity: " + name.getText().toString());
//        activityList = new ArrayList<Activity>();
//
//        Activity activity = new Activity(name.getText().toString());
//
//        activityList.add(activity);

//        name.setText("");
//        time.setText("");
//        weather.setText("");
//        category.setText("");
//        group.setText("");



//        for(int i = 0; i < activityList.size(); i++){
//            Log.v("Activities", activityList.get(i).toString());
//        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_out_button:

                break;


        }
    }

    private class FamilyConnectFetchTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v("FamilyConnect", "URL = " + params[0]);

            //POST REQUEST
            if (POST) {

                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestMethod("POST");

                    // Create the data
                    Integer id = 1;
                    String name = "Baseball";
                    String created = "2017-10-29T22:12:17.391Z";
                    String updated = "2017-10-29T22:12:17.391Z";

                    String urlParameters = "activitie_name=" + name + "&user_id=" + id +
                            "&created_at=" + created + "&updated_at=" + updated;


                    /* create_table "activities"
                        string "activitie_name"
                        integer "user_id"
                        datetime "created_at", null: false
                        datetime "updated_at", null: false
                        index ["user_id"], name: "index_activities_on_user_id"*/

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

                //Alternative POST Method
                /*try {
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");

                    // Create the data
                    String id = "";
                    String userName = "Jawan";
                    String email = "Jawan@gmail.com";
                    String created = "2017-10-29T22:12:17.391Z";
                    String updated = "2017-10-29T22:12:17.391Z";

                    String urlParameters = "id=" + id + "&user_name=" + userName + "&email=" + email +
                            "&created_at=" + created + "&updated_at=" + updated;

                    connection.setDoOutput(true);

                    DataOutputStream dataStream = new DataOutputStream(connection.getOutputStream());

                    dataStream.write(urlParameters.getBytes());
                    dataStream.flush();
                    dataStream.close();

                    int responseCode = connection.getResponseCode();
                    String output = "Request URL " + url;
                    output += System.getProperty("line.separator") + "Request Parameters " + urlParameters;
                    output += System.getProperty("line.separator") + "Response Code " + responseCode;

                    Log.v("FamilyConnect", "POST = " + output);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                /*final JSONObject jsonObject = new JSONObject();

                StringRequest postRequest = new StringRequest(Request.Method.POST, params[0],
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("POST", response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", ""+error);
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
//                        params.put("user_name", "Bob");
//                        params.put("email", "Bob@yahoo.com");
//                        params.put("created", "2017-10-29T22:12:17.391Z");
//                        params.put("updated", "2017-10-29T22:12:17.391Z");

                        params.put("activitie_name", "Baseball");
                        params.put("user_id", new Integer().toString());
                        params.put("created_at", "2017-10-29T22:12:17.391Z");
                        params.put("updated_at", "2017-10-29T22:12:17.391Z");

                       /* create_table "activities"
                            string "activitie_name"
                        integer "user_id"
                        datetime "created_at", null: false
                        datetime "updated_at", null: false
                        index ["user_id"], name: "index_activities_on_user_id"

                        return params;
                    }
                };
                queue.add(postRequest);*/
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }

    }



}
