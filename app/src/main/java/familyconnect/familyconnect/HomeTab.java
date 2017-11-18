package familyconnect.familyconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Timer;


public class HomeTab extends Fragment implements View.OnClickListener {

    private TextView username;
    private ImageButton settings;
    private PopupWindow settingsWindow;
    private LayoutInflater layoutInflater;

    //All are part of ONE ACTIVITY BUTTON (BG, IMG, IMG, TEXT)
    private ImageView viewActivities1, viewActivities2, viewActivities3;
    private TextView viewActivities4;

    //All are part of ONE CALENDAR BUTTON (BG, IMG, IMG, TEXT)
    private ImageView viewCalendar1, viewCalendar2, viewCalendar3;
    private TextView viewCalendar4;

    private Animation animRotate, animScale;
    private AnimationSet setAnim;
    private ImageButton dailyBtn, futureBtn;

    private MediaPlayer dailyActivitySound;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hometab, container, false);

        settings = rootView.findViewById(R.id.settings_icon);
        settings.setOnClickListener(this);

        username = rootView.findViewById(R.id.usernameTitle);
        username.setText("Welcome, " + UserLoginActivity.getUsername() + "!");

        //All are part of ONE ACTIVITY BUTTON (BG, IMG, IMG, TEXT)
        viewActivities1 = rootView.findViewById(R.id.listBGimage);
        viewActivities1.setOnClickListener(this);
        viewActivities2 = rootView.findViewById(R.id.arrow);
        viewActivities2.setOnClickListener(this);
        viewActivities3 = rootView.findViewById(R.id.activity_icon);
        viewActivities3.setOnClickListener(this);
        viewActivities4 = rootView.findViewById(R.id.listText);
        viewActivities4.setOnClickListener(this);

        //All are part of ONE CALENDAR BUTTON (BG, IMG, IMG, TEXT)
        viewCalendar1 = rootView.findViewById(R.id.calendarBGimage);
        viewCalendar1.setOnClickListener(this);
        viewCalendar2 = rootView.findViewById(R.id.calendar_arrow);
        viewCalendar2.setOnClickListener(this);
        viewCalendar3 = rootView.findViewById(R.id.calendar_icon);
        viewCalendar3.setOnClickListener(this);
        viewCalendar4 = rootView.findViewById(R.id.calendar_text);
        viewCalendar4.setOnClickListener(this);


        dailyBtn = rootView.findViewById(R.id.suggest_daily_activity_icon);
        dailyBtn.setOnClickListener(this);

//        futureBtn = rootView.findViewById(R.id.suggest_future_activity_icon);
//        futureBtn.setOnClickListener(this);

        //Animations
        animRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
        animScale = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_scale);

        setAnim = new AnimationSet(false);
        setAnim.addAnimation(animRotate);
        setAnim.addAnimation(animScale);

        dailyActivitySound = MediaPlayer.create(getActivity(), R.raw.daily_activity_sound);


        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.settings_icon:
                layoutInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.settings_menu, null);

                settingsWindow = new PopupWindow(container, 400, 400, true);
                settingsWindow.showAtLocation(v, Gravity.NO_GRAVITY, (int)settings.getX()-300, (int)settings.getY()+320);

                TextView logOut = container.findViewById(R.id.logOut);
                logOut.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getActivity());
                        }
                        builder.setTitle("Log Out")
                                .setMessage("Are you sure you want to Log Out?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent loginPage = new Intent(getActivity(), UserLoginActivity.class);
                                        HomeTab.this.startActivity(loginPage);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        settingsWindow.dismiss();

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        settingsWindow.dismiss();
                        return true;
                    }
                });

                break;

            case (R.id.listBGimage):
                GroupedActivities.getViewPager().setCurrentItem(1);
                break;

            case R.id.arrow:
                GroupedActivities.getViewPager().setCurrentItem(1);
                break;

            case R.id.activity_icon:
                GroupedActivities.getViewPager().setCurrentItem(1);
                break;

            case R.id.listText:
                GroupedActivities.getViewPager().setCurrentItem(1);
                break;

            case (R.id.calendarBGimage):

                break;

            case R.id.calendar_arrow:

                break;

            case R.id.calendar_icon:

                break;

            case R.id.calendar_text:

                break;

            case R.id.suggest_daily_activity_icon:

                v.startAnimation(setAnim);
                dailyActivitySound.start();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                                //DO Intent HERE to get Activity
                            }
                        }, 1200);
                break;

        }
    }
}
