package com.lonedev.androftpsync;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ProfileFoldersSyncItem extends LinearLayout implements OnClickListener {

	private ViewGroup parentView;
	private EditText localFolderEditText, remoteFolderEditText, includePatternEditText, excludePatternEditText;
	
	public ProfileFoldersSyncItem(Context context, ViewGroup parentView) {
		super(context);
		this.parentView = parentView;
		
		this.setOrientation(VERTICAL);
		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.setPadding(0, 5, 0, 0);
		
		init();
	}
	
	private void init() {
		TableLayout tl = new TableLayout(getContext());
		tl.setColumnStretchable(1, true);
		
		TableRow row1 = new TableRow(getContext());
		
		TextView localFolderTextView = new TextView(getContext());
		localFolderTextView.setText(R.string.profileLocalFolderPathString);
		row1.addView(localFolderTextView);
		
		localFolderEditText = new EditText(getContext());
		row1.addView(localFolderEditText);
		
		Button removeButton = new Button(getContext());
		removeButton.setText(R.string.profileRemoveFolderSyncString);
		removeButton.setOnClickListener(this);
		row1.addView(removeButton);
		
		tl.addView(row1);
		
		TableRow row2 = new TableRow(getContext());
		
		TextView remoteFolderTextView = new TextView(getContext());
		remoteFolderTextView.setText(R.string.profileRemoteFolderPathString);
		row2.addView(remoteFolderTextView);
		
		remoteFolderEditText = new EditText(getContext());
		row2.addView(remoteFolderEditText);
		
		tl.addView(row2);
		
		TableRow row3 = new TableRow(getContext());
		
		TextView includePatternTextView = new TextView(getContext());
		includePatternTextView.setText(R.string.fileIncludePatternString);
		row3.addView(includePatternTextView);
		
		includePatternEditText = new EditText(getContext());
		includePatternEditText.setText("*");
		row3.addView(includePatternEditText);
		
		tl.addView(row3);
		
		TableRow row4 = new TableRow(getContext());
		
		TextView excludePatternTextView = new TextView(getContext());
		excludePatternTextView.setText(R.string.fileExcludePatternString);
		row4.addView(excludePatternTextView);
		
		excludePatternEditText = new EditText(getContext());
		row4.addView(excludePatternEditText);
		
		tl.addView(row4);
		
		this.addView(tl);
		
    	View v = new View(getContext());
    	v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 2));
    	v.setBackgroundColor(Color.rgb(51, 51, 51));
    	this.addView(v);
	}

	@Override
	public void onClick(View view) {
		int folderSyncCount = 0;
		for (int i = 0; i < parentView.getChildCount(); i++) {
			if (parentView.getChildAt(i) instanceof ProfileFoldersSyncItem) {
				folderSyncCount++;
			}
		}
		
		if (folderSyncCount == 1) {
			CharSequence text = getContext().getString(R.string.folderRemoveDeniedBecauseLastOneString);
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(getContext(), text, duration);
			toast.show();
		} else {
			parentView.removeView(this);
		}
	}
	
	public String getLocalFolderText() {
		return this.localFolderEditText.getText().toString();
	}
	
	public String getRemoteFolderText() {
		return this.remoteFolderEditText.getText().toString();
	}
	
	public String getIncludePatternText() {
		return this.includePatternEditText.getText().toString();
	}
	
	public String getExcludePatternText() {
		return this.excludePatternEditText.getText().toString();
	}
}