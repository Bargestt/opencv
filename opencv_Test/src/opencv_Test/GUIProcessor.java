package opencv_Test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Mat;

import opencv_Test.display.LabelFrame;
import opencv_Test.processor.ConvertVideo;
import opencv_Test.processor.ProcessVideo;

public class GUIProcessor extends JFrame {
	
	ProcessVideo processor;
	
	LabelFrame inputFrame, outFrame;	
	JCheckBox showInput, showOutput;
	CheckBoxListener chkListen;
	
	Box boxLayout;
	JPanel panel;

	public GUIProcessor() {
		this.processor = new ConvertVideo();		
		setTitle(processor.getClass().getSimpleName());

		panel = new JPanel();
		boxLayout = Box.createVerticalBox();
		panel.add(boxLayout);
		this.add(panel);
		
		inputFrame = new LabelFrame(300,300);
		inputFrame.setVisible(true);
		outFrame = new LabelFrame(300,300);
		outFrame.setVisible(true);
		
		chkListen = new CheckBoxListener();
		showInput = new JCheckBox("Show Input Stream");
		showInput.addActionListener(chkListen);
		showInput.setSelected(true);
		showOutput = new JCheckBox("Show Output Stream");
		showOutput.addActionListener(chkListen);
		showOutput.setSelected(true);
		
		boxLayout.add(showInput);
		boxLayout.add(inputFrame);
		boxLayout.add(showOutput);
		boxLayout.add(outFrame);
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);		
	}
	
	private class CheckBoxListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {			
			if(e.getSource() == showInput) {
				inputFrame.setVisible(!inputFrame.isVisible());
			}
			if(e.getSource() == showOutput) {
				outFrame.setVisible(!outFrame.isVisible());
			}			
		}		
	}
	
	//=end
}
