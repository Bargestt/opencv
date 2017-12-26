package opencv_Test.processor;

import java.util.ArrayList;
import java.util.Collection;
import org.opencv.core.Mat;

import opencv_Test.display.FrameReciever;
import opencv_Test.source.FrameSource;

public abstract class ProcessVideo implements FrameSource, FrameReciever {
	
	private FrameSource source;
	private ArrayList<FrameReciever> recievers;
	
	private Mat curFrame;
	
	private boolean active = true;	
	
	public ProcessVideo() {
		this.curFrame = new Mat();
		recievers = new ArrayList<FrameReciever>();
	}
	
	public ProcessVideo(FrameSource source) {
		if(source == null) throw new NullPointerException("Source is null");
		this.curFrame = new Mat();
		this.source = source;
		this.source.addObserver(this);
		recievers = new ArrayList<FrameReciever>();
		
	}
	
	public ProcessVideo(FrameSource source, FrameReciever frame)
	{		
		if(source == null) throw new NullPointerException("Source is null");
		if(frame == null) throw new NullPointerException("Frame is null");
		this.curFrame = new Mat();
		this.source = source;
		this.source.addObserver(this);
		recievers = new ArrayList<FrameReciever>();
		recievers.add(frame);
		
	}
	
	public ProcessVideo(FrameSource source, ArrayList<FrameReciever> frames)
	{
		if(source == null) throw new NullPointerException("Source is null");
		if(frames == null) throw new NullPointerException("Frame is null");
		if(frames.contains(null))throw new NullPointerException("Frame is null");
		this.curFrame = new Mat();
		this.source = source;
		this.source.addObserver(this);
		recievers = new ArrayList<FrameReciever>(frames);			
	}	

	public final void put(Mat frame) {
		frame.copyTo(curFrame);
		if(active)
			curFrame = workOnFrame(curFrame);	
		
		if(curFrame != null)
			notifyObservers();
		
	}
	
	protected abstract Mat workOnFrame(Mat frame);
	
	@Override
	public final Mat get() {
		return curFrame;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	public void deactivate() {
		this.active = false;
	}
	public void activate() {
		this.active = true;
	}
	public void toggle() {
		this.active = !active;
	}

	public FrameSource getSource() {
		return source;
	}
	public void setSource(FrameSource source) {
		this.source = source;
	}
	public ArrayList<FrameReciever> getRecievers() {
		return recievers;
	}
	public void setRecievers(ArrayList<FrameReciever> recievers) {
		this.recievers = recievers;
	}
	
	
}
