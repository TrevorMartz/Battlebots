package sensors;

import interfaces.DistanceListener;

import java.util.ArrayList;
import java.util.List;

import lejos.robotics.RangeFinder;

public class DistanceMonitor {
	
	private List<DistanceListener> listeners = new ArrayList<>();
	private Thread thread;
	private RangeFinder range;
	private int threshold;
	
	public DistanceMonitor(RangeFinder range, int threshold){
		this.range = range;
		this.threshold = threshold;
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				monitorDistanceInput();
			}
		});
		thread.start();
	}
	
	public void addListener(DistanceListener l){
		listeners.add(l);
	}

	private void monitorDistanceInput(){
    	boolean isDetected = range.getRange() < threshold;
    	while(!Thread.interrupted()){
    		if(range.getRange() <= threshold && !isDetected){
    			for(DistanceListener l : listeners){
    				l.objectDetected();
    			}
    			isDetected = true;
    		} else if (range.getRange() > threshold && isDetected){
    			for(DistanceListener l : listeners){
    				l.objectLost();
    			}
    			isDetected = false;
    		}
    		Thread.yield();
    	}
    }
	
	public void stop(){
		thread.interrupt();
	}

}
