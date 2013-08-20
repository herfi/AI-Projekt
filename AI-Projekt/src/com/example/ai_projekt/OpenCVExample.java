package com.example.ai_projekt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class OpenCVExample extends Activity implements CvCameraViewListener2,
		OnSeekBarChangeListener {
	private final static String TAG = "OpenCVLoader/BaseLoaderCallback";
	public static CameraBridgeViewBase mOpenCvCameraView;

	private static final int VIEW_MODE_RGBA = 0;
	private static final int VIEW_MODE_SHAPE_EXTRACTION = 3;
	private static final int VIEW_MODE_CIRCLE = 5;
	private static final int VIEW_MODE_HSV = 6;
	private static final int VIEW_MODE_SHAPE = 7;

	private int mViewMode;
	private static Mat mRgba;
	private static Mat mIntermediateMat;
	
	android.hardware.Camera camera;

	private MenuItem mItemPreviewRGBA;
	private MenuItem mItemPreviewShapeExtraction;
	private MenuItem mItemPreviewCircle;
	private MenuItem mItemHSV;
	private MenuItem mItemPreviewShape;

	private SeekBar bar; // declare seekbar object variable
	private SeekBar bar2; // declare seekbar object variable
	private SeekBar bar3; // declare seekbar object variable
	private SeekBar bar4; // declare seekbar object variable
	private SeekBar bar5; // declare seekbar object variable
	private SeekBar bar6; // declare seekbar object variable
	
	
	private TextView textProgress1;
	private TextView textProgress2;
	private TextView textProgress3;
	private TextView textProgress4;
	private TextView textProgress5;
	private TextView textProgress6;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_open_cvexample);
		// camera = Camera.open();
		// Camera.Parameters camera_param = camera.getParameters();
		// camera_param.setRotation(90);
		// camera.setParameters(camera_param);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setRotation(0);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		
		bar = (SeekBar) findViewById(R.id.seekBar1); // make seekbar object
		bar.setMax(220);
		bar.setProgress(1);
		bar.setOnSeekBarChangeListener(this); // set seekbar listener.
		Detection.setH_min(1);
		
		bar2 = (SeekBar) findViewById(R.id.seekBar2); // make seekbar object
		bar2.setMax(220);
		bar2.setProgress(13);
		bar2.setOnSeekBarChangeListener(this); // set seekbar listener.
		Detection.setH_max(13);

		bar3 = (SeekBar) findViewById(R.id.seekBar3); // make seekbar object
		bar3.setMax(300);
		bar3.setProgress(75);
		bar3.setOnSeekBarChangeListener(this); // set seekbar listener.
		Detection.setS_min(75);

		bar4 = (SeekBar) findViewById(R.id.seekBar4); // make seekbar object
		bar4.setMax(300);
		bar4.setProgress(255);
		bar4.setOnSeekBarChangeListener(this); // set seekbar listener.
		Detection.setS_max(255);

		bar5 = (SeekBar) findViewById(R.id.seekBar5); // make seekbar object
		bar5.setMax(300);
		bar5.setProgress(55);
		bar5.setOnSeekBarChangeListener(this); // set seekbar listener.
		Detection.setV_min(55);

		bar6 = (SeekBar) findViewById(R.id.seekBar6); // make seekbar object
		bar6.setMax(300);
		bar6.setProgress(255);
		bar6.setOnSeekBarChangeListener(this); // set seekbar listener.
		Detection.setV_max(255);
		
		// make text label for progress value
		textProgress1 = (TextView) findViewById(R.id.textView1);

		// make text label for progress value
		textProgress2 = (TextView) findViewById(R.id.textView2);

		// make text label for progress value
		textProgress3 = (TextView) findViewById(R.id.textView3);

		// make text label for progress value
		textProgress4 = (TextView) findViewById(R.id.textView4);

		// make text label for progress value
		textProgress5 = (TextView) findViewById(R.id.textView5);

		// make text label for progress value
		textProgress6 = (TextView) findViewById(R.id.textView6);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		mItemPreviewRGBA = menu.add("Preview RGBA");
		mItemPreviewShapeExtraction = menu.add("Shape extraction ");
		mItemPreviewCircle = menu.add("Find Circle");
		mItemHSV = menu.add("HSV");
		mItemPreviewShape = menu.add("Find Shaps");

		return true;
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
		Detection.matIni(width, height);
	}

	public void onCameraViewStopped() {
		mRgba.release();
		mIntermediateMat.release();
		Detection.matRelease();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		final int viewMode = mViewMode;
		switch (viewMode) {

		case VIEW_MODE_RGBA:
			// input frame has RBGA format
			mRgba = inputFrame.rgba();
			break;

		case VIEW_MODE_SHAPE_EXTRACTION:
			//Imgproc.cvtColor(inputFrame.rgba(), mRgba, Imgproc.COLOR_RGB2HSV, 4);
			mRgba = Detection.shapeDetectionAndExtraction(inputFrame);
			break;

		case VIEW_MODE_CIRCLE:
			mRgba = Detection.circleHsvDetection(inputFrame);
			
			break;
			
		case VIEW_MODE_HSV:

			mRgba = Detection.hsv(inputFrame);
			

			break;
			
		case VIEW_MODE_SHAPE:

			
			mRgba = Detection.shapeDetection(inputFrame);
			

			break;
		}

		return mRgba;
	}

	

	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

		if (item == mItemPreviewRGBA) {
			mViewMode = VIEW_MODE_RGBA;
		} else if (item == mItemPreviewCircle) {
			mViewMode = VIEW_MODE_CIRCLE;
		} else if (item == mItemPreviewShapeExtraction) {
			mViewMode = VIEW_MODE_SHAPE_EXTRACTION;
		} else if (item == mItemHSV) {
			mViewMode = VIEW_MODE_HSV;
		} else if (item == mItemPreviewShape) {
			mViewMode = VIEW_MODE_SHAPE;
		}

		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
	
		
		
		if (seekBar.getId() == bar.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_HSV){
				textProgress1.setText("The value is: " + progress);
				//Detection.setMaxCorners(progress);
				Detection.setH_min(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress1.setText("The value is: " + (int) (progress));
				Detection.setH_min(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE) {
				textProgress1.setText("The value is: " + progress);
				//Detection.setMaxCorners(progress);
				Detection.setH_min(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE_EXTRACTION) {
				textProgress1.setText("The value is: " + progress);
				//Detection.setMaxCorners(progress);
				Detection.setH_min(progress);
			}
		}
		if (seekBar.getId() == bar2.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_HSV){
				textProgress2.setText("The value is: " + progress);
				//Detection.setQualityLevel(progress);
				Detection.setH_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress2.setText("The value is: " + (int) (progress));
				Detection.setH_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE) {
				textProgress2.setText("The value is: " + progress);
				//Detection.setQualityLevel(progress);
				Detection.setH_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE_EXTRACTION) {
				textProgress2.setText("The value is: " + progress);
				//Detection.setQualityLevel(progress);
				Detection.setH_max(progress);
			}
		}

		if (seekBar.getId() == bar3.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_HSV){
				textProgress3.setText("The value is: " + progress);
				//Detection.setMinDistance(progress);
				Detection.setS_min(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress3.setText("The value is: " + (int) (progress));
				Detection.setS_min(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE) {
				textProgress3.setText("The value is: " + progress);
				//Detection.setMinDistance(progress);
				Detection.setS_min(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE_EXTRACTION) {
				textProgress3.setText("The value is: " + progress);
				//Detection.setMinDistance(progress);
				Detection.setS_min(progress);
			}
		}

		if (seekBar.getId() == bar4.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_HSV){
				textProgress4.setText("The value is: " + (int) (progress));
				Detection.setS_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress4.setText("The value is: " + (int) (progress));
				Detection.setS_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE) {
				textProgress4.setText("The value is: " + (int) (progress));
				Detection.setS_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE_EXTRACTION) {
				textProgress4.setText("The value is: " + (int) (progress));
				Detection.setS_max(progress);
			}
		}
		if (seekBar.getId() == bar5.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_HSV){
				textProgress4.setText("The value is: " + (int) (progress));
				Detection.setS_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress5.setText("The value is: " + (int) (progress));
				Detection.setV_min(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE) {
				textProgress5.setText("The value is: " + (int) (progress));
				Detection.setV_min(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE_EXTRACTION) {
				textProgress5.setText("The value is: " + (int) (progress));
				Detection.setV_min(progress);
			}
		}

		if (seekBar.getId() == bar6.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_HSV){
				progress=bar6.getProgress();
				textProgress6.setText("The value is: " + (int) (progress));
				Detection.setV_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress6.setText("The value is: " + (int) (progress));
				Detection.setV_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE) {
				progress=bar6.getProgress();
				textProgress6.setText("The value is: " + (int) (progress));
				Detection.setV_max(progress);
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_SHAPE_EXTRACTION) {
				progress=bar6.getProgress();
				textProgress6.setText("The value is: " + (int) (progress));
				Detection.setV_max(progress);
			}
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		seekBar.setSecondaryProgress(seekBar.getProgress()); // set the shade of
																// the previous
																// value.
	}

}
