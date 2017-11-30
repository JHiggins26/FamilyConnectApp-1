package familyconnect.familyconnect;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


/**
 * DeleteUser.java - a class that allows the user to delete themselves from a group.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class DeleteUser extends android.app.Activity implements View.OnClickListener{

    private TextView delete_title, unjoin_delete_text;
    private Switch unjoin_delete_switch;
    private ImageButton unjoin_delete_button, cancel_button;
    private boolean isUnjoin = true;
    private RequestQueue queue;

    /**
     * @method onPrepareOptionsMenu()
     *
     * This method allows a menu to overflow the main activity.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    /**
     * @method onCreate()
     *
     * This method creates the android activity and initializes each instance variable.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deletepopup);

        delete_title = findViewById(R.id.delete_title);
        unjoin_delete_text = findViewById(R.id.unjoin_delete_text);
        unjoin_delete_switch = findViewById(R.id.unjoin_delete_switch);
        unjoin_delete_button = findViewById(R.id.unjoin_delete_button);
        cancel_button = findViewById(R.id.cancel_button);

        queue = Volley.newRequestQueue(this);

        unjoin_delete_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));

        delete_title.setText("Unjoin " + GroupsTab.getGroupsDropdownValue() + " Group");

        set_Unjoin_Delete();
    }

    /**
     * @method set_Unjoin_Delete()
     *
     * This method checks if the user wants to unjoin or delete a group.
     */
    public void set_Unjoin_Delete() {

        unjoin_delete_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    delete_title.setText("Delete " + GroupsTab.getGroupsDropdownValue() + " Group");
                    unjoin_delete_switch.setText("Delete Group");
                    unjoin_delete_text.setText("Delete Group");
                    isUnjoin = false;
                }
                else {
                    delete_title.setText("Unjoin " + GroupsTab.getGroupsDropdownValue() + " Group");
                    unjoin_delete_switch.setText("Unjoin Group");
                    unjoin_delete_text.setText("Unjoin Group");
                    isUnjoin = true;
                }
            }
        });
    }


    /**
     * @method onClick()
     *
     * This method provides a specific functionality for each button that is clicked
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.unjoin_delete_button:
                handleRequest();
                break;

            case R.id.cancel_button:
                finish();
                break;
        }
    }


    /**
     * @method handleRequest()
     *
     * This method handles the request when the unjoin button is clicked.
     */
    public void handleRequest() {

        if(isUnjoin) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(DeleteUser.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(DeleteUser.this);
            }
            builder.setTitle("Unjoin Group")
                    .setMessage("Are you sure you want to unjoin the " + GroupsTab.getGroupsDropdownValue() + " group?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            DeleteUser.FamilyConnectFetchTask taskGet = new DeleteUser.FamilyConnectFetchTask();
                            String uriDelete ="https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/" + GroupsTab.getGroupID() + "/usergroup";
                            taskGet.execute(uriDelete);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(DeleteUser.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(DeleteUser.this);
            }
            builder.setTitle("Delete Group")
                    .setMessage("Are you sure you want to delete the " + GroupsTab.getGroupsDropdownValue() + " group?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            DeleteUser.FamilyConnectFetchTask task = new DeleteUser.FamilyConnectFetchTask();
                            String uriDelete = "https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/" + GroupsTab.getGroupID();
                            task.execute(uriDelete);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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

            Log.v("FamilyConnect", "URI = " + params[0]);

            //DELETE REQUEST FOR USER
            if (isUnjoin) {

                final JSONObject jsonObject = new JSONObject();

                JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE,
                        params[0], jsonObject,

                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                // response
                                Log.d("DELETE", response.toString());

                                Toast.makeText(DeleteUser.this, "You have unjoined the group!",
                                        Toast.LENGTH_SHORT).show();

                                GroupsTab.getGroupsDropdown().setSelection(0);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(DeleteUser.this, "You have unjoined the group!",
                                        Toast.LENGTH_SHORT).show();

                                GroupsTab.getGroupsDropdown().setSelection(0);
                                finish();
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }
                )
                {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("X-User-Email", UserLoginActivity.getEmail());
                        headers.put("X-User-Token", UserLoginActivity.getToken());

                        return headers;
                    }
                };
                queue.add(deleteRequest);
            }

            //DELETE REQUEST FOR GROUP
            if (isUnjoin == false) {

                final JSONObject jsonObject = new JSONObject();

                JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.DELETE,
                        params[0], jsonObject,

                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                response = null;
                                //Log.d("DELETE", response.toString());

                                Toast.makeText(DeleteUser.this, "Group deleted!",
                                        Toast.LENGTH_SHORT).show();

                                GroupsTab.getGroupsDropdown().setSelection(0);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(DeleteUser.this, "Group deleted!",
                                        Toast.LENGTH_SHORT).show();

                                GroupsTab.getGroupsDropdown().setSelection(0);
                                finish();

                                Log.d("Error.Response", error.toString());
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json");
                        headers.put("X-User-Email", UserLoginActivity.getEmail());
                        headers.put("X-User-Token", UserLoginActivity.getToken());

                        return headers;
                    }
                };
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

