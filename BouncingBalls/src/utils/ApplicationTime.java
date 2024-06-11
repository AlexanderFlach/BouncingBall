package utils;

import app.Constants;

import java.util.concurrent.atomic.AtomicBoolean;

public class ApplicationTime extends Thread {
	//time in ms
	public double timeSinceStart = 0;
	public long currentTime = 0;
	public long formerTime = 0;
	private final double timeScale = Constants.TIMESCALE;
	private final AtomicBoolean isPaused = new AtomicBoolean(false);
	private final AtomicBoolean running = new AtomicBoolean(true);
	
	public ApplicationTime() {
	}
	
	@Override
	public void run() {
			
		formerTime = System.currentTimeMillis();
		while(running.get()) {
			currentTime = System.currentTimeMillis();			
			if(!isPaused.get()) {
				timeSinceStart += (currentTime - formerTime) * timeScale;
			}			
			formerTime = currentTime;
		}
	}

	public double getTimeInSeconds() {
		return timeSinceStart / 1000;
	}
}
