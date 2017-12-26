package opencv_Test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageObject {
	
	private Mat mat;
	
	private boolean printable = true;
	

	public ImageObject() {
		mat = new Mat();		
	}

	
	
	public boolean isPrintable() {
		return printable;
	}
	public void setPrintable(boolean printable) {
		this.printable = printable;
	}



	public BufferedImage toImage() {
		//Imgproc.resize(src, src, new Size(width, height),0, 0, Imgproc.INTER_LINEAR);
		if(mat.type() != CvType.CV_8UC1)
			mat.convertTo(mat, CvType.CV_8UC1);		

		
		byte[] sourcePixels = new byte[mat.cols() * mat.rows() * mat.channels()];
		mat.get(0, 0, sourcePixels);
		
		int type;
		if (mat.channels() > 1) 
			type = BufferedImage.TYPE_3BYTE_BGR;		
		else 
			type = BufferedImage.TYPE_BYTE_GRAY;		

		BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
		
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);		
		
		return image;
	}
	
	public BufferedImage toImage(int width, int height) {
		Mat src = mat.clone();
		
		Imgproc.resize(src, src, new Size(width, height),0, 0, Imgproc.INTER_LINEAR);
		byte[] sourcePixels = new byte[src.cols() * src.rows() * src.channels()];
		src.get(0, 0, sourcePixels);
		
		int type;
		if (src.channels() > 1) 
			type = BufferedImage.TYPE_3BYTE_BGR;		
		else 
			type = BufferedImage.TYPE_BYTE_GRAY;		

		BufferedImage image = new BufferedImage(src.cols(), src.rows(), type);
		
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);		
		
		src.release();
		src = null;
		
		return image;		
	}
}
