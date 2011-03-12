package com.lonedev.androftpsync;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileSettings extends Activity implements OnClickListener {
	Logger logger = Logger.getLogger(ProfileSettings.class.toString());
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings);
        
        Button testButton = (Button)findViewById(R.id.testFTPButton);
        testButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View view) {
		String ftpHostname = getFtpHostname();
		String ftpUsername = getFtpUsername();
		String ftpPassword = getFtpPassword();
		
		String result = "NOTHING??";
		
		logger.log(Level.INFO, "Attempting to login to...");

		FTPClient ftpClient = new FTPClient();
		
		try {
			ftpClient.connect(ftpHostname);
			ftpClient.login(ftpUsername, ftpPassword);

			int reply = ftpClient.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				result = "FAIL: FTP server refused connection.";
			} else {
				result = "SUCCESS !!";
			}
		} catch (Exception ex) {
			result = "FAIL: " + ex;
		} finally {
			try {
				ftpClient.disconnect();
			} catch (Exception e) {
				// Doesn't matter if exception gets thrown... At least we tried.
			}
		}

		Context context = getApplicationContext();
		CharSequence text = result;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	private String getFtpHostname() {
		return getEditTextString(R.id.ftpHostname);
	}
	
	private String getFtpUsername() {
		return getEditTextString(R.id.ftpUsername);
	}
	
	private String getFtpPassword() {
		return getEditTextString(R.id.ftpPassword);
	}
	
	private String getEditTextString(int id) {
		EditText text = (EditText)findViewById(id);
		return text.getText().toString();
	}
}