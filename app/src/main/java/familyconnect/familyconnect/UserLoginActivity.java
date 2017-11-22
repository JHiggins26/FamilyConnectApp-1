package familyconnect.familyconnect;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import familyconnect.familyconnect.json.FamilyConnectHttpResponse;


public class UserLoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;

    private static EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private TextView signupLink;
    private RequestQueue queue;
    private boolean GET, POST;
    private static int ID;
    private static String TOKEN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        queue = Volley.newRequestQueue(this);

        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), UserSignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(UserLoginActivity.this,
                R.style.AppTheme_PopupOverlay);//AppTheme_Dark_Dialog
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        UserLoginActivity.FamilyConnectFetchTask task = new UserLoginActivity.FamilyConnectFetchTask();
                        //String uriGet ="https://family-connect-ggc-2017.herokuapp.com/users";
                        String uriPost ="https://family-connect-ggc-2017.herokuapp.com/sessions";
                        task.execute(uriPost);
                        //GET = true;
                        POST = true;

                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Do Nothing
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() <= 4) {
            passwordText.setError("Password must be greater than 4 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }


    private class FamilyConnectFetchTask extends AsyncTask<String, Void, Bitmap> {

        //Converts JSON string into a Activity object
        private FamilyConnectHttpResponse getTask(String json){
            Gson gson = new GsonBuilder().create();

            if(json.charAt(0) == ','){
                json = json.substring(1);
            }
            FamilyConnectHttpResponse user = gson.fromJson(json, FamilyConnectHttpResponse.class);
            return user;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v("FamilyConnect", "URL = " + params[0]);

            //GET REQUEST
            if (GET) {

                try {
                    String line;
                    URL url = new URL(params[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                  /*  connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.addRequestProperty("X-Email", "EMAIL");
                    connection.addRequestProperty("X-User-Token", "TOKEN");
                    connection.setDoOutput(true);
                   */
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder json = new StringBuilder();


                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }

                    //Trims braces at the beginning and end of the string
                    String jsonTrim = json.toString().substring(1, json.toString().length() - 1);

                    Log.v("String", "" + jsonTrim);

                    Scanner scanJ = new Scanner(jsonTrim);
                    scanJ.useDelimiter("[}]");

                    ArrayList<FamilyConnectHttpResponse> usr = new ArrayList<FamilyConnectHttpResponse>();

                    while (scanJ.hasNext()) {
                        usr.add(getTask(scanJ.next() + "}"));
                    }

                    for (FamilyConnectHttpResponse user : usr) {

                        if(user.getEmail().equalsIgnoreCase(emailText.getText().toString())) {

                            POST = true;

                            break;
                        }
                        else {
                            //SUCCESS = false;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //POST REQUEST
            if (POST) {

                //                                                                  (KEY,       VALUE)
                //GET X-Token - Do a POST on (/sessions) it then returns a user token (X-Email, Jawan@gmail.com) (X-User-Token, 8U8774H7hGG)
                //HEADER GET POST PUT DELETE Include (X-User-Email, X-User-Token, Content-Type = application/json)

                //Before POST make sure I get the User email and password ****Can save USERNAME and PASSWORD on phones persistent memory to auto login***
                //@OnStart for creating a new session (do POST to (/SESSION))
                //When closing the app to a DELETE on (/sessions) in @OnStop and @OnDestroy override

                StringRequest postRequest = new StringRequest(Request.Method.POST, params[0],
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {

                                String jsonRequest [ ] = response.split(",");

                                ID = Integer.parseInt(jsonRequest[0].substring(6, jsonRequest[0].toString().length()));
                                TOKEN = jsonRequest[2].substring(24, jsonRequest[2].toString().length()-2);

                                Log.v("ID", jsonRequest[0].substring(6, jsonRequest[0].toString().length()));
                                Log.v("TOKEN", jsonRequest[2].substring(24, jsonRequest[2].toString().length()-2));

                                Intent homePage = new Intent(UserLoginActivity.this, GroupedActivities.class);
                                UserLoginActivity.this.startActivity(homePage);

                                onLoginSuccess();

                                Log.d("POST REQUEST", response);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                onLoginFailed();

                                Log.d("Error.Response", "" + error);
                            }
                        }
                ) {

                    //PASS PARAMETERS
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("email", emailText.getText().toString().toLowerCase());
                        params.put("password", passwordText.getText().toString());

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

    public static String getToken() {
        return TOKEN;
    }

    public static String getEmail() {
        return emailText.getText().toString();
    }

    public static int getID() {
        return ID;
    }
}
