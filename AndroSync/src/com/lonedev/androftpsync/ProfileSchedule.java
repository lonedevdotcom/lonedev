package com.lonedev.androftpsync;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileSchedule extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Let the TabActivity (the parent) know that this is the current
        // ProfileSchedule instance.
        ProfileTabActivity.profileScheduleActivity = this;

        TextView textview = new TextView(this);
        textview.setText(getString(R.string.scheduleFeatureComingSoon));
        setContentView(textview);
    }
}
