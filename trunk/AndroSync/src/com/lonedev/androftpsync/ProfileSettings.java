package com.lonedev.androftpsync;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileSettings extends Activity implements OnClickListener {
	private static final String TAG = "ProfileSettings";
	
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
		int ftpPort = getFtpPort();
		
		String result = "NOTHING??";
		
		Log.i(TAG, "Test attempt login to " + ftpHostname + " as " + ftpUsername);

		FTPClient ftpClient = new FTPClient();
		
		try {
			ftpClient.connect(ftpHostname, ftpPort);
			ftpClient.login(ftpUsername, ftpPassword);

			int reply = ftpClient.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				result = getString(R.string.testFTPConnectionDeniedString);
				Log.w(TAG, result);
			} else {
				result = getString(R.string.testFTPSuccessString);
				Log.i(TAG, result);
			}
		} catch (Exception ex) {
			result = getString(R.string.testFTPFailString) + ex;
			Log.w(TAG, result);
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
	
	public String getFtpHostname() {
		return getEditTextString(R.id.ftpHostname);
	}
	
	public String getFtpUsername() {
		return getEditTextString(R.id.ftpUsername);
	}
	
	public String getFtpPassword() {
		return getEditTextString(R.id.ftpPassword);
	}
	
	public int getFtpPort() {
		return Integer.parseInt(getEditTextString(R.id.ftpPort));
	}
	
	private String getEditTextString(int id) {
		EditText text = (EditText)findViewById(id);
		return text.getText().toString();
	}
}