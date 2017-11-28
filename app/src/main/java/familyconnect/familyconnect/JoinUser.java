package familyconnect.familyconnect;


import android.app.*;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JoinUser extends android.app.Activity implements View.OnClickListener{

    private EditText input_group_ID;
    private ImageButton join_add_button, cancel_button;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joinuserpopup);

        input_group_ID = findViewById(R.id.input_group_ID);
        join_add_button = findViewById(R.id.join_add_button);
        cancel_button = findViewById(R.id.cancel_button);


        queue = Volley.newRequestQueue(this);

        join_add_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.join_add_button:
                handleRequest();
                break;

            case R.id.cancel_button:
                finish();
                break;
        }
    }


    public void handleRequest() {

        if (!input_group_ID.getText().toString().equals("")) {
            JoinUser.FamilyConnectFetchTask task = new JoinUser.FamilyConnectFetchTask();
            String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/usergroup";
            task.execute(uriPost);

            finish();
        }
        else {
            input_group_ID.setError("Please enter a Group ID #");
        }
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

            Log.v("FamilyConnect", "URL = " + params[0]);

            //POST REQUEST
            StringRequest postRequest = new StringRequest(Request.Method.POST, params[0],
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {

                            Toast.makeText(JoinUser.this, "Joined Successfully",
                                    Toast.LENGTH_LONG).show();
                            Log.d("POST REQUEST", response);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(JoinUser.this, "Invalid Group ID#: Join Failed!",
                                    Toast.LENGTH_LONG).show();

                            Log.d("Error.Response", "" + error);
                        }
                    }
            ) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("X-User-Email", UserLoginActivity.getEmail());
                    headers.put("X-User-Token", UserLoginActivity.getToken());

                    return headers;
                }

                //PASS PARAMETERS
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("user_id", UserLoginActivity.getID()+"");
                    params.put("group_id", input_group_ID.getText().toString());

                    return params;
                }
            };
            queue.add(postRequest);

            return null;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }
    }
}
