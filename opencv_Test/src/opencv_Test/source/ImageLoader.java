package opencv_Test.source;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import opencv_Test.display.FrameReciever;

public class ImageLoader implements FrameSource {
	private ArrayList<FrameReciever> recievers;
	
	private Mat image;
	
	private int width = 0, height = 0;	

	public ImageLoader() {
		recievers = new ArrayList<FrameReciever>();
		image = new Mat();
	}
	public ImageLoader(int width, int height) {
		if(width != 0 && height != 0 ) {			
			this.width = Math.abs(width);
			this.height = Math.abs(height);
		}
		recievers = new ArrayList<FrameReciever>();
		image = new Mat();
	}
	
	public boolean loadImage(String path, boolean gray) {
		
		image = Imgcodecs.imread(path, (gray? Imgcodecs.IMREAD_GRAYSCALE : Imgcodecs.IMREAD_COLOR));
		if(image == null)
			return false;
		if(image.empty())
			return false;
		
		if(width != 0 && height != 0 )		
			Imgproc.resize( image, image, new Size(width, height),
									0, 0, Imgproc.INTER_LINEAR);		
		
		notifyObservers();		
		return true;
	}
	
	public void setSize(int width, int height) {
		if(width != 0 && height != 0 ) {			
			this.width = Math.abs(width);
			this.height = Math.abs(height);
		}
		Imgproc.resize( image, image, new Size(width, height),
				0, 0, Imgproc.INTER_LINEAR);
		
		notifyObservers();
	}
	
	@Override
	public Mat get() {		
		return image;
	}

	@Override
	public void addObserver(FrameReciever newObserver) {
		if(!recievers.contains(newObserver))
			recievers.add(newObserver);		
	}
	@Override
	public void removeObserver(FrameReciever observer) {
		recievers.remove(observer);		
	}
	@Override
	public void notifyObservers() {
		recievers.forEach(reciever -> reciever.put(image));	
	}

}
