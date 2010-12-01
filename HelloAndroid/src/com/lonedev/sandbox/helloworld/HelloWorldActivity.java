package com.lonedev.sandbox.helloworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelloWorldActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button okButton = (Button) findViewById(R.id.okButton);
        
        okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				Intent intent = getIntent(); // The intent is what started this activity
				okButton.setText(R.string.button_pressed);
			}
		});
    }
}