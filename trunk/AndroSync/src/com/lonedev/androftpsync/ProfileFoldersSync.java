package com.lonedev.androftpsync;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class ProfileFoldersSync extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_folders);
        
        createFolderSyncIndex();
    }
    
    private void createFolderSyncIndex() {
    	LinearLayout folderSyncsLayout = (LinearLayout)findViewById(R.id.folderSyncsLayout);
    	folderSyncsLayout.addView(new ProfileFoldersSyncItem(this, folderSyncsLayout));
    }
}
