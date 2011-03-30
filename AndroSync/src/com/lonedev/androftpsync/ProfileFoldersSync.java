package com.lonedev.androftpsync;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ProfileFoldersSync extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Let the TabActivity (the parent) know that this is the current
        // ProfileFoldersSync instance.
        ProfileTabActivity.profileFoldersSyncActivity = this;
        
        setContentView(R.layout.profile_folders);
        
        createFolderSyncIndex();
        
        Button addButton = (Button)findViewById(R.id.addFolderSyncButton);
        addButton.setOnClickListener(this);
    }
    
    private void createFolderSyncIndex() {
    	LinearLayout folderSyncsLayout = (LinearLayout)findViewById(R.id.folderSyncsLayout);
    	folderSyncsLayout.addView(new ProfileFoldersSyncItem(this, folderSyncsLayout));
    }
    
    /**
     * All "children" of the foldersSyncLayout id will be instances of
     * ProfileFoldersSyncItem's. 
     * 
     * @return An array of the folders sync items that the user has added.
     */
    public ProfileFoldersSyncItem[] getProfileFoldersSyncItems() {
    	LinearLayout folderSyncsLayout = (LinearLayout)findViewById(R.id.folderSyncsLayout);
    	ProfileFoldersSyncItem[] syncItems = new ProfileFoldersSyncItem[folderSyncsLayout.getChildCount()];
    	
    	for (int i = 0; i < folderSyncsLayout.getChildCount(); i++) {
    		syncItems[i] = (ProfileFoldersSyncItem)folderSyncsLayout.getChildAt(i);
    	}
    	
    	return syncItems;
    }

	@Override
	public void onClick(View view) {
		createFolderSyncIndex();
	}
}
