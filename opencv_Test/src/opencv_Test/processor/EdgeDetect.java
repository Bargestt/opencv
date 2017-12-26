package opencv_Test.processor;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public class EdgeDetect extends ProcessVideo {
	public EdgeDetect(FrameSource source) {
		super(source);		
	}

	public EdgeDetect(FrameSource source, FrameReciever frame) {
		super(source, frame);		
	}

	public EdgeDetect(FrameSource source, ArrayList<FrameReciever> frames) {
		super(source, frames);
	}
	
	private int lowThreshold = 0;
	private Mat grayFrame = new Mat();

	Mat dest = new Mat();
	@Override
	protected Mat workOnFrame(Mat frame) {
		Mat detectedEdges = new Mat();
		
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);		
		Imgproc.blur(grayFrame, detectedEdges, new Size(3, 3));
		
		Imgproc.Canny(
				detectedEdges, detectedEdges, 
				lowThreshold, 
				lowThreshold * 3, 
				3, 
				false);		
		
		dest = Mat.zeros(dest.size(), dest.type());
		frame.copyTo(dest, detectedEdges);
		
		return dest;
	}
	
	public int getLowThreshold() {
		return lowThreshold;
	}

	public void setLowThreshold(int lowThreshold) {
		this.lowThreshold = lowThreshold;
	}
}
