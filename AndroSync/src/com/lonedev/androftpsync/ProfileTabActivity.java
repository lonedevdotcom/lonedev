package com.lonedev.androftpsync;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * This is the class that defines the tabs breakdown of profile settings. 
 * It doesn't serve any purpose other than to display the profile settings,
 * folder syncs and schedule details.
 * 
 * @author Richard Hawkes
 */
public class ProfileTabActivity extends TabActivity {
	private Intent profileSettingsIntent, profileFoldersSyncIntent, profileScheduleIntent;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_tab_host);
        
        /** ProfileTabActivity will have Tabs!! */
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        
        String settingsString = getString(R.string.profileSettingsString);
        String foldersSyncString = getString(R.string.profileFoldersSyncString);
        String scheduleString = getString(R.string.profileScheduleString);
        
        TabSpec settingTabSpec = tabHost.newTabSpec(settingsString);
        TabSpec foldersSyncTabSpec = tabHost.newTabSpec(foldersSyncString);
        TabSpec scheduleTabSpec = tabHost.newTabSpec(scheduleString);
        
        settingTabSpec.setIndicator(settingsString, getResources().getDrawable(R.drawable.profile_settings_tab));
        profileSettingsIntent = new Intent(this, ProfileSettings.class);
        settingTabSpec.setContent(profileSettingsIntent);
        
        foldersSyncTabSpec.setIndicator(foldersSyncString, getResources().getDrawable(R.drawable.profile_folders_tab));
        profileFoldersSyncIntent = new Intent(this, ProfileFoldersSync.class);
        foldersSyncTabSpec.setContent(profileFoldersSyncIntent);
        
        scheduleTabSpec.setIndicator(scheduleString, getResources().getDrawable(R.drawable.profile_schedule_tab));
        profileScheduleIntent = new Intent(this,ProfileSchedule.class);
        scheduleTabSpec.setContent(profileScheduleIntent);
        
        /** Add tabSpec to the TabHost to display. */
        tabHost.addTab(settingTabSpec);
        tabHost.addTab(foldersSyncTabSpec);
        tabHost.addTab(scheduleTabSpec);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.profile_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	if (menuItem.getItemId() == R.id.syncNow) {
    		startSync();
    	} 
		return true;
    }

	private void startSync() {
//		FTPUtils ftpUtils = new FTPUtils();
		View profileSettingsViewGroup = getTabHost().getChildAt(1);
		profileSettingsViewGroup.bringToFront();
		return;
	}
}
