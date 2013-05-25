package com.example.ai_projekt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class SmalCameraView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		takeASmalPicture();
	}

	protected void takeASmalPicture() {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		startActivityForResult(cameraIntent, 0); 
	}



protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    ImageView imageView = new ImageView(this);
	if (requestCode == 0) {
        Bitmap thumb = (Bitmap) data.getExtras().get("data"); 
        imageView.setImageBitmap(thumb);
        setContentView(imageView);
    }
}


	
}
