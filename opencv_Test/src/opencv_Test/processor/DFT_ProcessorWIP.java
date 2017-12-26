package opencv_Test.processor;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public class DFT_ProcessorWIP extends ProcessVideo {	
	private ArrayList<Mat> planes = new ArrayList<Mat>();
	private ArrayList<Mat> newPlanes = new ArrayList<Mat>();
	
	private Mat complexImage = new Mat();
	private Mat mag = new Mat();
	private Mat image = new Mat();	

	private boolean calcMagnitude = false;
	private boolean quadShift = false;
	
	public DFT_ProcessorWIP(FrameSource source) {
		super(source);		
	}
	public DFT_ProcessorWIP(FrameSource source,ArrayList<FrameReciever> frames) {
		super(source, frames);		
	}
	public DFT_ProcessorWIP(FrameSource source, FrameReciever frame) {
		super(source, frame);		
	}
	
	
	@Override
	protected Mat workOnFrame(Mat frame) {		
		if(getSource().getClass() != DFT_ProcessorWIP.class) {
			addBorder(frame, frame);
			toComplexCompat(frame, complexImage);	
		}	
		else
			complexImage = frame;
		
		Core.dft(complexImage, complexImage);
		
		Core.split(complexImage, this.newPlanes);		
		if(calcMagnitude) 
			magnitude(complexImage, image);		
		else
			convertToImage(newPlanes.get(0), image);
		
		if(quadShift) doQuadShift(image);
		
		planes.clear();
		newPlanes.clear();
		return image;
	}	

	
	
	protected void magnitude(Mat complexResult_2ch, Mat magnitude) {		
		Core.split(complexResult_2ch, newPlanes);
		Core.magnitude(newPlanes.get(0), newPlanes.get(1), magnitude);

		Core.add(magnitude, new Scalar(1), magnitude);
		Core.log(magnitude, magnitude);	
		
		convertToImage(magnitude, magnitude);		
	}	
	protected void convertToImage(Mat src, Mat result) {
		Core.normalize(src, result, 0, 255, Core.NORM_MINMAX);
		result.convertTo(result, CvType.CV_8UC1);		
	}

	
	protected void addBorder(Mat frame, Mat dest) {
		int addPixelRows = Core.getOptimalDFTSize(frame.rows());
		int addPixelCols = Core.getOptimalDFTSize(frame.cols());
		
		Core.copyMakeBorder(frame, dest, 
				0, 
				addPixelRows - frame.rows(), 
				0, 
				addPixelCols - frame.cols(),
				Core.BORDER_CONSTANT, Scalar.all(0));			
	}
	
	protected void toComplexCompat(Mat frame, Mat dest) {
		frame.convertTo(frame, CvType.CV_32F);
		planes.add(frame);
		planes.add(Mat.zeros(frame.size(), CvType.CV_32F));		
		Core.merge(this.planes, dest);
	}
	
	protected void doQuadShift(Mat image) {	
		//побитовое для чётности
		image = image.submat( 
				new Rect( 0, 0,	image.cols() & -2, image.rows() & -2));
		int cx = image.cols() / 2;
		int cy = image.rows() / 2;		
		
		//ацкий бред, но так хотябы не плодим Ректы
		// ряд_начало -> ряд_конец, стобл_начало -> столб_конец  - тот ещё берд
		Mat q0 = image.submat(0, cy, 0, cx);
		Mat q1 = image.submat(0, cy, cx, image.cols());
		Mat q2 = image.submat(cy, image.rows(), 0, cx);
		Mat q3 = image.submat(cy, image.rows(), cx, image.cols());

		Mat tmp = new Mat();
		//swap q0 q3
		q0.copyTo(tmp);
		q3.copyTo(q0);
		tmp.copyTo(q3);
		//swap q1 q2
		q1.copyTo(tmp);
		q2.copyTo(q1);
		tmp.copyTo(q2);
		tmp = null;			
	}

	
	public void notifyObservers() {
		getRecievers().forEach(reciever -> {
			if(reciever.getClass() == DFT_ProcessorWIP.class)
				reciever.put(complexImage);
			else
				reciever.put(get());
		});
	}
	
	public boolean isCalcMagnitude() {
		return calcMagnitude;
	}
	public void setCalcMagnitude(boolean calcMagnitude) {
		this.calcMagnitude = calcMagnitude;
	}
	public boolean isQuadShift() {
		return quadShift;
	}
	public void setQuadShift(boolean quadShift) {
		this.quadShift = quadShift;
	};
	
}