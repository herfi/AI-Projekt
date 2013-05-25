package com.example.ai_projekt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.ai_projekt.Message";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
   /* public void sendMessage(View view){
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }
   */
    
    public void takePicture(View view){
    	Intent intent = new Intent(this, CameraView.class);
    	startActivity(intent);
    }
    
    public void takeSmallPicture(View view){
    	Intent intent = new Intent(this, SmalCameraView.class);
    	startActivity(intent);
    }
    
    public void takeOpenCVPicture(View view){
    	Intent intent = new Intent(this, OpenCVExample.class);
    	startActivity(intent);
    }
    
    public void OpenCVImage(View view){
    	Intent intent = new Intent(this, OpenCVImage.class);
    	startActivity(intent);
    }
}
