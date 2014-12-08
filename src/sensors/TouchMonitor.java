package sensors;

import interfaces.TouchListener;

import java.util.ArrayList;
import java.util.List;

import lejos.robotics.Touch;

public class TouchMonitor
{

	private List<TouchListener> listeners = new ArrayList<>();
	private Touch touch;
	private Thread thread;

	public TouchMonitor(Touch touch)
	{

		this.touch = touch;

		thread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				monitorTouchInput();
			}

		});

		thread.start();
	}

	public void addListener(TouchListener l)
	{
		listeners.add(l);
	}

	private void monitorTouchInput()
	{
		boolean isPressed = touch.isPressed();
		while (!Thread.interrupted())
		{
			if (touch.isPressed() && !isPressed)
			{
				isPressed = true;
				for (TouchListener l : listeners)
				{
					l.contactInitiated();
				}
			}
			else if (!touch.isPressed() && isPressed)
			{
				isPressed = false;
				for (TouchListener l : listeners)
				{
					l.contactStopped();
				}
			}
			Thread.yield();
		}
	}

	public void stop()
	{
		thread.interrupt();
	}

}
