package opencv_Test.processor;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public class DirectVideo extends ProcessVideo {
	public DirectVideo(FrameSource source, FrameReciever frame) {
		super(source, frame);		
	}

	@Override
	protected Mat workOnFrame(Mat frame) {		
		return frame;
	}
}
