package com.lonedev.androftpsync;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ProfileFoldersSyncItem extends TableLayout implements OnClickListener {

	public ProfileFoldersSyncItem(Context context) {
		super(context);
		this.setColumnStretchable(1, true);
		init();
	}
	
	private void init() {
		TableRow row1 = new TableRow(getContext());
		
		TextView localFolderTextView = new TextView(getContext());
		localFolderTextView.setText(R.string.profileLocalFolderPathString);
		row1.addView(localFolderTextView);
		
		EditText localFolderEditText = new EditText(getContext());
		row1.addView(localFolderEditText);
		
		Button removeButton = new Button(getContext());
		removeButton.setText(R.string.profileRemoveFolderSyncString);
		removeButton.setOnClickListener(this);
		row1.addView(removeButton);
		
		this.addView(row1);
		
		TableRow row2 = new TableRow(getContext());
		
		TextView remoteFolderTextView = new TextView(getContext());
		remoteFolderTextView.setText(R.string.profileRemoteFolderPathString);
		row2.addView(remoteFolderTextView);
		
		EditText remoteFolderEditText = new EditText(getContext());
		row2.addView(remoteFolderEditText);
		
		this.addView(row2);
	}

	@Override
	public void onClick(View view) {
		this.getRootView();
		view.refreshDrawableState();
		//
	}
}