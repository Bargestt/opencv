package opencv_Test.source;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.text.StyleConstants.ColorConstants;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.utils.Converters;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.sun.javafx.css.converters.ColorConverter;
import com.sun.prism.paint.Color;

import opencv_Test.display.FrameReciever;

import org.opencv.highgui.*;
import org.opencv.*;


public class CameraReader implements FrameSource{
	
	private ArrayList<FrameReciever> recievers;
	
	private VideoCapture capture;	
	private Mat curFrame;
	
	private int maxFPS;
	
	private int width = 1280;
	private int height = 720;	

	
	public CameraReader() {
		recievers = new ArrayList<FrameReciever>();
		capture = new VideoCapture();
		curFrame = new Mat();
	}	

	public boolean start() {
		capture.open(0);
		if(!capture.isOpened())
			return false;
		
		loadParams();

		return true;
	}
	
	private void loadParams() {
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH,width);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,height);
		
		maxFPS = (int) capture.get(Videoio.CAP_PROP_FPS);
		if(maxFPS == 0) maxFPS = 30;

		
		
		capture.read(curFrame);	
		System.out.println( "Capturing at:" + curFrame.cols() + "x" + curFrame.rows());
		System.out.println( "Max FPS: " + maxFPS);
		width = curFrame.cols();
		height = curFrame.rows();
	}
	


	public void stop() {
		capture.release();
	}

	public Mat get() {
		return curFrame;		
	}

	public void update() {
		capture.read(curFrame);	
		notifyObservers();
	}
	public void justUpdate() {
		capture.read(curFrame);	
	}

	public synchronized int getMaxFPS() {
		return maxFPS;
	}
	public synchronized int getWidth() {
		return width;
	}
	public synchronized int getHeight() {
		return height;
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
		recievers.forEach(reciever -> reciever.put(curFrame));	
	}


}
