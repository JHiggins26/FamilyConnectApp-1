package familyconnect.familyconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import familyconnect.familyconnect.json.FamilyConnectHttpResponse;

import static android.R.attr.bitmap;
import static com.android.volley.Request.Method.PUT;


/**
 * The type User main area.
 */
public class UserMainArea extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private TextView name, email;
    private ImageView picture;
    private Button buttonLogout, buttonFetch, buttonPost, buttonPut, buttonDelete;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences prefs;
    private static boolean GET, POST, PUT, DELETE = false;
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermainarea);

        queue = Volley.newRequestQueue(this);

        //Retrieve Data from GOOGLE Sign In
        prefs = getSharedPreferences("USER_ATTRIBUTES", MODE_PRIVATE);

        buttonDelete = (Button) findViewById(R.id.delete_button);
        buttonPut = (Button) findViewById(R.id.put_button);
        buttonPost = (Button) findViewById(R.id.post_button);
        buttonFetch = (Button) findViewById(R.id.fetch_button);
        buttonLogout = (Button) findViewById(R.id.sign_out_button);
        name = (TextView) findViewById(R.id.profileName);
        name.setText(prefs.getString("name",""));

        email = (TextView) findViewById(R.id.profileEmail);
        email.setText(prefs.getString("email", ""));

        picture = (ImageView) findViewById(R.id.profilePic);
        Glide.with(this).load(prefs.getString("picture","")).into(picture);

        buttonPut.setOnClickListener(this);
        buttonPost.setOnClickListener(this);
        buttonFetch.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,  this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_out_button:
                signOut();
                revokeAccess();
                break;

            case R.id.delete_button:
                FamilyConnectFetchTask taskDelete = new FamilyConnectFetchTask();
                String uriDelete ="https://family-connect-ggc-2017.herokuapp.com/users";
                //String uriDelete ="https://myprojects-mikeh87.c9users.io/users";
                taskDelete.execute(uriDelete);
                GET = false;
                POST = false;
                PUT = false;
                DELETE = true;
                break;

            case R.id.put_button:
                FamilyConnectFetchTask taskPut = new FamilyConnectFetchTask();
                String uriPut ="https://family-connect-ggc-2017.herokuapp.com/users";
                //String uriPut ="https://myprojects-mikeh87.c9users.io/users";
                taskPut.execute(uriPut);
                GET = false;
                POST = false;
                PUT = true;
                DELETE = false;
                break;

            case R.id.post_button:
                FamilyConnectFetchTask taskPost = new FamilyConnectFetchTask();
                String uriPost ="https://family-connect-ggc-2017.herokuapp.com/users";
                //String uriPost ="https://myprojects-mikeh87.c9users.io/users";
                taskPost.execute(uriPost);
                GET = false;
                POST = true;
                PUT = false;
                DELETE = false;
                break;

            case R.id.fetch_button:
                FamilyConnectFetchTask taskGet = new FamilyConnectFetchTask();
                String uriGet ="https://family-connect-ggc-2017.herokuapp.com/users";
                //String uriGet ="https://myprojects-mikeh87.c9users.io/users";
                taskGet.execute(uriGet);
                GET = true;
                POST = false;
                PUT = false;
                DELETE = false;
                break;
        }
    }

    private void signOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }


    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        mGoogleApiClient.clearDefaultAccountAndReconnect();
                        mGoogleApiClient.disconnect();
                        updateUI(false);
                    }
                });
    }

    private void updateUI(boolean isLogin) {

        Intent signInPage = new Intent(UserMainArea.this, Login.class);
        UserMainArea.this.startActivity(signInPage);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private class FamilyConnectFetchTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v("FamilyConnect", "URL = " + params[0]);

            //GET REQUEST
            if (GET) {

                final JSONObject jsonObject = new JSONObject();

                //Alternative GET Method
                /*
                try {
                    String line;
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder json = new StringBuilder();
                    StringBuilder jsonPrinter = new StringBuilder();

                    String lineArray[] = reader.readLine().split("\\},");

                    for (int i = 0; i < lineArray.length; i++) {
                        //json.append(reader.readLine());
                        jsonPrinter.append(lineArray[i] + System.getProperty("line.separator"));
                    }

                    while ((line = reader.readLine()) != null) {

                        json.append(line);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                */

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, params[0] + "/3", jsonObject,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // response
                                Log.d("GET", response.toString());

                                //Format JSON into Object
                                Gson gson = new Gson();
                                JsonReader jsonReader = new JsonReader(new StringReader(response.toString()));
                                jsonReader.setLenient(true);

                                FamilyConnectHttpResponse httpResponse =
                                        gson.fromJson(jsonReader, FamilyConnectHttpResponse.class);

                                String user = httpResponse.getUserName();
                                Log.v("User", user);

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

            //POST REQUEST
            else if (POST) {

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

                final JSONObject jsonObject = new JSONObject();

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
                        params.put("user_name", "Alif");
                        params.put("email", "Alif@yahoo.com");
                        params.put("created", "2017-10-29T22:12:17.391Z");
                        params.put("updated", "2017-10-29T22:12:17.391Z");

                        return params;
                    }
                };
                queue.add(postRequest);
            }

            //PUT REQUEST
            else if (PUT) {

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

        }

    }
}
