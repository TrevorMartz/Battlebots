package sensors;
import interfaces.TimerListener;

import java.util.ArrayList;
import java.util.List;

public class Timer{
	
	List<TimerListener> listeners;
	long startTime;
	long duration;

    public Timer(List<TimerListener> l, long duration){
        // store l and duration
    	listeners = l;
    	this.duration = duration;
    }
    
    public Timer(TimerListener l, long duration){
        // store l and duration
    	listeners = new ArrayList<>();
    	listeners.add(l);
    }

    void start(){
    	// store start time
        // start a new thread and call checkTime
    	startTime = System.currentTimeMillis();
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				checkTime();
			}
		}).start();
    }

    private void checkTime(){
//        while current time minus start time is less than duration
//            wait(1)
//        call all listeners
    	while (System.currentTimeMillis() - startTime < duration){
    		try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
    	}
    	for(TimerListener l : listeners){
    		l.onTimerFinish();
    	}
    }
}
