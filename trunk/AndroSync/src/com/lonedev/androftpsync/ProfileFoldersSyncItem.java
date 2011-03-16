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

	private ViewGroup parentView;
	
	public ProfileFoldersSyncItem(Context context, ViewGroup parentView) {
		super(context);
		this.parentView = parentView;
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
		
		TableRow row3 = new TableRow(getContext());
		
		TextView includePatternTextView = new TextView(getContext());
		includePatternTextView.setText(R.string.fileIncludePatternString);
		row3.addView(includePatternTextView);
		
		EditText includePatternEditText = new EditText(getContext());
		includePatternEditText.setText("*");
		row3.addView(includePatternEditText);
		
		this.addView(row3);
		
		TableRow row4 = new TableRow(getContext());
		
		TextView excludePatternTextView = new TextView(getContext());
		excludePatternTextView.setText(R.string.fileExcludePatternString);
		row4.addView(excludePatternTextView);
		
		EditText excludePatternEditText = new EditText(getContext());
		row4.addView(excludePatternEditText);
		
		this.addView(row4);
	}

	@Override
	public void onClick(View view) {
		parentView.removeView(this);
	}
}