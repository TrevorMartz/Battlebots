package controller;

import interfaces.DistanceListener;
import interfaces.LightListener;
import interfaces.TimerListener;
import interfaces.TouchListener;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import sensors.DistanceMonitor;
import sensors.LightMonitor;
import sensors.Timer;
import sensors.TouchMonitor;

public class Driver implements DistanceListener, LightListener, TouchListener, TimerListener{
	
	public static void main(String[] args) {
		Driver d = new Driver();
		d.start();
	}
	
	public enum MotionMode{
		SEARCHING, EVADING_LINE, PUSHING, RETREATING, INTERCEPTING, STOPPED
	}
	
	private MotionMode currentState = MotionMode.SEARCHING;
	private GroundInteraction gi;
	private DistanceMonitor dist;
	private LightMonitor light;
	private TouchMonitor touch;
	private NXTRegulatedMotor whacker;
	
	public Driver() {
		gi = new GroundInteraction();
		dist = new DistanceMonitor(new UltrasonicSensor(SensorPort.S2), 40);
		light = new LightMonitor(new LightSensor(SensorPort.S3));
		touch = new TouchMonitor(new TouchSensor(SensorPort.S4));
		whacker = Motor.getInstance(0);
		
		dist.addListener(this);
		light.addListener(this);
		touch.addListener(this);
	}

	private void start() {
		gi.moveForward();
	}

	@Override
	public void contactInitiated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contactStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void thresholdPassed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void thresholdUnderpassed() {
		// TODO Auto-generated method stub
		if(currentState != MotionMode.EVADING_LINE){
			currentState = MotionMode.EVADING_LINE;
			whacker.stop();
			gi.evadeLine();
			gi.moveForward();
			Timer t = new Timer(this, 500);
		}
	}

	@Override
	public void objectDetected() {
		// TODO Auto-generated method stub
		if(currentState != MotionMode.EVADING_LINE){
			Sound.beepSequenceUp();
			currentState = MotionMode.PUSHING;
			gi.moveForward();
			whacker.setSpeed(10000000);
			whacker.forward();
		}
	}

	@Override
	public void objectLost() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTimerFinish(){
		currentState = MotionMode.SEARCHING;
	}

}
