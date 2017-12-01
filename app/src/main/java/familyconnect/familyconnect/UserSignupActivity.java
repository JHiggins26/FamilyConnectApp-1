package familyconnect.familyconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;


/**
 * UserSignupActivity.java - a class that allows the users to signup to create an account.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class UserSignupActivity extends AppCompatActivity {

    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirm_passwordText;
    private EditText groupNameText, groupIDText;
    private Button signupButton;
    private Switch groupSwitch;
    private TextView loginLink;
    private RequestQueue queue;
    private boolean POST, GROUP_POST, GROUP_POST_JOIN = false;
    private boolean isCreateGroup = true;
    private static int ID;
    private static String EMAIL, TOKEN;

    /**
     * @method onCreate()
     *
     * This method creates the android activity and initializes each instance variable.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        nameText = findViewById(R.id.input_name);
        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        confirm_passwordText = findViewById(R.id.input_confirm_password);
        groupSwitch = findViewById(R.id.group_switch);
        groupNameText = findViewById(R.id.input_group_name);
        groupIDText = findViewById(R.id.input_group_ID);
        signupButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.link_login);

        queue = Volley.newRequestQueue(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to the Login activity
                finish();
            }
        });

        setGroupSwitch();
    }


    /**
     * @method setGroupSwitch()
     *
     * This method allows the user to join or create a group.
     */
    public void setGroupSwitch() {

        groupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    groupSwitch.setText("Join Group");
                    groupIDText.setVisibility(View.VISIBLE);
                    groupIDText.requestFocus();
                    groupIDText.setText("");

                    groupNameText.setVisibility(View.GONE);
                    isCreateGroup = false;
                }
                else {
                    groupSwitch.setText("Create Group");
                    groupNameText.setVisibility(View.VISIBLE);
                    groupNameText.requestFocus();
                    groupNameText.setText("");

                    groupIDText.setVisibility(View.GONE);
                    isCreateGroup = true;
                }
            }
        });
    }


    /**
     * @method signup()
     *
     * This method determines if the user's credentials are valid.
     */
    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        UserSignupActivity.FamilyConnectFetchTask taskPost = new UserSignupActivity.FamilyConnectFetchTask();
        String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users";
        taskPost.execute(uriPost);
        POST = true;
        GROUP_POST = false;
        GROUP_POST_JOIN = false;
    }


    /**
     * @method onSignupFailed()
     *
     * This method displays a failed message when the user's credentials are not valid.
     */
    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }


    /**
     * @method validate()
     *
     * This method validates the user's credentials to assure the criteria is met.
     *
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String confirm_password = confirm_passwordText.getText().toString();
        String group_name = groupNameText.getText().toString();
        String group_ID = groupIDText.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("Username must be at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() <= 5) {
            passwordText.setError("Password must contain at least 6 or more alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (confirm_password.isEmpty() || (!confirm_password.equals(password))) {
            confirm_passwordText.setError("Confirm password does not match the password");
            valid = false;
        } else {
            confirm_passwordText.setError(null);
        }

        if(isCreateGroup) {
            if (group_name.isEmpty() || group_name.length() < 3) {
                groupNameText.setError("Group name must be at least 3 characters");
                valid = false;
            } else {
                groupNameText.setError(null);
            }
        }

        if(!isCreateGroup) {
            if (group_ID.isEmpty()) {
                groupIDText.setError("Please enter a Group ID #");
                valid = false;
            } else {
                groupIDText.setError(null);
            }
        }

        return valid;
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

            //POST REQUEST
            if (POST) {

                StringRequest postRequest = new StringRequest(Request.Method.POST, params[0],
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                String jsonRequest [ ] = response.split(",");

                                ID = Integer.parseInt(jsonRequest[0].substring(6, jsonRequest[0].toString().length()));
                                EMAIL = jsonRequest[4].substring(9, jsonRequest[4].toString().length()-1);
                                TOKEN = jsonRequest[5].substring(24, jsonRequest[5].toString().length()-1);

                                Log.d("POST REQUEST", response);


                                final ProgressDialog progressDialog = new ProgressDialog(UserSignupActivity.this,
                                        R.style.AppTheme_PopupOverlay);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Creating Account...");
                                progressDialog.show();

                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {

                                                if(((Integer) ID != null) && (EMAIL != null) && (TOKEN != null) ) {
                                                    if (isCreateGroup) {
                                                        UserSignupActivity.FamilyConnectFetchTask taskPost = new UserSignupActivity.FamilyConnectFetchTask();
                                                        String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users/" + ID + "/groups";
                                                        taskPost.execute(uriPost);
                                                        GROUP_POST = true;
                                                        POST = false;
                                                        GROUP_POST_JOIN = false;
                                                    } else {
                                                        UserSignupActivity.FamilyConnectFetchTask taskPost = new UserSignupActivity.FamilyConnectFetchTask();
                                                        String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users/" + ID + "/usergroup";
                                                        taskPost.execute(uriPost);
                                                        GROUP_POST_JOIN = true;
                                                        POST = false;
                                                        GROUP_POST = false;
                                                    }
                                                }
                                                else {
                                                    onSignupFailed();
                                                }

                                                progressDialog.dismiss();
                                            }
                                        }, 3000);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getBaseContext(), "Sign Up Failed! The Email is already in use.", Toast.LENGTH_LONG).show();
                                finish();

                                Log.d("Error.Response", ""+error);
                            }
                        }
                ) {

                    //PASS PARAMETERS
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("user_name", nameText.getText().toString());
                        params.put("email", emailText.getText().toString().toLowerCase());
                        params.put("password", passwordText.getText().toString());
                        params.put("password_confirmation", confirm_passwordText.getText().toString());

                        return params;
                    }
                };
                queue.add(postRequest);
            }

            if (GROUP_POST) {

                StringRequest postRequest = new StringRequest(Request.Method.POST, params[0],
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {

                                Log.d("GROUP POST REQUEST", response);

                                Toast.makeText(getBaseContext(), "Account Created", Toast.LENGTH_LONG).show();

                                Intent signIn = new Intent(UserSignupActivity.this, UserLoginActivity.class);
                                UserSignupActivity.this.startActivity(signIn);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", "" + error);
                            }
                        }
                ) {

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("X-User-Email", emailText.getText().toString());
                        headers.put("X-User-Token", TOKEN);

                        return headers;
                    }

                    //PASS PARAMETERS
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("group_name", groupNameText.getText().toString());

                        return params;
                    }
                };
                queue.add(postRequest);
            }


            if (GROUP_POST_JOIN) {

                StringRequest postRequest = new StringRequest(Request.Method.POST, params[0],
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {

                                Log.d("GROUP POST REQUEST", response);

                                Toast.makeText(getBaseContext(), "Account Created", Toast.LENGTH_LONG).show();

                                Intent signIn = new Intent(UserSignupActivity.this, UserLoginActivity.class);
                                UserSignupActivity.this.startActivity(signIn);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", "" + error);
                            }
                        }
                ) {

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("X-User-Email", emailText.getText().toString());
                        headers.put("X-User-Token", TOKEN);

                        return headers;
                    }

                    //PASS PARAMETERS
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("user_id", ID+"");
                        params.put("group_id", groupIDText.getText().toString());

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
