package com.lonedev.androftpsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ProfileExplorer extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_explorer);
        
        AndroSyncDB database = new AndroSyncDB(this);
        database.getReadableDatabase();
        
        TextView newProfile = (TextView)findViewById(R.id.newProfile);
        newProfile.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, ProfileSettings.class);
		startActivity(intent);
	}
}
