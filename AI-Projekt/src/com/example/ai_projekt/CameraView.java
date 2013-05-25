package com.example.ai_projekt;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class CameraView extends Activity {

private static final String LOG_TAG = CameraView.class.getSimpleName();
	
	private boolean done = true;
	File sdDir;
	DateFormat dateFormat = new SimpleDateFormat("ddMMyyyykkmm"); 
	String datumAktuell = dateFormat.format(new Date());
	
	protected static final String PHOTO_TAKEN = "photo_taken";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		try {
			super.onCreate(savedInstanceState);         
			File root = new File(Environment.getExternalStorageDirectory() + File.separator + "AI_Projekt" + File.separator);
			root.mkdirs();
			//sdDir = new File(root, datumAktuell +".jpg");
			sdDir = new File(root, "test.jpg");
			Log.d(LOG_TAG, "Creating image storage file: " + sdDir.getPath());
			startCameraActivity();
		} catch (Exception e) {
			finish();
		}

	}

	
	
	protected void startCameraActivity() {
		Uri outputFileUri = Uri.fromFile(sdDir);
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Intent intent = new Intent();
		intent.putExtra("uri", sdDir.getPath());
		setResult(0, intent);
		finish();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.getBoolean(CameraView.PHOTO_TAKEN)) {
			done = true;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(CameraView.PHOTO_TAKEN,  done);
	}


}

