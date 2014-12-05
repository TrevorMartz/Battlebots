package controller;

import interfaces.TimerListener;

import java.util.ArrayList;
import java.util.List;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class GroundInteraction implements TimerListener
{

	Thread movementFacilitator;
	static NXTRegulatedMotor left;
	static NXTRegulatedMotor right;
	List<Instruction> path = new ArrayList<>();

	private enum Direction
	{
		RIGHT, LEFT, FORWARD, BACKWARD;

		public void move(int speed)
		{
			switch(this)
			{
				case BACKWARD:
					right.setSpeed(speed);
					left.setSpeed(speed);
					right.forward();
					left.forward();
					break;
				case FORWARD:
					right.setSpeed(speed);
					left.setSpeed(speed);
					right.backward();
					left.backward();
					break;
				case LEFT:
					right.setSpeed(speed);
					left.setSpeed(speed);
					left.backward();
					right.forward();
					break;
				case RIGHT:
					right.setSpeed(speed);
					left.setSpeed(speed);
					left.forward();
					right.backward();
					break;
				default:
					break;
				
			}
		}
	}

	private class Instruction
	{
		public Direction direction;
		long time;
		int speed;

		public Instruction(Direction d, long t, int speed)
		{
			this.direction = d;
			time = t;
			this.speed = speed;
		}
	}

	public GroundInteraction()
	{

		left = Motor.getInstance(1);
		right = Motor.getInstance(2);

		movementFacilitator = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while(!Thread.interrupted())
				{
					move();
				}//this might cause problems
			}
		});

	}

	public synchronized void stop()
	{
		if (movementFacilitator != null && movementFacilitator.isAlive())
		{
			movementFacilitator.interrupt();
			try
			{
//				RConsole.print("Waiting for movement thread to end.");
				movementFacilitator.join(1);
//				RConsole.println("Movement thread ended.");
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

//		RConsole.println("Path is null? " + (path == null));

		movementFacilitator = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				move();
			}
		});

		left.stop();
		right.stop();
		path.clear();
	}

	public void retreat()
	{
		stop();
		path.add(new Instruction(Direction.BACKWARD, 3000, (int) left.getMaxSpeed()));
		movementFacilitator.start();
	}
	
	public void push()
	{
		stop();
		path.add(new Instruction(Direction.FORWARD, 5000, (int) left.getMaxSpeed()));
	}
	
	public void search()
	{
		stop();
		path.add(new Instruction(Direction.LEFT, 1000, 1000));
		path.add(new Instruction(Direction.FORWARD, 100, 1000));
		movementFacilitator.start();
	}

	public void evadeLine()
	{
		stop();
		synchronized (movementFacilitator)
		{
			path.add(new Instruction(Direction.BACKWARD, 200, 1000000));
			movementFacilitator.start();
			try
			{
				movementFacilitator.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void moveForward()
	{
		stop();
		path.add(new Instruction(Direction.FORWARD, 3000, 800));
		movementFacilitator.start();
	}

	private void move()
	{
//		RConsole.println("Starting the movement thread");
		if (null != path)
		{

			for (int i = 0; i < path.size(); i++)
			{
				Instruction inst = path.get(i);
				inst.direction.move(inst.speed);
//				new Timer(this, (long) inst.time);
				long start = System.currentTimeMillis();
				while (System.currentTimeMillis() - start < inst.time)
				{
					//TODO Timer??
					try
					{
						Thread.sleep(1);
					}
					catch (InterruptedException e)
					{
					}
					if (Thread.interrupted())
					{
//						RConsole.println("Movement thread is stopping.");
						return;
					}
				}
			}
		}
	}

	@Override
	public void onTimerFinish()
	{
		// TODO Auto-generated method stub
		
	}

}
