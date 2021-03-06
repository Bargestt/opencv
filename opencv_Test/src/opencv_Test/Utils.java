package opencv_Test;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Utils {
	
	/**
	 * Plain conversion from Mat to Buffered image<br>
	 * if Mat.channels == 1 constructs GrayScale BufferedImage<br>
	 * no Improc used
	 */	
	public static BufferedImage matToBufferedImage(Mat src)
	{	
		
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
		
		return image;
	}
	
	public static BufferedImage matToBufferedImage(Mat src, int width, int height)
	{		
		Imgproc.resize(src, src, new Size(width, height),0, 0, Imgproc.INTER_LINEAR);
		byte[] sourcePixels = new byte[src.cols() * src.rows() * src.channels()];
		src.get(0, 0, sourcePixels);
		
		int type;
		if (src.channels() > 1) 
			type = BufferedImage.TYPE_3BYTE_BGR;		
		else 
			type = BufferedImage.TYPE_BYTE_GRAY;		
		int w = src.rows();
		BufferedImage image = new BufferedImage(src.cols(), src.rows(), type);
		
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);		
		
		return image;
	}

}
