package opencv_Test.processor;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public class AddImage extends ProcessVideo {
	
	private Mat image = null;
	private Rect roi = null;
	private Mat mask = null;
	
	private Point pos;
	private boolean mix = false;

	public AddImage(FrameSource source, FrameReciever frame) {
		super(source, frame);
		pos = new Point(0,0);
		roi = new Rect(0,0,0,0);
	}
	
	
	public boolean loadImage(String path) {
		
		image = Imgcodecs.imread(path,Imgcodecs.IMREAD_UNCHANGED);
		if(image == null)
			return false;
		if(image.empty()){
			image = null;
			return false;
		}
		
		if(image.channels() >= 3) {
			mask = new Mat();
			Core.extractChannel(image, mask, 3);
			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGRA2BGR);
		}
		else
			mask = image.clone();
		
		return true;
	}
	
	public void setPos(int x, int y) {
		if(x<0)x=0;
		if(y<0)y=0;
		pos.setLocation(x, y);
	}

	@Override
	protected Mat workOnFrame(Mat frame) {
		if(image == null) return frame;		
		
		int maxX = frame.cols()-image.cols();
		int maxY = frame.rows()-image.rows();
		
		if(pos.x > maxX) pos.x = maxX;
		if(pos.y > maxY) pos.y = maxY;
		
		roi.x = pos.x;
		roi.y = pos.y;
		roi.width = image.cols();
		roi.height = image.rows();
		
		
		if(frame.elemSize() == 1)
			Imgproc.cvtColor(frame, frame, Imgproc.COLOR_GRAY2BGR);
		Mat imageROI = frame.submat(roi);
		
		if(mix)
			Core.addWeighted(imageROI, 1.0, image, 1.0, 0.0, imageROI);	
		else
			image.copyTo(imageROI, mask);
		
		return frame;
	}

	
}
