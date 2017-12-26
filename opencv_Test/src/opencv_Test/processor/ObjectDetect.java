/**
 * 
 */
package opencv_Test.processor;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public class ObjectDetect extends ProcessVideo {

	public ObjectDetect(FrameSource source) {
		super(source);
	}
	public ObjectDetect(FrameSource source, FrameReciever frame) {
		super(source, frame);
	}
	public ObjectDetect(FrameSource source, ArrayList<FrameReciever> frames) {
		super(source, frames);
	}
	
	private Mat hsvImage = new Mat();
	private Mat mask = new Mat();
	private Mat morphOutput = new Mat();
	

	private int hueMax = 180;
	private int hueMin = 0;
	
	private int satMax = 255;
	private int satMin = 0;
	
	private int valMax = 255;
	private int valMin = 0;	
	
	
	private Mat HSV_sample = new Mat();
	private Rect sampleRect = new Rect(0,0, 100, 100);
	private boolean sampleInput = true;
	
	
	private Scalar contourColor = new Scalar(255,255,255);
	private int contourThickness = 3;
	
	
	private FrameReciever maskReciever = null;
	private FrameReciever morphReciever = null;
	private FrameReciever sampleReciever = null;
	
	@Override
	protected Mat workOnFrame(Mat frame) {
		removeNoise(frame);	
		
		if(sampleInput)
			sampleFrom(hsvImage, sampleRect);
		
		Scalar minValues = new Scalar(hueMin, satMin, valMin);
		Scalar maxValues = new Scalar(hueMax, satMax, valMax);
		
		Core.inRange(hsvImage, minValues, maxValues, mask);
		
		
		Mat dilateElement = Imgproc.getStructuringElement(
				 Imgproc.MORPH_RECT, new Size(24, 24));
		Mat erodeElement = Imgproc.getStructuringElement(
				 Imgproc.MORPH_RECT, new Size(12, 12));
		
		Imgproc.erode(mask, morphOutput, erodeElement);
		Imgproc.erode(mask, morphOutput, erodeElement);
		
		Imgproc.dilate(mask, morphOutput, dilateElement);
		Imgproc.dilate(mask, morphOutput, dilateElement);
		 
		 
		drawContours(morphOutput, frame);
		return frame;
	}
	
	private void removeNoise(Mat frame) {
		Imgproc.blur(frame, frame, new Size(7, 7));
		Imgproc.cvtColor(frame, hsvImage, Imgproc.COLOR_BGR2HSV);
	}
	
	private void drawContours(Mat maskedImage, Mat frame) {
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		 
		 // find contours
		Imgproc.findContours(
				 maskedImage, contours, hierarchy, 
				 Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		 // if any contour exist...
		if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
		{
			for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
			{
				Imgproc.drawContours(
						frame, contours, idx, contourColor, contourThickness);
		    }
		}
		hierarchy.release();

	}
	
	public void sampleFrom(Mat HSVframe, Rect rect) {		
		HSVframe.submat(rect).copyTo(HSV_sample);			
		
		int d = 50;
		setHueMax((int) getMean(HSV_sample, 0) + d);
		setHueMin((int) getMean(HSV_sample, 0) - d);
		
		setSatMax((int) getMean(HSV_sample, 1) + d);
		setSatMin((int) getMean(HSV_sample, 1) - d);
		
		setValMax((int) getMean(HSV_sample, 2) + d);
		setValMin((int) getMean(HSV_sample, 2) - d);
	}
	public void sampleFrom(Mat HSVframe) {		
		HSVframe.copyTo(HSV_sample);			
		
		int d = 50;
		setHueMax((int) getMean(HSV_sample, 0) + d);
		setHueMin((int) getMean(HSV_sample, 0) - d);
		
		setSatMax((int) getMean(HSV_sample, 1) + d);
		setSatMin((int) getMean(HSV_sample, 1) - d);
		
		setValMax((int) getMean(HSV_sample, 2) + d);
		setValMin((int) getMean(HSV_sample, 2) - d);
	}
	
	
	
	
	
	public int getHueMax() {
		return hueMax;
	}
	public int getHueMin() {
		return hueMin;
	}
	public int getSatMax() {
		return satMax;
	}
	public int getSatMin() {
		return satMin;
	}
	public int getValMax() {
		return valMax;
	}
	public int getValMin() {
		return valMin;
	}
	
	
	public void setHueMax(int hueMax) {
		if(hueMax < 0)hueMax = 0;
		if(hueMax > 255) hueMax = 255;
		
		this.hueMax = hueMax;
		if(this.hueMax < this.hueMin)
			this.hueMax = this.hueMin;
	}
	public void setHueMin(int hueMin) {
		if(hueMin < 0)hueMin = 0;
		if(hueMin > 255) hueMin = 255;
		
		this.hueMin = hueMin;
		if(this.hueMin > this.hueMax)
			this.hueMin = this.hueMax;
	}
	public void setSatMax(int satMax) {
		if(satMax < 0)satMax = 0;
		if(satMax > 255) satMax = 255;
		
		this.satMax = satMax;
		if(this.satMax < this.satMin)
			this.satMax = this.satMin;
	}
	public void setSatMin(int satMin) {
		if(satMin < 0)satMin = 0;
		if(satMin > 255) satMin = 255;
		
		this.satMin = satMin;
		if(this.satMin > this.satMax)
			this.satMin = this.satMax;
	}
	public void setValMax(int valMax) {
		if(valMax < 0)valMax = 0;
		if(valMax > 255) valMax = 255;
		
		this.valMax = valMax;
		if(this.valMax < this.valMin)
			this.valMax = this.valMin;
	}
	public void setValMin(int valMin) {
		if(valMin < 0)valMin = 0;
		if(valMin > 255) valMin = 255;
		
		this.valMin = valMin;
		if(this.valMin > this.valMax)
			this.valMin = this.valMax;
	}

	
	public void setMaskReciever(FrameReciever reciever) {
		maskReciever = reciever;
	}
	public void setMorphReciever(FrameReciever reciever) {
		morphReciever = reciever;
	}
	public void setSampleReciever(FrameReciever reciever) {
		sampleReciever = reciever;
	}
	
	
	
	public Rect getSampleRect() {
		return sampleRect;
	}
	public void setSampleRect(Rect sampleRect) {
		this.sampleRect = sampleRect;
	}
	public boolean isSampleInput() {
		return sampleInput;
	}
	public void setSampleInput(boolean sampleInput) {
		this.sampleInput = sampleInput;
	}
	public Scalar getContourColor() {
		return contourColor;
	}
	public void setContourColor(int r, int g, int b) {
		r = Math.abs(r);
		g = Math.abs(g);
		b = Math.abs(b);
		
		if(r > 255)r= 255;
		if(g > 255)g= 255;
		if(b > 255)b= 255;
		
		this.contourColor = new Scalar(b,g,r);
	}
	public int getContourThickness() {
		return contourThickness;
	}
	public void setContourThickness(int contourThickness) {
		this.contourThickness = contourThickness;
	}
	
	
	@Override
	public void notifyObservers() {
		if(maskReciever != null)
			maskReciever.put(mask);
		if(morphReciever != null)
			morphReciever.put(morphOutput);
		if(sampleInput && sampleReciever != null)
			sampleReciever.put(HSV_sample);
		
		super.notifyObservers();
	}
	
	
	
	private double getMean(Mat hsvImg, int channel) {
		List<Mat> planes = new ArrayList<Mat>();
		Core.split(hsvImg, planes);
		
		Mat hist_hue = new Mat();
		int range = (channel == 0 ? 180 : 255);
		MatOfInt histSize = new MatOfInt(range);
		Imgproc.calcHist(
				planes.subList(channel, channel+1), 
				new MatOfInt(0), 
				new Mat(), 
				hist_hue , 
				histSize, 
				new MatOfFloat(0, range-1));
		
		double average = 0;
		for (int h = 0; h < range; h++)
		    average += (hist_hue.get(h, 0)[0] * h);
		average = average / hsvImg.size().height / hsvImg.size().width;		
		
		hist_hue.release();
		histSize.release();
		
		return average;
	}

}
