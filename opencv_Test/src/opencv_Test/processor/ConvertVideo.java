package opencv_Test.processor;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public class ConvertVideo extends ProcessVideo{
	
	private int colorConversion = Imgproc.COLOR_BGRA2BGR;

	public ConvertVideo() {		
	}
	public ConvertVideo(FrameSource source, FrameReciever frame) {
		super(source, frame);		
	}
	public ConvertVideo(FrameSource source) {
		super(source);		
	}

	@Override
	protected Mat workOnFrame(Mat frame) {		
		Imgproc.cvtColor(frame, frame, colorConversion);
		return frame;
	}

	public int getColorConversion() {
		return colorConversion;
	}

	public void setColorConversion(int colorConversion) {
		this.colorConversion = colorConversion;
	}
}
