package opencv_Test.processor;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public class HaarClassifier extends ProcessVideo {
	public HaarClassifier(FrameSource source, String classifierPath) {
		super(source);
		
		faceCascade = new CascadeClassifier();
		valid = faceCascade.load(classifierPath);
		if(!valid)
			System.err.println("Cascale load error: " + classifierPath);
		grayFrame = new Mat();
	}

	private boolean valid = false;
	
	private Mat grayFrame;
	private int absoluteFaceSize;
	private CascadeClassifier faceCascade;

	@Override
	protected Mat workOnFrame(Mat frame) {
		if(!valid) {
			System.err.println("Cascale classifier not loaded");
			return frame;
		}
		
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(grayFrame, grayFrame);		

		
		if (this.absoluteFaceSize == 0)
		{
		    int height = grayFrame.rows();
		    if (Math.round(height * 0.2f) > 0) 
		    {
		    	absoluteFaceSize = Math.round(height * 0.2f);
		    }
		}
		


		MatOfRect faces = new MatOfRect();		
		faceCascade.detectMultiScale( 
									grayFrame, 
									faces, 
									1.1, 
									2, 
									0 | Objdetect.CASCADE_SCALE_IMAGE, 
									new Size(absoluteFaceSize, absoluteFaceSize), 
									new Size());
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++) 
		{
		    Imgproc.rectangle( 
		    		frame, 
		    		facesArray[i].tl(), 
		    		facesArray[i].br(), 
		    		new Scalar(0, 255, 0, 255), 
		    		3);
		}
	
		
		return frame;
	}

}
