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
        ProfileTabActivity.profileFoldersSync = this;
        
        setContentView(R.layout.profile_folders);
        
        createFolderSyncIndex();
        
        Button addButton = (Button)findViewById(R.id.addFolderSyncButton);
        addButton.setOnClickListener(this);
    }
    
    private void createFolderSyncIndex() {
    	LinearLayout folderSyncsLayout = (LinearLayout)findViewById(R.id.folderSyncsLayout);
    	folderSyncsLayout.addView(new ProfileFoldersSyncItem(this, folderSyncsLayout));
    }

	@Override
	public void onClick(View view) {
		createFolderSyncIndex();
	}
}
