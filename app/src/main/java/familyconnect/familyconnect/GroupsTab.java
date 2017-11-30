package familyconnect.familyconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * GroupsTab.java - a class that displays all the group members associated to a group.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class GroupsTab extends Fragment implements View.OnClickListener, View.OnTouchListener{

    private TextView groupsTitle, userIdTitle, groupsDropdownText;
    private static TextView groupsIdTitle;
    private ListView membersListview;
    private ImageButton deleteGroupBtn, addUserBtn;
    private static Spinner groupsDropdown;
    private RequestQueue queue;
    private boolean isGroupDropdown, isPopulateUsers = false;
    private static String groupsDropdownValue;
    private ArrayList<String> groupList = new ArrayList<String>();
    private Map<String, Long> groupPairList = new LinkedHashMap<String, Long>();
    private ArrayList<String> userList = new ArrayList<String>();;
    private long groupID;
    private static long groupNum = 0;
    private String groupName, userName;
    private int countLoop = 0;


    /**
     * @method onCreate()
     *
     * This method creates the android activity and initializes each instance variable.
     *
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.groupstab, container, false);

        groupsTitle = rootView.findViewById(R.id.groupsTitle);
        groupsIdTitle = rootView.findViewById(R.id.groups_id_title);
        userIdTitle = rootView.findViewById(R.id.user_id_title);
        membersListview = rootView.findViewById(R.id.group_members_listview);
        addUserBtn = rootView.findViewById(R.id.add_user);
        deleteGroupBtn = rootView.findViewById(R.id.delete_group);
        groupsDropdown = rootView.findViewById(R.id.group_dropdown);
        groupsDropdownText = rootView.findViewById(R.id.group_dropdown_text);

        queue = Volley.newRequestQueue(getActivity());

        addUserBtn.setOnClickListener(this);
        deleteGroupBtn.setOnClickListener(this);
        groupsDropdown.setOnTouchListener(this);

        if(GroupsTab.getGroupID() == 0 ) {
            groupsTitle.setText("Activity Group Members");
            userIdTitle.setText("User ID#: " + UserLoginActivity.getID());
            groupsIdTitle.setText("Group ID#: N/A");
        }
        else {
            userIdTitle.setText("User ID#: " + UserLoginActivity.getID());
            groupsTitle.setText(groupsDropdownValue + " Group Members");
            groupsIdTitle.setText("Group ID#: " + groupNum);
        }

        return rootView;
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

            case R.id.delete_group:

                if(GroupsTab.getGroupID() == 0) {
                    Toast.makeText(getActivity(), "Please consider joining or creating a group first!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(new Intent(getActivity(), DeleteUser.class));
                }
                break;

            case R.id.add_user:

                startActivity(new Intent(getActivity(), JoinUser.class));
                break;
        }
    }


    /**
     * @method onTouch()
     *
     * This method provides a specific functionality for when the Spinner (Dropdown) is touched.
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(countLoop == 0) {
            setGroups();
            countLoop++;
        }
        return false;
    }


    /**
     * @method setGroups
     *
     * This method get a request for the group the user would like to display.
     */
    public void setGroups() {

        isGroupDropdown = true;
        isPopulateUsers = false;

        GroupsTab.FamilyConnectFetchTask taskGet = new GroupsTab.FamilyConnectFetchTask();
        String uriGetDropdown ="https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/";

        taskGet.execute(uriGetDropdown);
    }


    /**
     * @method getUsers()
     *
     * This method updates the Spinner (Dropdown) with the correct group names.
     *
     * @param j
     */
    private void getUsers(JSONArray j) {

        groupList.add("");
        groupList.add("Create a group");

        for (int i = 0; i < j.length(); i++) {
            try {

                JSONObject json = j.getJSONObject(i);

                groupList.add(json.getString("group_name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, groupList);
        groupsDropdown.setAdapter(adapterList);

        groupsDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                isPopulateUsers = true;
                isGroupDropdown = false;
                countLoop = 0;
                groupNum = 0;

                if(position > 1) {
                    int index = position - 2;
                    int count = 0;

                    for (Map.Entry<String, Long> e : groupPairList.entrySet()) {

                        if (index == count) {
                            groupNum = e.getValue();
                        }
                        count++;
                    }
                }

                if(position > 1) {
                    GroupsTab.FamilyConnectFetchTask taskGet = new GroupsTab.FamilyConnectFetchTask();
                    String uriGetUsers ="https://family-connect-ggc-2017.herokuapp.com/users/" + UserLoginActivity.getID() + "/groups/" + groupNum + "/usergroup";
                    taskGet.execute(uriGetUsers);

                    groupsDropdownText.setText("");
                    groupsDropdownText.setBackgroundColor(Color.WHITE);

                    groupsDropdownValue = groupsDropdown.getSelectedItem().toString();

                    groupsTitle.setText(groupsDropdownValue + " Group Members");
                    groupsIdTitle.setText("Group ID#: " + groupNum);
                    membersListview.setVisibility(View.VISIBLE);

                    DisplayActivitiesTab.getNonCompletedActivityList().clear();

                    Toast.makeText(getActivity(), "Group Changed!",
                            Toast.LENGTH_SHORT).show();
                }
                else if (position == 1) {
                    groupsDropdownText.setText("");
                    groupsDropdownText.setBackgroundColor(Color.WHITE);
                    groupsTitle.setText("Activity Group Members");
                    groupsIdTitle.setText("Group ID#: N/A");
                    membersListview.setVisibility(View.INVISIBLE);

                    Toast.makeText(getActivity(), "No Group Selected!",
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getActivity(), CreateGroup.class));
                }
                else if(position == 0) {
                    groupsDropdownText.setText("Select a Group");
                    groupsTitle.setText("Activity Group Members");
                    groupsIdTitle.setText("Group ID#: N/A");
                    groupsDropdownText.setBackgroundColor(Color.parseColor("#0c59cf"));
                    membersListview.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * @method populateGroupUsers()
     *
     * This method populates all members associated to the group and displays them in a List View.
     */
    public void populateGroupUsers() {

        isPopulateUsers = false;

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.usergrouplist, R.id.listText, userList);

        membersListview.setAdapter(arrayAdapter);
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

            //GET REQUEST FOR GROUP USERS
            if (isGroupDropdown) {

                JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, params[0], null,
                        new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {

                                try {
                                    groupPairList.clear();
                                    groupList.clear();

                                    for(int i = 0; i < response.length(); i++) {

                                        JSONObject groupObj = response.getJSONObject(i);

                                        groupName = groupObj.getString("group_name");

                                        groupID = groupObj.getLong("id");

                                        groupPairList.put(groupName, groupID);
                                    }

                                    getUsers(response);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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
                queue.add(getRequest);
            }

            //GET REQUEST FOR GROUP USERS
            if (isPopulateUsers) {

                JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, params[0], null,
                        new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {

                                try {
                                    userList.clear();

                                    for(int i = 0; i < response.length(); i++) {

                                        JSONObject userNameObj = response.getJSONObject(i);

                                        userName = userNameObj.getString("user_name");

                                        userList.add(userName);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                populateGroupUsers();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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
                queue.add(getRequest);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }
    }

    public static String getGroupsIdTitle() {
        return groupsIdTitle.getText().toString().substring(11, groupsIdTitle.getText().toString().length());
    }

    public static long getGroupID() {
        return groupNum;
    }

    public static void setGroupID(long id) {
         GroupsTab.groupNum = id;
    }

    public static String getGroupsDropdownValue() {
        return groupsDropdownValue;
    }

    public static Spinner getGroupsDropdown() {
        return groupsDropdown;
    }
}