package opencv_Test.display;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import opencv_Test.Utils;

public class LabelFrame extends JLabel implements FrameReciever{
	
	private boolean keepAspectRatio = false;
	
	private Mat curFrame = new Mat();
	
	public LabelFrame() {
		setSizeFixed(1, 1);	
	}	
	public LabelFrame(int width, int height) {
		setSizeFixed(width, height);
		setImage(Mat.zeros(new Size(width, height), CvType.CV_8UC1));
	}	
	
	public void setSizeFixed(int width, int height) {	
		Dimension d = new Dimension(width, height);
		super.setSize(d);
		super.setMinimumSize(d);
		super.setPreferredSize(d);		
	}
	
	private void setImageKeepAspect(BufferedImage bImg) {
		int w, h;						
		if(getWidth() > getHeight()) {
			w = bImg.getWidth() * this.getHeight() / bImg.getHeight();
			h = this.getHeight();
		}
		else {
			w = this.getWidth();
			h = bImg.getHeight() * this.getWidth() / bImg.getWidth();
		}		
		setIcon( new ImageIcon(bImg.getScaledInstance(w, h,Image.SCALE_FAST)) );
	}	
	
	/**
	 * <b> Slover rescale than Mat </b> 
	 * @param bImg
	 */
	public void setImage(BufferedImage bImg) {
		//if(!keepAspectRatio) {
			setIcon(new ImageIcon(bImg.getScaledInstance(						
									this.getWidth(), 
									this.getHeight(), 
									Image.SCALE_FAST))
									);
//		}
//		else {
//			setImageKeepAspect(bImg);
//		}
	}	
	/**
	 * <b> Faster rescale </b> 
	 * @param bImg
	 */
	public void setImage(Mat img) {
		setIcon(new ImageIcon(Utils.matToBufferedImage(
							img, this.getWidth(), this.getHeight() )));
	}

	public boolean isKeepAspectRatio() {
		return keepAspectRatio;
	}

	public void setKeepAspectRatio(boolean keepAspectRatio) {
		this.keepAspectRatio = keepAspectRatio;
	}

	@Override
	public void put(Mat frame) {
		frame.copyTo(curFrame);
		this.setImage(curFrame);		
	}
	
	
	
	
}
