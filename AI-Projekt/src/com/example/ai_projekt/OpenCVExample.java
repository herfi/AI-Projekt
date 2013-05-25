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

public class OpenCVExample extends Activity implements CvCameraViewListener2 {
	private final static String TAG = "OpenCVLoader/BaseLoaderCallback";
	private CameraBridgeViewBase mOpenCvCameraView;

	private static final int VIEW_MODE_RGBA = 0;
	private static final int VIEW_MODE_GRAY = 1;
	private static final int VIEW_MODE_CANNY = 2;
	private static final int VIEW_MODE_CIRCLE = 5;
	private static final int VIEW_MODE_OCTAGON = 6;

	private int mViewMode;
	private Mat mRgba;
	private Mat mIntermediateMat;
	private Mat mGray;

	private MenuItem mItemPreviewRGBA;
	private MenuItem mItemPreviewGray;
	private MenuItem mItemPreviewCanny;
	private MenuItem mItemPreviewCircle;
	private MenuItem mItemPreviewOctagon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_open_cvexample);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		mItemPreviewRGBA = menu.add("Preview RGBA");
		mItemPreviewGray = menu.add("Preview GRAY");
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
		case VIEW_MODE_CIRCLE:
			// input frame has RGBA format
			mRgba = inputFrame.rgba();
			mGray = inputFrame.gray();
			// FindFeatures(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr());

			Imgproc.GaussianBlur(mGray, mGray, new Size(9, 9), 2, 2);
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
			Imgproc.HoughLinesP(mIntermediateMat, octagonImage, 1, Math.PI/180, 50, 20, 20);

			
			
			//Imgproc.HoughLinesP(thresholdImage, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);

		    for (int x = 0; x < octagonImage.cols() && x < 1; x++) 
		    {
		          double[] vec = octagonImage.get(0, x);
		          double x1 = vec[0], 
		                 y1 = vec[1],
		                 x2 = vec[2],
		                 y2 = vec[3];
		          Point start = new Point(x1, y1);
		          Point end = new Point(x2, y2);

		          Core.line(mRgba, start, end, new Scalar(255,0,0), 3);

		    }
			
			
//			// input frame has gray scale format
//			mRgba = inputFrame.rgba();
//			mGray = inputFrame.gray();
//			
//			//Mat octagonImage = new Mat(mGray.rows(), mGray.cols(),
//			//		CvType.CV_8UC1);
//			
//			Imgproc.Canny(mGray, mIntermediateMat, 80, 100);
//			Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA,4);
//			Imgproc.HoughLines(mIntermediateMat, mRgba, 1, Math.PI/180, 100);
//			
//			if (octagonImage.cols() > 0) {
//				Log.i(TEXT_SERVICES_MANAGER_SERVICE, "Kreis gr��er als 0 --> "
//						+ octagonImage.cols());
//				for (int x = 0; x < octagonImage.cols(); x++) {
//
//					double vCircle[] = octagonImage.get(0, x);
//
//					if (vCircle == null) {
//						Log.i(TEXT_SERVICES_MANAGER_SERVICE,
//								"vCircle ist leer --> " + octagonImage.get(0, x));
//						break;
//					}
//					
//					Core.line(img, pt1, pt2, color)
//							new Scalar(255, 255, 0, 255), 10);
//					Core.circle(mRgba, pt, 3, new Scalar(255, 255, 0, 255), 10);
//
//				}
//			}
//			Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA,
//					4);
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
		} else if (item == mItemPreviewOctagon) {
			mViewMode = VIEW_MODE_OCTAGON;
		}

		return true;
	}

	public native void FindFeatures(long matAddrGr, long matAddrRgba);
}