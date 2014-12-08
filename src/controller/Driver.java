package controller;

import interfaces.DistanceListener;
import interfaces.LightListener;
import interfaces.TimerListener;
import interfaces.TouchListener;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import sensors.DistanceMonitor;
import sensors.LightMonitor;
import sensors.TouchMonitor;

public class Driver implements DistanceListener, LightListener, TouchListener, TimerListener
{

	public static void main(String[] args)
	{
//		RConsole.openBluetooth(0);
		Driver d = new Driver();
		d.start();
	}

	public enum MotionMode
	{
		SEARCHING, EVADING_LINE, PUSHING, RETREATING, EVADING_ATTACK
	}

	private MotionMode currentState = MotionMode.SEARCHING;
	private GroundInteraction groundInteraction;
	private DistanceMonitor distanceMonitor;
	private LightMonitor lightMonitor;
	private TouchMonitor[] touchMonitors = new TouchMonitor[2];

	public Driver()
	{
		groundInteraction = new GroundInteraction();
		distanceMonitor = new DistanceMonitor(new UltrasonicSensor(SensorPort.S1), 40);
		distanceMonitor.addListener(this);
		
		lightMonitor = new LightMonitor(new LightSensor(SensorPort.S2));
		lightMonitor.addListener(this);
		
		touchMonitors[0] = new TouchMonitor(new TouchSensor(SensorPort.S3));
		touchMonitors[0].addListener(this);
		
		touchMonitors[1] = new TouchMonitor(new TouchSensor(SensorPort.S4));
		touchMonitors[1].addListener(this);
		
	}

	private void start()
	{
		groundInteraction.moveForward();
	}

	@Override
	public void contactInitiated()
	{
		if(currentState == MotionMode.EVADING_LINE || currentState == MotionMode.RETREATING)
		{
			currentState = MotionMode.RETREATING;
			groundInteraction.retreat();
		}
		else if(currentState == MotionMode.SEARCHING || currentState == MotionMode.PUSHING)
		{
			currentState = MotionMode.EVADING_ATTACK;
			groundInteraction.evadeAttackFrombehind();
		}
		else
		{
			currentState = MotionMode.SEARCHING;
			groundInteraction.search();
		}

	}

	@Override
	public void contactStopped()
	{
//		if(currentState == MotionMode.RETREATING || currentState == MotionMode.EVADING_ATTACK)
//		{
			currentState = MotionMode.SEARCHING;
			groundInteraction.search();
//		}

	}

	@Override
	public void lineDetected()
	{
		currentState = MotionMode.EVADING_LINE;
		groundInteraction.evadeLine();
	}

	@Override
	public void lineLost()
	{
		if (currentState == MotionMode.EVADING_LINE)
		{
			currentState = MotionMode.SEARCHING;
			groundInteraction.search();
		}
	}

	@Override
	public void objectDetected()
	{
		if (currentState == MotionMode.EVADING_LINE)
		{
			currentState = MotionMode.RETREATING;
			groundInteraction.retreat();
		}
		else
		{
			currentState = MotionMode.PUSHING;
			groundInteraction.push();
		}
	}

	@Override
	public void objectLost()
	{
		currentState = MotionMode.SEARCHING;
		groundInteraction.search();
	}

	@Override
	public void onTimerFinish()
	{
//		currentState = MotionMode.SEARCHING;
	}
}
