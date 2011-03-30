package com.lonedev.androftpsync;

import java.io.File;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	private String TAG = "ProfileTabActivity";
	
	// I cannot for the life of me work out how to get back to the Activity 
	// instances that this TabActivity creates. Seeing as how this Activity
	// handles the menu buttons (Save, Sync etc), it needs to be able to get
	// to these classes data. Therefore, these classes will need to update 
	// the matching Activity below via its "onCreate(...)" method. Then, when
	// the user clicks a menu option, this class can reference the fields in
	// that activity. Crap solution in my book, but until I can find a way to
	// get to these instances, I'm stuck for cleaner solutions.
	public static Activity profileSettingsActivity, profileFoldersSyncActivity, profileScheduleActivity;
	
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
        Intent profileSettingsIntent = new Intent(this, ProfileSettings.class);
        settingTabSpec.setContent(profileSettingsIntent);
        
        foldersSyncTabSpec.setIndicator(foldersSyncString, getResources().getDrawable(R.drawable.profile_folders_tab));
        Intent profileFoldersSyncIntent = new Intent(this, ProfileFoldersSync.class);
        foldersSyncTabSpec.setContent(profileFoldersSyncIntent);
        
        scheduleTabSpec.setIndicator(scheduleString, getResources().getDrawable(R.drawable.profile_schedule_tab));
        Intent profileScheduleIntent = new Intent(this,ProfileSchedule.class);
        scheduleTabSpec.setContent(profileScheduleIntent);
        
        /** Add tabSpec to the TabHost to display. */
        tabHost.addTab(settingTabSpec);
        tabHost.addTab(foldersSyncTabSpec);
        tabHost.addTab(scheduleTabSpec);
        
        tabHost.setCurrentTab(1);
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
		ProfileSettings ps = (ProfileSettings)profileSettingsActivity;
		ProfileFoldersSync pfs = (ProfileFoldersSync)profileFoldersSyncActivity;
		Log.d(TAG, "Sync started");
		
		try {
			FTPUtils ftpUtils = new FTPUtils(ps.getFtpHostname(), ps.getFtpPort(), ps.getFtpUsername(), ps.getFtpPassword());
//			ftpUtils.connect();
			
			for (ProfileFoldersSyncItem syncItem : pfs.getProfileFoldersSyncItems()) {
				String localFolderString = syncItem.getLocalFolderText();
				String remoteFolderString = syncItem.getRemoteFolderText();
				Log.d(TAG, "Syncing from " + localFolderString + " to " + remoteFolderString);
				File localFolder = new File(localFolderString);
				File remoteFolder = new File(remoteFolderString);
				ftpUtils.syncToFTPDirectory(localFolder, remoteFolder);
			}
		} catch (Exception ex) {
			Log.e(TAG, "Sync errror: " + ex.toString());
		}
	}
}
