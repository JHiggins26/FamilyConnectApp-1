package familyconnect.familyconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.HashMap;
import java.util.Map;

public class Welcome extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private TextView name, email;
    private ImageView picture;
    private Button buttonLogout, buttonDashboard, buttonCreate;
    private SharedPreferences prefs;
    private GoogleApiClient mGoogleApiClient;
    private RequestQueue queue;
    private static boolean GET, POST, PUT, DELETE = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queue = Volley.newRequestQueue(this);

        buttonCreate = (Button) findViewById(R.id.buttonCreate);

        buttonDashboard = (Button) findViewById(R.id.buttonDashboard);
        buttonLogout = (Button) findViewById(R.id.sign_out_button);

        buttonCreate.setOnClickListener(this);
        buttonDashboard.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);


        //Retrieve Data from GOOGLE Sign In
        prefs = getSharedPreferences("USER_ATTRIBUTES", MODE_PRIVATE);

        name = (TextView) findViewById(R.id.profileName);
        name.setText(prefs.getString("name",""));

        email = (TextView) findViewById(R.id.profileEmail);
        email.setText(prefs.getString("email", ""));

        picture = (ImageView) findViewById(R.id.profilePic);
        Glide.with(this).load(prefs.getString("picture","")).into(picture);


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

            case R.id.buttonCreate:
                Welcome.FamilyConnectFetchTask taskPost = new Welcome.FamilyConnectFetchTask();
                String uriPost ="https://family-connect-ggc-2017.herokuapp.com/users";
                //String uriPost ="https://myprojects-mikeh87.c9users.io/users";
                taskPost.execute(uriPost);
                POST = true;

                break;

            case R.id.buttonDashboard:

                Intent dashboard = new Intent(Welcome.this, GroupedActivities.class);
                Welcome.this.startActivity(dashboard);
                break;

            case R.id.sign_out_button:
                signOut();
                revokeAccess();
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

        Intent signInPage = new Intent(Welcome.this, Login.class);
        Welcome.this.startActivity(signInPage);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class FamilyConnectFetchTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v("FamilyConnect", "URL = " + params[0]);


            //POST REQUEST
            if (POST) {

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
                        params.put("user_name", "Jawan");
                        params.put("email", "Jawan@gmail.com");
                        params.put("created", "2017-10-29T22:12:17.391Z");
                        params.put("updated", "2017-10-29T22:12:17.391Z");

                        return params;
                    }
                };
                queue.add(postRequest);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }

    }


}
