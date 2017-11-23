package familyconnect.familyconnect;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;


public class GroupsTab extends Fragment {

    private TextView groupsTitle, groupsID;
    private ScrollView membersScrollview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.groupstab, container, false);

        groupsTitle = rootView.findViewById(R.id.groupsTitle);
        groupsTitle.setText(HomeTab.getGroupName() + " Group Members");

        groupsID = rootView.findViewById(R.id.groups_id_title);
        groupsID.setText("Group ID#: " + UserLoginActivity.getGroupID());

        membersScrollview = rootView.findViewById(R.id.group_members_scrollview);

        return rootView;
    }


    public void populateGroupUsers() {

        /*
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.displayactivitylist, R.id.listText, new ArrayList<String>());

        membersScrollview.setAdapter(arrayAdapter);

        membersScrollview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String  itemValue = (String) scrollView.getItemAtPosition(position);

            }
        });*/
    }

}

