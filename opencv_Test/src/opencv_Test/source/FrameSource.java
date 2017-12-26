package opencv_Test.source;

import org.opencv.core.Mat;

import opencv_Test.display.FrameReciever;

public interface FrameSource {
	public Mat get();

	
	public void addObserver(FrameReciever newObserver);
	public void removeObserver(FrameReciever observer);
	public void notifyObservers();
}
