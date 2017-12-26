package opencv_Test.processor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import opencv_Test.Utils;
import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public class ProcessHistogramm extends ProcessVideo{
	
	private int width = 1280;
	private int height = 720;
	
	
	public ProcessHistogramm(FrameSource source) {
		super(source);	
	}
	public ProcessHistogramm(FrameSource source, FrameReciever frame) {
		super(source, frame);		
	}


	@Override
	protected Mat workOnFrame(Mat frame) {
		if(frame.elemSize() == 1)
			return calcHist(frame, true);
		
		return calcHist(frame, false);		
	}


	private Mat calcHist(Mat frame, boolean gray) {
		ArrayList<Mat> images = new ArrayList<Mat>();
		Core.split(frame, images);
		
		MatOfInt histSize = new MatOfInt(256);			
		MatOfInt channels = new MatOfInt(0);			
		MatOfFloat histRange = new MatOfFloat(0, 256);

		int bin_w = (int) Math.round(width / histSize.get(0, 0)[0]);
		
		//bg
		Mat histImage = new Mat( 
				height, 
				width, 
				CvType.CV_8UC3, 
				new Scalar(0, 0, 0));
		
		//matrices for components
		Mat hist_b = new Mat();
		Mat hist_g = new Mat();
		Mat hist_r = new Mat();
		
		Imgproc.calcHist( images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);			
		if (!gray)
		{
			Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);
			Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);
		}
		
		
		Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		if (!gray){
		   Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		   Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
		}
		double x1, x2, y1, y2;
		for (int i = 1; i < histSize.get(0, 0)[0]; i++){
			x1 = bin_w * (i - 1);
			x2 = bin_w * (i);
			
		    Imgproc.line(
				    histImage, 
				    new Point(x1, height - getHistY(hist_b, i - 1) ), 
				    new Point(x2, height - getHistY(hist_b, i) ), 
				    new Scalar(0, 0, 255), 
				    1, 8, 0);
		    if (!gray){			    	
		        Imgproc.line(
		    		    histImage,
		    		    new Point(x1, height - getHistY(hist_g, i - 1)),
		    		    new Point(x2, height - getHistY(hist_g, i)), 
		    		    new Scalar(0, 255, 0), 
		    		    1, 8, 0);
		        Imgproc.line(
		    		    histImage, 
		    		    new Point(x1, height - getHistY(hist_r, i - 1)),
		    		    new Point(x2, height - getHistY(hist_r, i)), 
		    		    new Scalar(255, 0, 0), 
		    		    1, 8, 0);
		    }
		}				
		return histImage;
	}
	
	private static double getHistY(Mat hist, int i) {
		return Math.round(hist.get(i, 0)[0]);
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
