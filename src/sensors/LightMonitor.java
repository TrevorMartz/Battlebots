package sensors;

import interfaces.LightListener;

import java.util.ArrayList;
import java.util.List;

import lejos.nxt.LightSensor;
import lejos.nxt.comm.RConsole;

public class LightMonitor
{

	LightSensor light;
	List<LightListener> listeners = new ArrayList<>();
	private int threshold;
	Thread monitor;

	public LightMonitor(LightSensor light)
	{
		this.light = light;
		// light.calibrateLow();
		this.threshold = 45;
		monitor = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				monitorLineDetector();
			}
		});

		monitor.start();
	}

	public void stop()
	{
		monitor.interrupt();
	}

	public void addListener(LightListener l)
	{
		listeners.add(l);
	}

	public void calibrate()
	{
		light.calibrateLow();
	}

	public void monitorLineDetector()
	{
		boolean isDetected = false;
		while (!Thread.interrupted())
		{
//			RConsole.print(light.getLightValue() + "\n");
			if (Math.abs(light.getLightValue()) > threshold && !isDetected)
			{
				for (LightListener listener : listeners)
				{
					listener.thresholdPassed();
				}
				isDetected = true;
			}
			if (Math.abs(light.getLightValue()) < threshold && isDetected)
			{
				for (LightListener listener : listeners)
				{
					listener.thresholdUnderpassed();
				}
				isDetected = false;
			}
			Thread.yield();
		}
	}

}
