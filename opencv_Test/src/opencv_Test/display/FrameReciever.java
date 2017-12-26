package opencv_Test.display;

import org.opencv.core.Mat;

public interface FrameReciever {	
	public void put(Mat frame);
}
