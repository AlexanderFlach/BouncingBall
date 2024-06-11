package utils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.TimerTask;

public class FrameUpdater extends TimerTask
{
	private final ArrayList<JFrame> frames;
	
	/**
	 * Create a FrameUpdater with an ArrayList of Frames for better performance
	 * @param frames The frames to be refreshed
	 */
	public FrameUpdater(ArrayList<JFrame> frames) {
		this.frames = frames;
	}
	
	@Override
	public void run() {
		frames.forEach(Component::repaint);
	}
}
