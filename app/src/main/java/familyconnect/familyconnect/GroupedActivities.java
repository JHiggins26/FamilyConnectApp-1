package familyconnect.familyconnect;


import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


/**
 * GroupedActivities.java - a class that groups all the activity tabs and provides functionality that switches each tabs.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class GroupedActivities extends AppCompatActivity {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;

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
        setContentView(R.layout.activity_grouped_activities);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.home_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.activity_list_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.group_icon);
    }


    /**
     * @method onCreateOptionsMenu()
     *
     * This method creates an options menu.
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grouped_activities, menu);
        return true;
    }

    /**
     * @method onOptionsItemSelected()
     *
     * This method gets the item selected in the options menu.
     *
     * @param item
     * @return onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * @class SectionsPagerAdapter
     *
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * @method getItem()
         *
         * This method returns the Fragment that is displayed.
         *
         * @param position
         * @return Fragment
         */
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    HomeTab tab1 = new HomeTab();

                    return tab1;
                case 1:
                    DisplayActivitiesTab tab2 = new DisplayActivitiesTab();

                    return tab2;

                case 2:
                    GroupsTab tab3 = new GroupsTab();

                    return tab3;

                default:
                    return null;
            }
        }

        /**
         * @method getCount()
         *
         * This method gets the count of how many tabs there are.
         *
         * @return int
         */
        @Override
        public int getCount() {
            // Switched view to three tabs
            return 3;
        }

        /**
         * @method getPageTitle()
         *
         * This method returns the title of each tab displayed.
         *
         * @param position
         * @return CharSequence
         */
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
            }
            return null;
        }
    }

    /**
     * @method onBackPressed()
     *
     * This method prevents the device from going back.
     */
    @Override
    public void onBackPressed() {
        //Do Nothing
    }

    public static ViewPager getViewPager() {
        return mViewPager;
    }
}
