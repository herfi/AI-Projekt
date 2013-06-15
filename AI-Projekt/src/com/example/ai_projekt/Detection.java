package com.example.ai_projekt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
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

import android.util.Log;

public class Detection {
	
	private static Mat mRgba;
	private static Mat mIntermediateMat;
	private static Mat mGray;
	private static int threshold = 1, minLineSize = 1, lineGap = 1;
	private static int h_min = 0, h_max = 0, s_min = 0, s_max = 0, v_min = 0, v_max = 0;
	private static int maxCorners = 4;
	private static double qualityLevel = 0.001, minDistance = 1.0;
	
	android.hardware.Camera camera;
	
public static Mat circleHsvDetection(CvCameraViewFrame inputFrame){
		
		mRgba = inputFrame.rgba();
		Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV, 4);
		Core.inRange(mIntermediateMat, new Scalar(h_min, s_min, v_min),
				new Scalar(h_max, s_max, v_max), mIntermediateMat);

		circleDetection(mIntermediateMat);
	
		return mRgba;
	}
	
public static Mat squareHsvDetection(CvCameraViewFrame inputFrame){
	mRgba = inputFrame.rgba();
	
	/*
	Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV, 4);
	Core.inRange(mIntermediateMat, new Scalar(0,120,120), new Scalar(40,255,255), mIntermediateMat);
		
	Mat lines = new Mat(mIntermediateMat.rows(), mIntermediateMat.cols(), CvType.CV_8UC1);
	
	//Imgproc.GaussianBlur(inputFrame.gray(), mGray, new Size(9, 9), 2, 2);
	Imgproc.Canny(mIntermediateMat, mIntermediateMat, 80, 100);
	
	
	Imgproc.HoughLinesP(mIntermediateMat, lines, 1, Math.PI /180, 70, 30, 10);
	*/
	
	Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV, 4);
	Core.inRange(mIntermediateMat, new Scalar(0,120,120), new Scalar(40,255,255), mRgba);
		
	Mat lines = new Mat(mRgba.rows(), mRgba.cols(), CvType.CV_8UC1);
	
	//Imgproc.GaussianBlur(inputFrame.gray(), mGray, new Size(9, 9), 2, 2);
	//Imgproc.Canny(mRgba, mRgba, 80, 100);
	
	
	Imgproc.HoughLinesP(mRgba, lines, 1, Math.PI /180, 70, 30, 10);
/*	
if(lines.cols() > 0){
	for (int i = 0; i < lines.cols(); i++)
	{
	    double[] veclines = lines.get(0, i);
	    
	    if (veclines == null)
            break;
	    
	    lines.put(0, i, 0);
	    lines.put(0, i, (veclines[1] - veclines[3]) / (veclines[0] - veclines[2]) * -veclines[0] + veclines[1]); 
	    lines.put(0, i, mIntermediateMat.cols()); 
	    lines.put(0, i, (veclines[1] - veclines[3]) / (veclines[0] - veclines[2]) * (mIntermediateMat.cols() - veclines[2]) + veclines[3]);
	}
}
	*/
	
	List<Point> approxList = new LinkedList<Point>();
	List<Point> squareList = new LinkedList<Point>();
	if(lines.cols() > 0){
	for (int i = 0; i < lines.cols(); i++)
	{
	    for (int j = i+1; j < lines.cols(); j++)
	    {
	    	double[] vecI = lines.get(0, i);
	    	double[] vecJ = lines.get(0, j);
	    	
	    	if(vecI == null || vecJ == null)
	    		break;
	    	
	    	Point pt = computeIntersect(vecI, vecJ);
	    	
	    	if(pt.x >= 0 && pt.y >= 0)
	    	approxList.add(pt);
	    	
	    }
	}
	}
	MatOfPoint2f contour2f = new MatOfPoint2f();
	MatOfPoint2f approx2f = new MatOfPoint2f();	    	
	if(approxList.size() > 0){
	for(int i=0; i < approxList.size(); i++ ){
		contour2f.fromList(approxList);
	}
	
		Imgproc.approxPolyDP(contour2f, approx2f, Imgproc.arcLength(contour2f, true)*0.02, true);
    	
		squareList.addAll(approx2f.toList()); 
	
	if(squareList.size() == 3){
		for(int i=1; i < squareList.size(); i++){
			
			Point pt1 = squareList.get(i);
			
			Point pt2 = squareList.get(i-1);
			// Mittelpunkt des Kreis wird gezeichnet
			//Core.circle(mRgba, pt, 3, new Scalar(255, 255, 0, 255), 10);
				Core.line(mRgba, pt1, pt2, new Scalar(255, 255, 0, 255), 10);
		
		}
	}
	}
	/*
if(lines.cols() > 0){
	for(int i=0; i < lines.cols(); i++){
		
		
		double[] vecLines = lines.get(0, i);
		if (vecLines == null)
            break;

		
		double x1 = vecLines[0], 
                 y1 = vecLines[1],
                 x2 = vecLines[2],
                 y2 = vecLines[3];
          Point start = new Point(x1, y1);
          Point end = new Point(x2, y2);

          Core.line(mRgba, start, end, new Scalar(255,0,0), 3);
	}
}
	*/
	return mRgba;
}

public static Point computeIntersect(double[] a, double[] b)
{
	Point pt= new Point();
    double x1 = a[0], y1 = a[1], x2 = a[2], y2 = a[3];
    double x3 = b[0], y3 = b[1], x4 = b[2], y4 = b[3];

    double d = ((x1-x2) * (y3-y4)) - ((y1-y2) * (x3-x4));
    
    if(d > 0)
    {
        pt.x = ((x1*y2 - y1*x2) * (x3-x4) - (x1-x2) * (x3*y4 - y3*x4)) / d;
        pt.y = ((x1*y2 - y1*x2) * (y3-y4) - (y1-y2) * (x3*y4 - y3*x4)) / d;
        return pt;
    }
    else
    	pt.x = -1;
    	pt.y = -1;
        return pt;
}

	public static Mat octagonDetection(CvCameraViewFrame inputFrame){
		
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
		return mRgba;
	}
	
//public static Mat cornerDetection(CvCameraViewFrame inputFrame){
//		
//		mRgba = inputFrame.rgba();
//
//		MatOfPoint corners = new MatOfPoint();
//		
//		
//		Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
//		Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA,
//				4);
//
//		Imgproc.goodFeaturesToTrack(mIntermediateMat, corners, maxCorners, qualityLevel, minDistance);
//		
//		
//
//		
//		 for (int x = 0; x < corners.cols() ; x++) {
//			 for (int y = 0; y < corners.rows() ; y++) {
//				double[] vec = corners.get(y, x);
//				double x1 = vec[0], y1 = vec[1];
//				
//				
//							
//				
//				Point start = new Point(x1, y1);
//					//	Point end = new Point(x2, y2);
//				Core.circle(mRgba, start, 10, new Scalar(255, 0, 0));
//					//	Core.line(mRgba, start, end, new Scalar(255, 0, 0), 3);
//			 }
//		}
//		
//
//		return mRgba;
//	}

public static Mat shapeDetection(CvCameraViewFrame inputFrame){
	
	
		
	mRgba = inputFrame.rgba();

	MatOfPoint2f approx2f = new MatOfPoint2f();
	MatOfPoint approx = new MatOfPoint();
	MatOfPoint2f contour2f = new MatOfPoint2f();
	List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
	List<Point> approxList = new Vector<Point>();
    List<Double> cosine = new LinkedList<Double>();
    
    
    Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV, 4);
	Core.inRange(mIntermediateMat, new Scalar(h_min, s_min, v_min),
			new Scalar(h_max, s_max, v_max), mRgba);
	//Imgproc.dilate(mRgba, mRgba, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3)));
	//Imgproc.Canny(mRgba, mIntermediateMat, 100, 100);
	//Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA,
		//	4);

	Imgproc.findContours(mRgba, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
	
	
	
	
	//for (int idx = 0; idx < contours.size(); idx++) {
		
		
		
		
	    
//	    double contourarea = Imgproc.contourArea(contour2f);
//	    
//	    if (contourarea > maxArea) {
//	        maxArea = contourarea;
//	        maxAreaIdx = idx;
//	    }
	    
	    
	    
	    
	    
	    for (int i = 0; i < contours.size(); i++)
	    {
	    	MatOfPoint contour = contours.get(i);
			
			contour2f.fromArray(contour.toArray());
	    	
	    	
	    	
	    	Imgproc.approxPolyDP(contour2f, approx2f, /*Imgproc.arcLength(contour2f, true)*0.02*/ 3, true);
	    	approx.fromArray(approx2f.toArray());
	    	
	    	Converters.Mat_to_vector_Point(approx2f, approxList);
	    	
	    	//Fl�chenberechnung der Contour
	    	double area = Imgproc.contourArea(contour);
	        Rect r = Imgproc.boundingRect(contour);
	        int radius = r.width / 2;
	    	
	       
	       double a = (Math.abs(1 - ((double)r.width / r.height)));
	       double b = (Math.abs(1 - (area / (Math.PI * Math.pow(radius, 2)))));
	        
	        
	        double kreisFlaeche = (Math.PI * Math.pow(radius, 2));
	        double achteckFlaeche = (Math.pow(radius, 2)*(2*(1+Math.sqrt(2))));
	        
	        //Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE, "Kreisfläche: " + kreisFlaeche);
	        //Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE, "Achteckfläche: " + achteckFlaeche);
	    	
	    	if (Math.abs((Imgproc.contourArea(contour))) > 1000 && Imgproc.isContourConvex(approx) && (int)(approx.total()) > 2 && (int)(approx.total()) < 9)
	        {
	    		if ((int)(approx.total()) == 3){
	    			Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE, "Dreieck!");
	    			squares.add(approx);	    	
	    		}
	    		else if((int)(approx.total()) >= 4 && (int)(approx.total()) <= 8  )
	        	cosine.clear();
	            double maxCosine = 0;
	            double minCosine = 0;
	            int vtc = (int)(approx.total());
	            

	            for( int j = 2; j < vtc+1; j++ )
	            {
	                cosine.add(angle(approxList.get(j%vtc), approxList.get(j-2), approxList.get(j-1)));
	            }
	            
	            Collections.sort(cosine);
	            maxCosine = cosine.get(vtc-2);
	            minCosine = cosine.get(0);

	            
	            if(vtc == 4 && minCosine >= -0.1 && maxCosine <= 0.3 )
	            {
	            Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE, "Viereck!");
	            squares.add(approx);
	            
	            }
	            //else if (vtc == 5 && minCosine >= -0.34 && maxCosine <= -0.27)
	            	//squares.add(approx);	     
	            
	            
	            //else if (vtc == 6 && minCosine >= -0.55 && maxCosine <= -0.45)
	            	//squares.add(approx);
	            
	            else if (vtc == 8){
	            	Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE, "Achteck!");
	            	squares.add(approx);
	            }
	        }
	    	if(Math.abs((Imgproc.contourArea(contour))) > 1000 && Imgproc.isContourConvex(approx) && a <= 0.2 && b <= 0.2){
	    		Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE, "Kreis!");
	    		squares.add(approx);
	        }
	    	
	    	for (int j = 0; j < squares.size(); j++)
		    {

		    Imgproc.drawContours(mRgba, squares, j, new Scalar(255, 255, 0, 255), 10);
		    }

	    }
	    

	    
	    
	    
	    
	    
	//}
	


		

	return mRgba;
}




	static double angle(Point pt1, Point pt2, Point pt0)
	{
	
		double dx1 = pt1.x - pt0.x;
		double dy1 = pt1.y - pt0.y;
		double dx2 = pt2.x - pt0.x;
		double dy2 = pt2.y - pt0.y;
		
		return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
	}
	
	public static void circleDetection(Mat grau) {

		// Weichzeichner (GaussianBlur)
		Imgproc.GaussianBlur(grau, grau, new Size(9, 9), 2, 2);

		// Erzeugen einer Mat mit der Gr��e von mGray und einem Kanal.
		Mat circleImage = new Mat(grau.rows(), grau.cols(), CvType.CV_8UC1);

		// Kreise erkennen (HoughCircle)
		Imgproc.HoughCircles(grau, circleImage, Imgproc.CV_HOUGH_GRADIENT, 2,
				grau.rows() / 4, 200, 100, 0, 0);

		Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE, "minDistants --> " + grau.rows()
				/ 4);

		Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE, "Kreise erkannt");
		// Wenn Kreise gefunden wurden enth�lt circleImage Werte
		if (circleImage.cols() > 0) {
			Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE, "Kreis gr��er als 0 --> "
					+ circleImage.cols());

			// Schleife wird mit jedem Wert von circleImage (Vectoren)
			// durchgegangen.
			for (int x = 0; x < circleImage.cols(); x++) {

				double vCircle[] = circleImage.get(0, x);

				if (vCircle == null) {
					Log.i(android.content.Context.TEXT_SERVICES_MANAGER_SERVICE,
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
	
	public static void setThreshold(int thres) {
		threshold = thres;
	}
	
	public static void setminLineSize(int tmp) {
		minLineSize = tmp;
	}
	
	public static void setlineGap(int tmp) {
		lineGap = tmp;
	}
	
	public static void setH_min(int tmp) {
		h_min = tmp;
	}
	
	public static void setH_max(int tmp) {
		h_max = tmp;
	}
	
	public static void setS_min(int tmp) {
		s_min = tmp;
	}
	
	public static void setS_max(int tmp) {
		s_max = tmp;
	}
	
	public static void setV_min(int tmp) {
		v_min = tmp;
	}
	
	public static void setV_max(int tmp) {
		v_max = tmp;
	}
	public static void setMaxCorners(int tmp) {
		maxCorners = (int) (tmp/2.55);
	}
	public static void setQualityLevel(int tmp) {
		double tmp2 = tmp/1000;
		if (!(tmp2 == 0.0))
		qualityLevel = tmp2;
	}
	public static void setMinDistance(int tmp) {
		minDistance = tmp;
	}

	

	public static void matRelease() {
		mRgba.release();
		mGray.release();
		mIntermediateMat.release();
	}

	public static void matIni(int width, int height) {
		// TODO Auto-generated method stub
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
	}
	
}
