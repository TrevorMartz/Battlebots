package controller;

import java.util.ArrayList;
import java.util.List;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.comm.RConsole;

public class GroundInteraction
{

	Thread movementFacilitator;
	NXTRegulatedMotor left;
	NXTRegulatedMotor right;
	List<Instruction> path = new ArrayList<>();

	private enum Direction
	{
		RIGHT, LEFT, FORWARD, BACKWARD
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

		// left.setSpeed(1000000);
		// right.setSpeed(1000000);

		movementFacilitator = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				move();
			}
		});

	}

	public void rotate()
	{
		left.forward();
		right.backward();
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

	public void startSearch()
	{
		stop();
		path.add(new Instruction(Direction.RIGHT, 200, 1000));
		movementFacilitator.start();
	}

	public void startRetreat()
	{
		stop();
		path.add(new Instruction(Direction.BACKWARD, 2000, 1000));
		movementFacilitator.start();
	}

	public void evadeLine()
	{
		stop();
		synchronized (movementFacilitator)
		{
			path.add(new Instruction(Direction.BACKWARD, 50, 1000000));
			path.add(new Instruction(Direction.RIGHT, 100, 1000000));
			path.add(new Instruction(Direction.FORWARD, 500, 1000000));
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

	public void evadePlan2()
	{
		stop();
		path.add(new Instruction(Direction.BACKWARD, 300, 100000));
		path.add(new Instruction(Direction.LEFT, 300, 1000000));
		movementFacilitator.start();
	}

	public void moveForward()
	{
		stop();
//		RConsole.println("\npath is null? " + (path == null));
		path.add(new Instruction(Direction.FORWARD, 3000, 800));
		movementFacilitator.start();
	}

	public void searchSweeping()
	{
		stop();
		path.add(new Instruction(Direction.RIGHT, 700, 500));
		path.add(new Instruction(Direction.LEFT, 900, 500));
		movementFacilitator.start();
	}

	public void reverseAndSearch()
	{
		stop();
		path.add(new Instruction(Direction.BACKWARD, 1000, 100000));
		path.add(new Instruction(Direction.RIGHT, 2000, 500));
		movementFacilitator.start();
	}

	private void move()
	{
		RConsole.println("Starting the movement thread");
		if (null != path)
		{

			for (int i = 0; i < path.size(); i++)
			{
				Instruction inst = path.get(i);
				switch (inst.direction)
				{
				case BACKWARD:
					right.setSpeed(inst.speed);
					left.setSpeed(inst.speed);
					right.forward();
					left.forward();
					break;
				case FORWARD:
					right.setSpeed(inst.speed);
					left.setSpeed(inst.speed);
					right.backward();
					left.backward();
					break;
				case LEFT:
					right.setSpeed(inst.speed);
					left.setSpeed(inst.speed);
					left.backward();
					right.forward();
					break;
				case RIGHT:
					right.setSpeed(inst.speed);
					left.setSpeed(inst.speed);
					left.forward();
					right.backward();
					break;
				default:
					break;
				}
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
						RConsole.println("Movement thread is stopping.");
						return;
					}
				}
			}
		}
	}

}
