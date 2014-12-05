package controller;

import interfaces.DistanceListener;
import interfaces.LightListener;
import interfaces.TimerListener;
import interfaces.TouchListener;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import sensors.DistanceMonitor;
import sensors.LightMonitor;
import sensors.Timer;

public class Driver implements DistanceListener, LightListener, TouchListener, TimerListener
{

	public static void main(String[] args)
	{
		Driver d = new Driver();
		d.start();
	}

	public enum MotionMode
	{
		SEARCHING, EVADING_LINE, PUSHING, RETREATING, INTERCEPTING, STOPPED
	}

	private MotionMode currentState = MotionMode.SEARCHING;
	private GroundInteraction groundInteraction;
	private DistanceMonitor distanceMonitor;
	private LightMonitor lightMonitor;

	public Driver()
	{
		groundInteraction = new GroundInteraction();
		distanceMonitor = new DistanceMonitor(new UltrasonicSensor(SensorPort.S1), 40);
		lightMonitor = new LightMonitor(new LightSensor(SensorPort.S2));
		
		distanceMonitor.addListener(this);
		lightMonitor.addListener(this);
	}

	private void start()
	{
		groundInteraction.moveForward();
	}

	@Override
	public void contactInitiated()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void contactStopped()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void thresholdPassed()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void thresholdUnderpassed()
	{
		// TODO Auto-generated method stub
		if (currentState != MotionMode.EVADING_LINE)
		{
			currentState = MotionMode.EVADING_LINE;
			groundInteraction.evadeLine();
			groundInteraction.moveForward();
			new Timer(this, 500);
		}
	}

	@Override
	public void objectDetected()
	{
		// TODO Auto-generated method stub
		if (currentState != MotionMode.EVADING_LINE)
		{
			Sound.beepSequenceUp();
			currentState = MotionMode.PUSHING;
			groundInteraction.moveForward();
		}
	}

	@Override
	public void objectLost()
	{
		
		// TODO search again

	}

	@Override
	public void onTimerFinish()
	{
		currentState = MotionMode.SEARCHING;
	}
}
