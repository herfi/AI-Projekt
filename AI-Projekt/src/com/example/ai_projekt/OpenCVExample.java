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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class OpenCVExample extends Activity implements CvCameraViewListener2,
		OnSeekBarChangeListener {
	private final static String TAG = "OpenCVLoader/BaseLoaderCallback";
	private CameraBridgeViewBase mOpenCvCameraView;

	private static final int VIEW_MODE_RGBA = 0;
	private static final int VIEW_MODE_HSV = 3;
	private static final int VIEW_MODE_CIRCLE = 5;
	private static final int VIEW_MODE_OCTAGON = 6;

	private int mViewMode;
	private Mat mRgba;
	private Mat mIntermediateMat;
	private Mat mGray;
	private int threshold = 1, minLineSize = 1, lineGap = 1;
	private int h_min = 0, h_max = 0, s_min = 0, s_max = 0, v_min = 0, v_max = 0;

	android.hardware.Camera camera;

	private MenuItem mItemPreviewRGBA;
	private MenuItem mItemPreviewHSV;
	private MenuItem mItemPreviewCircle;
	private MenuItem mItemPreviewOctagon;

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
		bar.setMax(255);
		bar.setOnSeekBarChangeListener(this); // set seekbar listener.

		bar2 = (SeekBar) findViewById(R.id.seekBar2); // make seekbar object
		bar2.setMax(255);
		bar2.setOnSeekBarChangeListener(this); // set seekbar listener.

		bar3 = (SeekBar) findViewById(R.id.seekBar3); // make seekbar object
		bar3.setMax(255);
		bar3.setOnSeekBarChangeListener(this); // set seekbar listener.

		bar4 = (SeekBar) findViewById(R.id.seekBar4); // make seekbar object
		bar4.setMax(255);
		bar4.setOnSeekBarChangeListener(this); // set seekbar listener.

		bar5 = (SeekBar) findViewById(R.id.seekBar5); // make seekbar object
		bar5.setMax(255);
		bar5.setOnSeekBarChangeListener(this); // set seekbar listener.

		bar6 = (SeekBar) findViewById(R.id.seekBar6); // make seekbar object
		bar6.setMax(255);
		bar6.setOnSeekBarChangeListener(this); // set seekbar listener.

		
		
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
		mItemPreviewHSV = menu.add("HSV");
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

		case VIEW_MODE_RGBA:
			// input frame has RBGA format
			mRgba = inputFrame.rgba();
			break;

		case VIEW_MODE_HSV:
			Imgproc.cvtColor(inputFrame.rgba(), mRgba, Imgproc.COLOR_RGB2HSV, 4);
			break;

		case VIEW_MODE_CIRCLE:
			mRgba = inputFrame.rgba();
			Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV, 4);
			Core.inRange(mIntermediateMat, new Scalar(h_min, s_min, v_min),
					new Scalar(h_max, s_max, v_max), mRgba);

			circleDetection(mRgba);
			break;

		case VIEW_MODE_OCTAGON:

			mRgba = inputFrame.rgba();

			Mat octagonImage = new Mat(mGray.rows(), mGray.cols(),
					CvType.CV_8UC1);

			Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
			Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA,
					4);
			Imgproc.HoughLinesP(mIntermediateMat, octagonImage, 1,
					Math.PI / 180, threshold, minLineSize, lineGap);

			double[][] vec2 = new double[octagonImage.cols()][5];

			// Imgproc.HoughLinesP(thresholdImage, lines, 1, Math.PI/180,
			// threshold, minLineSize, lineGap);

			for (int x = 0; x < octagonImage.cols(); x++) {
				double[] vec = octagonImage.get(0, x);
				double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];

				double m = (y2) - (y1) / (x2) - (x1);
				double b = y1 - (m) * x1;
				// double m2 =1;

				for (int m1 = 0; m1 < vec2.length; m1++) {
					if (x2 == 0)
						break;
					int alpha = (int) Math.toDegrees(Math.atan(m - vec2[m1][4]
							/ 1 + m * vec2[m1][4]));
					if (!(alpha >= 91) && !(alpha <= 89)) {

						vec2[x][0] = x1;
						vec2[x][1] = y1;
						vec2[x][2] = x2;
						vec2[x][3] = y2;
						vec2[x][4] = m;
						Point start = new Point(x1, y1);
						Point end = new Point(x2, y2);
						Core.line(mRgba, start, end, new Scalar(255, 0, 0), 3);
					}
				}

				// vec2[x][0] = x1;
				// vec2[x][1] = y1;
				// vec2[x][2] = x2;
				// vec2[x][3] = y2;
				// vec2[x][4] = m;

			}

			// for (int m1 = 0; m1 < vec2.length; m1++)
			// {
			// for (int m2 = 1; m2 < vec2.length; m2++)
			// {
			// int alpha = (int)
			// Math.toDegrees(Math.atan(vec2[m1][4]-vec2[m2][4]/1+vec2[m1][4]*vec2[m2][4]));
			//
			// if (!(alpha > 230) && !(alpha < 220)){
			// Point start = new Point(vec2[m1][0], vec2[m1][1]);
			// Point end = new Point(vec2[m1][2], vec2[m1][3]);
			// Core.line(mRgba, start, end, new Scalar(255,0,0), 3);
			//
			// Point start2 = new Point(vec2[m2][0], vec2[m2][1]);
			// Point end2 = new Point(vec2[m2][2], vec2[m2][3]);
			// Core.line(mRgba, start2, end2, new Scalar(255,0,0), 3);
			// }
			//
			// }
			// }

			break;
		}

		return mRgba;
	}

	public void circleDetection(Mat grau) {

		// Weichzeichner (GaussianBlur)
		Imgproc.GaussianBlur(grau, grau, new Size(9, 9), 2, 2);

		// Erzeugen einer Mat mit der Größe von mGray und einem Kanal.
		Mat circleImage = new Mat(grau.rows(), grau.cols(), CvType.CV_8UC1);

		// Kreise erkennen (HoughCircle)
		Imgproc.HoughCircles(grau, circleImage, Imgproc.CV_HOUGH_GRADIENT, 2,
				grau.rows() / 4, 200, 100, 0, 0);

		Log.i(TEXT_SERVICES_MANAGER_SERVICE, "minDistants --> " + grau.rows()
				/ 4);

		Log.i(TEXT_SERVICES_MANAGER_SERVICE, "Kreise erkannt");
		// Wenn Kreise gefunden wurden enthält circleImage Werte
		if (circleImage.cols() > 0) {
			Log.i(TEXT_SERVICES_MANAGER_SERVICE, "Kreis größer als 0 --> "
					+ circleImage.cols());

			// Schleife wird mit jedem Wert von circleImage (Vectoren)
			// durchgegangen.
			for (int x = 0; x < circleImage.cols(); x++) {

				double vCircle[] = circleImage.get(0, x);

				if (vCircle == null) {
					Log.i(TEXT_SERVICES_MANAGER_SERVICE,
							"vCircle ist leer --> " + circleImage.get(0, x));
					break;
				}

				// Bestimmung des Mittelpunktes vom Kreis
				Point pt = new Point(Math.round(vCircle[0]),
						Math.round(vCircle[1]));
				// Bestimmung des Radius vom Kreis
				int radius = (int) Math.round(vCircle[2]);

				// Gefundener Kreis wird gezeichnet
				Core.circle(mRgba, pt, radius, new Scalar(255, 255, 0, 255), 10);
				// Mittelpunkt des Kreis wird gezeichnet
				Core.circle(mRgba, pt, 3, new Scalar(255, 255, 0, 255), 10);
			}
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

		if (item == mItemPreviewRGBA) {
			mViewMode = VIEW_MODE_RGBA;
		} else if (item == mItemPreviewCircle) {
			mViewMode = VIEW_MODE_CIRCLE;
		} else if (item == mItemPreviewHSV) {
			mViewMode = VIEW_MODE_HSV;
		} else if (item == mItemPreviewOctagon) {
			mViewMode = VIEW_MODE_OCTAGON;
		}

		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (seekBar.getId() == bar.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_OCTAGON){
				textProgress1.setText("The value is: " + progress);
				threshold = progress;
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress1.setText("The value is: " + (int) (progress));
				h_min = (int) (progress);
			}
		}
		if (seekBar.getId() == bar2.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_OCTAGON){
				textProgress2.setText("The value is: " + progress);
				minLineSize = progress;
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress2.setText("The value is: " + (int) (progress));
				h_max = (int) (progress);
			}
		}

		if (seekBar.getId() == bar3.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_OCTAGON){
				textProgress3.setText("The value is: " + progress);
				lineGap = progress;
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress3.setText("The value is: " + (int) (progress));
				s_min = (int) (progress);
			}
		}

		if (seekBar.getId() == bar4.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_OCTAGON){
				textProgress4.setText("The value is: " + progress);
				threshold = progress;
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress4.setText("The value is: " + (int) (progress));
				s_max = (int) (progress);
			}
		}
		if (seekBar.getId() == bar5.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_OCTAGON){
				textProgress5.setText("The value is: " + progress);
				minLineSize = progress;
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress5.setText("The value is: " + (int) (progress));
				v_min = (int) (progress);
			}
		}

		if (seekBar.getId() == bar6.getId()) {
			if (progress != 0 && mViewMode == VIEW_MODE_OCTAGON){
				textProgress6.setText("The value is: " + progress);
				lineGap = progress;
			}
			else if (progress != 0 && mViewMode == VIEW_MODE_CIRCLE) {
				textProgress6.setText("The value is: " + (int) (progress));
				v_max = (int) (progress);
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