package familyconnect.familyconnect;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;


public class CreateActivitiesTab extends Fragment implements View.OnClickListener {

    private static EditText name, time, weather, category, group = null;

    private String readName, readTime, readWeather, readCategory, readGroup = null;
    private boolean POST= false;
    private static boolean isCreated = false;
    private RequestQueue queue;

    private static ArrayList<Activity> activitiesList = new ArrayList<Activity>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.createactivitiestab, container, false);

        name = rootView.findViewById(R.id.activityName);

        time = rootView.findViewById(R.id.activityTime);

        weather = rootView.findViewById(R.id.activityWeather);

        category = rootView.findViewById(R.id.activityCategory);

        group = rootView.findViewById(R.id.activityGroup);

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                readName = name.getText().toString();
                readTime = time.getText().toString();
                readWeather = weather.getText().toString();
                readCategory = category.getText().toString();
                readGroup = group.getText().toString();


                if(readName.matches("")) {

                    Snackbar.make(view, "Please fill out activity fields!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Log.v("Created", "ReadName: " + readName);

                    Activity activity = new Activity(readName);
                    activitiesList.add(activity);

                    isCreated = true;

                    FamilyConnectFetchTask taskPost = new FamilyConnectFetchTask();
                    String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users/1/activities";
                    //String uriPost ="https://myprojects-mikeh87.c9users.io/users/1/activities";
                    taskPost.execute(uriPost);
                    POST = true;

                    Snackbar.make(view, "Activity Created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        return rootView;
    }


    @Override
    public void onClick(View v) {

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

                    // Read in the data
                    int user_id = 1;
                    String activityName = readName;
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


    public static EditText getName() {
        return name;
    }

    public static void setName(EditText name) {
        CreateActivitiesTab.name = name;
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

    public static ArrayList<Activity> getActivitiesList() {
        return activitiesList;
    }

}

