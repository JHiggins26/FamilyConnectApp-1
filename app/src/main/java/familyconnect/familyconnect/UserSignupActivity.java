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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;


public class UserSignupActivity extends AppCompatActivity {

    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirm_passwordText;
    private Button signupButton;
    private TextView loginLink;
    private RequestQueue queue;
    private static boolean POST = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        nameText = (EditText) findViewById(R.id.input_name);
        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        confirm_passwordText = (EditText) findViewById(R.id.input_confirm_password);
        signupButton = (Button) findViewById(R.id.btn_signup);
        loginLink = (TextView) findViewById(R.id.link_login);

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
    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(UserSignupActivity.this,
                R.style.AppTheme_PopupOverlay);//AppTheme_Dark_Dialog
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        UserSignupActivity.FamilyConnectFetchTask taskPost = new UserSignupActivity.FamilyConnectFetchTask();
                        String uriPost ="https://family-connect-ggc-2017.herokuapp.com/users";
                        taskPost.execute(uriPost);
                        POST = true;

                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String confirm_password = confirm_passwordText.getText().toString();


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

        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError("Password must be greater than 4 alphanumeric characters");
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

        return valid;
    }


    private class FamilyConnectFetchTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v("FamilyConnect", "URL = " + params[0]);

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

                    //PASS IN HEADER
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=UTF-8");
                        //headers.put("X-Email", "EMAIL");
                        //headers.put("X-User-Token", "TOKEN");

                        return headers;
                    }

                    //PASS PARAMETERS
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("user_name", nameText.getText().toString());
                        params.put("email", emailText.getText().toString());
                        params.put("password", passwordText.getText().toString());
                        params.put("password_confirmation", confirm_passwordText.getText().toString());

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

            Intent signIn = new Intent(UserSignupActivity.this, UserLoginActivity.class);
            UserSignupActivity.this.startActivity(signIn);

        }

    }
}
