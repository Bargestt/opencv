package opencv_Test.processor;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public class BGRemoval extends ProcessVideo {
	public BGRemoval(FrameSource source) {
		super(source);
	}
	public BGRemoval(FrameSource source, FrameReciever frame) {
		super(source, frame);
	}
	public BGRemoval(FrameSource source, ArrayList<FrameReciever> frames) {
		super(source, frames);
	}
	
	private boolean inverse = false;
	
	Mat hsvImg = new Mat();
	ArrayList<Mat> hsvPlanes = new ArrayList<Mat>();
	Mat mask = new Mat();
	
	@Override
	protected Mat workOnFrame(Mat frame) {		
		hsvImg.create(frame.size(), CvType.CV_8U);
		Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_BGR2HSV);

		Core.split(hsvImg, hsvPlanes);
		
		double average = getMean(hsvPlanes.subList(0, 1));	
		
		
		int mode = Imgproc.THRESH_BINARY;		
		if(!inverse) mode = Imgproc.THRESH_BINARY_INV;
		// ! - стоит т.к мы обнуляем всё по найденной маске
		
		Imgproc.threshold(
				hsvPlanes.get(0), 
				mask, 
				average, 
				179.0, 
				mode);
		Imgproc.blur(mask, mask, new Size(5, 5));
				
		
		Imgproc.dilate(mask, mask, new Mat(), new Point(-1, -1), 1);
		Imgproc.erode( mask, mask, new Mat(), new Point(-1, -1), 3);
		
		Imgproc.threshold(mask, mask, average, 179.0, Imgproc.THRESH_BINARY);
		
		//обнуляем всё по найденной маске
		frame.setTo(Scalar.all(0), mask);	
		
		return frame;
	}
	
	private double getMean(List<Mat> planes) {
		Mat hist_hue = new Mat();
		MatOfInt histSize = new MatOfInt(180);
		Imgproc.calcHist(
				planes, 
				new MatOfInt(0), 
				new Mat(), 
				hist_hue , 
				histSize, 
				new MatOfFloat(0, 179));
		
		double average = 0;
		for (int h = 0; h < 180; h++)
		    average += (hist_hue.get(h, 0)[0] * h);
		average = average / hsvImg.size().height / hsvImg.size().width;		
		
		hist_hue.release();
		histSize.release();
		
		return average;
	}

}
