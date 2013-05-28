package com.example.ai_projekt;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class OpenCVExample extends Activity implements CvCameraViewListener2, OnSeekBarChangeListener {
	private final static String TAG = "OpenCVLoader/BaseLoaderCallback";
	private CameraBridgeViewBase mOpenCvCameraView;

	private static final int VIEW_MODE_RGBA = 0;
	private static final int VIEW_MODE_GRAY = 1;
	private static final int VIEW_MODE_CANNY = 2;
	private static final int VIEW_MODE_HSV = 3;
	private static final int VIEW_MODE_CIRCLE = 5;
	private static final int VIEW_MODE_OCTAGON = 6;

	private int mViewMode;
	private Mat mRgba;
	private Mat mIntermediateMat;
	private Mat mGray;
	private int threshold=1, minLineSize=1, lineGap=1;
	android.hardware.Camera camera;

	private MenuItem mItemPreviewRGBA;
	private MenuItem mItemPreviewGray;
	private MenuItem mItemPreviewHSV;
	private MenuItem mItemPreviewCanny;
	private MenuItem mItemPreviewCircle;
	private MenuItem mItemPreviewOctagon;
	
	private SeekBar bar; // declare seekbar object variable
	private SeekBar bar2; // declare seekbar object variable
	private SeekBar bar3; // declare seekbar object variable
	
	private TextView textProgress1,textAction1;
	private TextView textProgress2,textAction2;
	private TextView textProgress3,textAction3;
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_open_cvexample);
//		camera = Camera.open();
//		Camera.Parameters camera_param = camera.getParameters();
//		camera_param.setRotation(90);
//		camera.setParameters(camera_param);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setRotation(0);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		
		bar = (SeekBar)findViewById(R.id.seekBar1); // make seekbar object
        bar.setOnSeekBarChangeListener(this); // set seekbar listener.
        
        bar2 = (SeekBar)findViewById(R.id.seekBar2); // make seekbar object
        bar2.setOnSeekBarChangeListener(this); // set seekbar listener.
        
        bar3 = (SeekBar)findViewById(R.id.seekBar3); // make seekbar object
        bar3.setOnSeekBarChangeListener(this); // set seekbar listener.
        
     // make text label for progress value
        textProgress1 = (TextView)findViewById(R.id.textView1);
     // make text label for action
        textAction1 = (TextView)findViewById(R.id.textView4);
        
     // make text label for progress value
        textProgress2 = (TextView)findViewById(R.id.textView2);
     // make text label for action
        textAction2 = (TextView)findViewById(R.id.textView5);
        
     // make text label for progress value
        textProgress3 = (TextView)findViewById(R.id.textView3);
     // make text label for action
        textAction3 = (TextView)findViewById(R.id.textView6);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		mItemPreviewRGBA = menu.add("Preview RGBA");
		mItemPreviewGray = menu.add("Preview GRAY");
		mItemPreviewHSV = menu.add("HSV");
		mItemPreviewCanny = menu.add("Canny");
		mItemPreviewCircle = menu.add("Find Circle");
		mItemPreviewOctagon = menu.add("Find Octagon");

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
		mGray = new Mat(height, width, CvType.CV_8UC1);

	}

	public void onCameraViewStopped() {
		mRgba.release();
		mGray.release();
		mIntermediateMat.release();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		final int viewMode = mViewMode;
		switch (viewMode) {
		case VIEW_MODE_GRAY:
			// input frame has gray scale format

			Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA,
					4);
			break;
		case VIEW_MODE_RGBA:
			// input frame has RBGA format
			mRgba = inputFrame.rgba();
			break;
		case VIEW_MODE_CANNY:
			// input frame has gray scale format
			mRgba = inputFrame.rgba();
			Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
			Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA,
					4);
			break;
		
		case VIEW_MODE_HSV:
			Imgproc.cvtColor(inputFrame.rgba(), mRgba, Imgproc.COLOR_RGB2HSV, 4);
			break;
			
		case VIEW_MODE_CIRCLE:
			// input frame has RGBA format
			mRgba = inputFrame.rgba();

			Imgproc.GaussianBlur(inputFrame.gray(), mGray, new Size(9, 9), 2, 2);
			Mat circleImage = new Mat(mGray.rows(), mGray.cols(),
					CvType.CV_8UC1);
			/*
			 * Imgproc.HoughCircles(mGray, circleImage,
			 * Imgproc.CV_HOUGH_GRADIENT, 2.0, mGray.rows() / 4, 100, 250, 10,
			 * 400);
			 */
			Imgproc.HoughCircles(mGray, circleImage, Imgproc.CV_HOUGH_GRADIENT,
					2.0, 100.0, 30, 200, 10, 400);

			Log.i(TEXT_SERVICES_MANAGER_SERVICE,
					"minDistants --> " + mGray.rows() / 4);

			Log.i(TEXT_SERVICES_MANAGER_SERVICE, "Kreise erkannt");
			if (circleImage.cols() > 0) {
				Log.i(TEXT_SERVICES_MANAGER_SERVICE, "Kreis gr��er als 0 --> "
						+ circleImage.cols());
				for (int x = 0; x < circleImage.cols(); x++) {

					double vCircle[] = circleImage.get(0, x);

					if (vCircle == null) {
						Log.i(TEXT_SERVICES_MANAGER_SERVICE,
								"vCircle ist leer --> " + circleImage.get(0, x));
						break;
					}

					Point pt = new Point(Math.round(vCircle[0]),
							Math.round(vCircle[1]));
					int radius = (int) Math.round(vCircle[2]);

					// draw the found circle
					Core.circle(mRgba, pt, radius,
							new Scalar(255, 255, 0, 255), 10);
					Core.circle(mRgba, pt, 3, new Scalar(255, 255, 0, 255), 10);

				}
			}
			
			break;
		

	case VIEW_MODE_OCTAGON:


		mRgba = inputFrame.rgba();

		Mat octagonImage = new Mat(mGray.rows(), mGray.cols(),CvType.CV_8UC1);

		Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
		Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA,
		4);
		Imgproc.HoughLinesP(mIntermediateMat, octagonImage, 1, Math.PI/180, threshold,minLineSize,lineGap);

		double[][] vec2= new double[octagonImage.cols()][5];


		//Imgproc.HoughLinesP(thresholdImage, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);

		for (int x = 0; x < octagonImage.cols(); x++)
		{
		double[] vec = octagonImage.get(0, x);
		double x1 = vec[0],
		y1 = vec[1],
		x2 = vec[2],
		y2 = vec[3];
		
		double m = (y2)-(y1)/(x2)-(x1);
		double b = y1-(m)*x1;
		//double m2 =1;
		vec2[x][0] = x1;
		vec2[x][1] = y1;
		vec2[x][2] = x2;
		vec2[x][3] = y2;
		vec2[x][4] = m;
		
		}
		
		for (int m1 = 0; m1 < vec2.length; m1++)
		{
			for (int m2 = 1; m2 < vec2.length; m2++)
			{
				int alpha = (int) Math.toDegrees(Math.atan(vec2[m1][4]-vec2[m2][4]/1+vec2[m1][4]*vec2[m2][4]));
				
				if (!(alpha > 230) &&  !(alpha < 220)){
				Point start = new Point(vec2[m1][0], vec2[m1][1]);
				Point end = new Point(vec2[m1][2], vec2[m1][3]);
				Core.line(mRgba, start, end, new Scalar(255,0,0), 3);
		
				Point start2 = new Point(vec2[m2][0], vec2[m2][1]);
				Point end2 = new Point(vec2[m2][2], vec2[m2][3]);
				Core.line(mRgba, start2, end2, new Scalar(255,0,0), 3);
				}
				
			}
		}


		
		break;
		}
	
		return mRgba;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

		if (item == mItemPreviewRGBA) {
			mViewMode = VIEW_MODE_RGBA;
		} else if (item == mItemPreviewGray) {
			mViewMode = VIEW_MODE_GRAY;
		} else if (item == mItemPreviewCanny) {
			mViewMode = VIEW_MODE_CANNY;
		} else if (item == mItemPreviewCircle) {
			mViewMode = VIEW_MODE_CIRCLE;
		} else if (item == mItemPreviewHSV) {
			mViewMode = VIEW_MODE_HSV;
		}else if (item == mItemPreviewOctagon) {
			mViewMode = VIEW_MODE_OCTAGON;
		}

		return true;
	}

	public native void FindFeatures(long matAddrGr, long matAddrRgba);

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (seekBar.getId()==bar.getId()){
		textProgress1.setText("The value is: "+progress);
		if (progress!=0)
		threshold=progress;
		}
		if (seekBar.getId()==bar2.getId()){
		textProgress2.setText("The value is: "+progress);
		if (progress!=0)
		minLineSize=progress;
		}
		
		if (seekBar.getId()==bar3.getId()){
		textProgress3.setText("The value is: "+progress);
		if (progress!=0)
		lineGap=progress;
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		textAction1.setText("starting to track touch");
		textAction2.setText("starting to track touch");
		textAction3.setText("starting to track touch");
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		seekBar.setSecondaryProgress(seekBar.getProgress()); // set the shade of the previous value.
		textAction1.setText("ended tracking touch");
	}

	
}