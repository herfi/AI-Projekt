package com.example.ai_projekt;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShowShape extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_shape);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_shape, menu);
		return true;
	}

}
