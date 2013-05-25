package com.example.ai_projekt;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class OpenCVImage extends Activity {
	private final static String TAG = "OpenCVLoader/BaseLoaderCallback";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_cvimage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.open_cvimage, menu);
		return true;
	}

	/*
	 * 
	 */
	public void showImage(View view) {
		ImageView imageView = new ImageView(this);
		Bitmap image;

		image = readImage();

		imageView.setImageBitmap(image);
		setContentView(imageView);
	}

	public void grayImage(View view) {
		ImageView imageView = new ImageView(this);
		Mat mImg = new Mat();
		Bitmap image;

		image = readImage();

		try {
			Utils.bitmapToMat(image, mImg);
		} catch (Exception e) {
			Log.i(TEXT_SERVICES_MANAGER_SERVICE,
					"Bild nicht als Mat umgewandelt");
		}

		Mat mGray = new Mat(mImg.rows(), mImg.cols(), CvType.CV_8UC1);
		Imgproc.cvtColor(mImg, mGray, Imgproc.COLOR_BGRA2GRAY);
		Bitmap bmp = Bitmap.createBitmap(mGray.cols(), mGray.rows(),
				Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mGray, bmp);
		imageView.setImageBitmap(bmp);
		setContentView(imageView);

	}

	public void gaussianBlurImage(View view) {
		ImageView imageView = new ImageView(this);
		Mat mImg = new Mat();
		Bitmap image;

		image = readImage();

		try {
			Utils.bitmapToMat(image, mImg);
		} catch (Exception e) {
			Log.i(TEXT_SERVICES_MANAGER_SERVICE,
					"Bild nicht als Mat umgewandelt");
		}

		// Mat mGray = new Mat(mImg.rows(), mImg.cols(), CvType.CV_8UC1);
		// Imgproc.cvtColor(mImg, mGray, Imgproc.COLOR_BGRA2GRAY);
		Imgproc.GaussianBlur(mImg, mImg, new Size(9, 9), 2, 2);
		Bitmap bmp = Bitmap.createBitmap(mImg.cols(), mImg.rows(),
				Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mImg, bmp);
		imageView.setImageBitmap(bmp);
		setContentView(imageView);

	}

	public void HoughCircle(View view) {
		ImageView imageView = new ImageView(this);
		Mat mImg = new Mat();
		Bitmap image;
		
		int iCannyUpperThreshold = 100;
		int iMinRadius = 20;
		int iMaxRadius = 400;
		int iAccumulator = 300;
		int iLineThickness = 10;
		
		image = readImage();

		try {
			Utils.bitmapToMat(image, mImg);
		} catch (Exception e) {
			Log.i(TEXT_SERVICES_MANAGER_SERVICE,
					"Bild nicht als Mat umgewandelt");
		}

		Mat mGray = new Mat(mImg.rows(), mImg.cols(), CvType.CV_8UC1);
		Imgproc.cvtColor(mImg, mGray, Imgproc.COLOR_BGRA2GRAY);
		Imgproc.GaussianBlur(mGray, mGray, new Size(9, 9), 2, 2);
		Mat circleImage = new Mat(mGray.rows(), mGray.cols(), CvType.CV_8UC1);
		
		/*
		Imgproc.HoughCircles(mGray, circleImage, Imgproc.CV_HOUGH_GRADIENT,
				2.0, mGray.rows() / 16, iCannyUpperThreshold, iAccumulator,
				iMinRadius, iMaxRadius);
		*/
		Imgproc.HoughCircles(mGray, circleImage, Imgproc.CV_HOUGH_GRADIENT,
				2.0, 100.0, 30, 200, 10, 400);
		
		Log.i(TEXT_SERVICES_MANAGER_SERVICE, "minDistants --> "
				+ mGray.rows() / 8);
		
		//Imgproc.HoughCircles( mGray, circleImage, Imgproc.CV_HOUGH_GRADIENT, 6.0, 5, 110, 70, 3, 200 );
		
		//Imgproc.HoughCircles(image, circles, method, dp, minDist, param1, param2, minRadius, maxRadius)
		
		Log.i(TEXT_SERVICES_MANAGER_SERVICE, "Kreise erkannt");
		if (circleImage.cols() > 0) {
			Log.i(TEXT_SERVICES_MANAGER_SERVICE, "Kreis größer als 0 --> "
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
				Core.circle(mImg, pt, radius,  new Scalar(255, 255, 0, 255),
						iLineThickness);
				Core.circle(mImg, pt, 3, new Scalar(255, 255, 0, 255), iLineThickness);

			}
		}
		
		/*
		float circle[] = new float[3];

		for (int i = 0; i < circleImage.cols(); i++)
		{
		circleImage.get(0, i, circle);
		org.opencv.core.Point center = new org.opencv.core.Point();
		center.x = circle[0];
		center.y = circle[1];
		Core.circle(mImg, center, (int) circle[2], new Scalar(255, 255, 0, 255), 4);
		}
		*/
		Log.i(TEXT_SERVICES_MANAGER_SERVICE, "Kreise gezeichnet");
		Bitmap bmp = Bitmap.createBitmap(mImg.cols(), mImg.rows(),
				Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mImg, bmp);
		imageView.setImageBitmap(bmp);
		setContentView(imageView);

	}

	public Bitmap readImage() {
		Bitmap tmp;
		Bitmap bmp32 = null;
		Bitmap rotated;

		try {
			tmp = BitmapFactory
					.decodeFile("/storage/sdcard0/AI_Projekt/test.jpg");
			bmp32 = tmp.copy(Bitmap.Config.ARGB_8888, true);
		} catch (Exception e) {
			Log.i(TEXT_SERVICES_MANAGER_SERVICE, "Hat nicht geklappt");
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		rotated = Bitmap.createBitmap(bmp32, 0, 0, bmp32.getWidth(),
				bmp32.getHeight(), matrix, true);

		return rotated;
	}

	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i("Load OpenCV", "OpenCV loaded successfully");
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
				mOpenCVCallBack);
	}
}
