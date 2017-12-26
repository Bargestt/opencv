package opencv_Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.prism.Graphics;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;


import opencv_Test.display.LabelFrame;
import opencv_Test.processor.AddImage;
import opencv_Test.processor.BGRemoval;
import opencv_Test.processor.ConvertVideo;
import opencv_Test.processor.DFT_ProcessorWIP;

import opencv_Test.processor.DirectVideo;
import opencv_Test.processor.EdgeDetect;
import opencv_Test.processor.HaarClassifier;
import opencv_Test.processor.ObjectDetect;
import opencv_Test.processor.ProcessHistogramm;
import opencv_Test.processor.ProcessVideo;
import opencv_Test.source.CameraReader;
import opencv_Test.source.ImageLoader;



public class Main {

	public static void main(String[] args) {		
		System.out.println("test");		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);		
		

		GUIProcessor p = new GUIProcessor();
		
		//Frame fr = new Frame();
	}
	
	
	
	
	public static class Frame2 extends JFrame{
		LabelFrame imgOriginal, imgAfter, imgAfterBoth;
		
		ImageLoader load;
		
		ProcessVideo forward, backward;
		
		public Frame2() {
			setSize(1200, 1200);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
			setResizable(false);			
			
			
			
			imgOriginal = new LabelFrame(500,500);
			imgAfter = new LabelFrame(500,500);
			imgAfterBoth = new LabelFrame(300,300);
			
			JPanel panel = new JPanel();
			panel.add(imgOriginal);
			panel.add(imgAfter);
			//panel.add(imgAfterBoth);
			this.add(panel);
			
			load = new ImageLoader(300,300);
			load.addObserver(imgOriginal);	
			
			forward = new BGRemoval(load, imgAfter);

			
			load.loadImage("res/original.png", false);
		}
	}
	
	
	public static class Frame extends JFrame
	{		
		ScheduledThreadPoolExecutor pool;	
		
		
		JPanel panel;

		JButton btnGray, btnLogo, btnColor;
		JSlider slider;
		
		JSlider hueMax, hueMin, satMax, satMin, valMax, valMin;
		ChangeListener sliderListen;
		JLabel hue, sat, val, title;

	
		BufferedImage image;
		Mat logo = new Mat();
		
		
		CameraReader camera;
		ObjectDetect process1, process2;
		LabelFrame videoOut, frame2, frame3;
		CamRecord recording;
		
		DFT_ProcessorWIP proc;
		
		public Frame() {
			initParams();
			initUI();  			
			
			
			camera = new CameraReader();			
			process1 = new ObjectDetect(camera, videoOut);			
			process1.setSampleReciever(frame2);
			process1.setMorphReciever(frame3);
			
			if(camera.start()){
				System.out.println("Capture started");
				recording = new CamRecord();
				pool.scheduleAtFixedRate(
						recording, 0, 1000/camera.getMaxFPS(), TimeUnit.MILLISECONDS);
				}			
		}	
		
		

       
        long time = 0;
        long avgTime = 0;
        long count = 0;
        
		void updateVideo() {
			time = System.currentTimeMillis();
			camera.justUpdate();
			
			camera.notifyObservers();			
			
			avgTime += System.currentTimeMillis() - time;
			count++;
			
			if(count > 10) {
				System.out.println((float)avgTime / count);
				count = 0;
				avgTime = 0;
			}			
			
			title.setText("H("+process1.getHueMin()+":"+process1.getHueMax()+
					") S("+process1.getSatMin()+":"+process1.getSatMax()+
					") V("+process1.getValMin()+":"+process1.getValMax()+")");
			
        	this.repaint();
		}

		
		BufferedImage buff;
		ImageIcon iconbuff = new ImageIcon();
		private void procImg() {
			Mat img = camera.get();
			BufferedImage buff = Utils.matToBufferedImage(
					img, videoOut.getWidth(), videoOut.getHeight());
			iconbuff.setImage(buff);
			
			videoOut.setIcon(iconbuff);
		}
		
		
		//======================================================================
		
		
		private class CamRecord implements Runnable {	
			public void run() {	
				updateVideo();
			}
		}	
		
		private class ButtonListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == btnGray) {					
				}
				if(e.getSource() == btnLogo) {
					process2.toggle();
				}		
				if(e.getSource() == btnColor) {
					
				}					
			}			
		}
		private void initParams() {
			setSize(1550, 500);			

			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
			setResizable(false);
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					camera.stop();					
					pool.shutdown();					
					
					System.out.println("Capture is released");					
					super.windowClosing(e);
				}				
			});	
			
			pool = new ScheduledThreadPoolExecutor(2);            
		}
		
		private void initUI() {				
			panel = new JPanel();			
			this.add(panel);
			
			videoOut = new LabelFrame();			 
			videoOut.setSizeFixed(16*50, 9*50);
	        panel.add(videoOut);
            
            title = new JLabel("Title");
            title.setFont(new Font("Arial", Font.BOLD, 18));
            ButtonListener btnListener = new ButtonListener();
            btnGray = new JButton("GrayScale");
            btnGray.addActionListener(btnListener);
            btnLogo = new JButton("Logo");
            btnLogo.addActionListener(btnListener);
            btnColor = new JButton("Alter Color");
            btnColor.addActionListener(btnListener);
            Box box = Box.createVerticalBox();
            box.add(title);
            box.add(btnGray);
            box.add(btnLogo);
            box.add(btnColor);
            panel.add(box);            
            
            logo = Imgcodecs.imread("res/logo.png");             
            Imgproc.cvtColor(logo, logo, Imgproc.COLOR_BGR2BGRA);             
            

            
            sliderListen = new SliderListen();
            hueMax = new JSlider(JSlider.HORIZONTAL, 0, 180, 180); 
            hueMin = new JSlider(JSlider.HORIZONTAL, 0, 180, 0); 
            satMax = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);  
            satMin = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);  
            valMax = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);  
            valMin = new JSlider(JSlider.HORIZONTAL, 0, 255, 0); 
            
            hueMax.addChangeListener(sliderListen);
            hueMin.addChangeListener(sliderListen);
            satMax.addChangeListener(sliderListen);
            satMin.addChangeListener(sliderListen);
            valMax.addChangeListener(sliderListen);
            valMin.addChangeListener(sliderListen);
            
            
            
            hue = new JLabel("HUE");
            box.add(hue);
            box.add(hueMax);
            box.add(hueMin); 
            JSeparator sep1 = new JSeparator();
            sep1.setPreferredSize(new Dimension(100, 20));
            box.add(sep1);
            sat = new JLabel("Saturatuon");
            box.add(sat);
            box.add(satMax);
            box.add(satMin);
            
            JSeparator sep2 = new JSeparator();
            sep2.setPreferredSize(new Dimension(100, 20));
            box.add(sep2);
            
            val = new JLabel("Value");
            box.add(val);
            box.add(valMax);
            box.add(valMin);
            
            
            
            int w= 16*20;
            int h  = 9*20;
            Box box2 = Box.createVerticalBox();
            box.setSize(w + 100, h*2 + 100);
            box.setMinimumSize(new Dimension(w + 100, h*2 + 100));
            box.setBackground(new Color(0, 0, 0));
            frame2 = new LabelFrame();
            frame2.setSizeFixed(w, h);            
            box2.add(frame2);
            
            frame3 = new LabelFrame();
            frame3.setSizeFixed(w, h);            
            box2.add(frame3);
            panel.add(box2);      
            
            //pack();

            setLocationRelativeTo(null);
		}
		
		class SliderListen implements ChangeListener{
			@Override
			public void stateChanged(ChangeEvent e) {
				if(e.getSource() == hueMax) {
					if(hueMax.getValue() < hueMin.getValue())
						hueMin.setValue(hueMax.getValue());
					hue.setText("Hue: " + hueMin.getValue() + ", " + hueMax.getValue());
					process1.setHueMax(hueMax.getValue());
				}
				if(e.getSource() == hueMin) {
					if(hueMax.getValue() < hueMin.getValue())
						hueMax.setValue(hueMin.getValue());
					hue.setText("Hue: " + hueMin.getValue() + ", " + hueMax.getValue());
					process1.setHueMin(hueMin.getValue());
				}
				
				
				if(e.getSource() == satMax) {
					if(satMax.getValue() < satMin.getValue())
						satMin.setValue(satMax.getValue());
					sat.setText("Sat: " + satMin.getValue() + ", " + satMax.getValue());
					process1.setSatMax(satMax.getValue());
				}
				if(e.getSource() == satMin) {
					if(satMax.getValue() < satMin.getValue())
						satMax.setValue(satMin.getValue());
					sat.setText("Sat: " + satMin.getValue() + ", " + satMax.getValue());
					process1.setSatMin(satMin.getValue());
				}
				
				if(e.getSource() == valMax) {
					if(valMax.getValue() < valMin.getValue())
						valMin.setValue(valMax.getValue());
					val.setText("Val: " + valMin.getValue() + ", " + valMax.getValue());
					process1.setValMax(valMax.getValue());
				}
				if(e.getSource() == valMin) {
					if(valMax.getValue() < valMin.getValue())
						valMax.setValue(valMin.getValue());
					val.setText("Val: " + valMin.getValue() + ", " + valMax.getValue());
					process1.setValMin(valMin.getValue());
				}
				
			}			
		}
	}
	
}





