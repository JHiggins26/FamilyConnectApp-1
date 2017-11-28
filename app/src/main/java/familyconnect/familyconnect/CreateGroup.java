package familyconnect.familyconnect;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends android.app.Activity implements View.OnClickListener{

    private EditText input_group_name;
    private ImageButton create_button, cancel_button;
    private boolean isJoin = true;
    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creategrouppopup);

        input_group_name = findViewById(R.id.input_group_name);
        create_button = findViewById(R.id.create_button);
        cancel_button = findViewById(R.id.cancel_button);

        queue = Volley.newRequestQueue(this);

        create_button.setOnClickListener(this);
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

            case R.id.create_button:
                handleRequest();
                break;

            case R.id.cancel_button:
                finish();
                break;
        }
    }


    public void handleRequest() {

        if (!input_group_name.getText().toString().equals("")) {
            CreateGroup.FamilyConnectFetchTask task = new CreateGroup.FamilyConnectFetchTask();
            String uriPost = "https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups";
            task.execute(uriPost);

            finish();
        }
        else {
            input_group_name.setError("Please enter a Group name");
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

                            Toast.makeText(CreateGroup.this, "Group Created!",
                                    Toast.LENGTH_LONG).show();
                            Log.d("POST REQUEST", response);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(CreateGroup.this, "Failed to create group!",
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
                    params.put("group_name", input_group_name.getText().toString());

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
