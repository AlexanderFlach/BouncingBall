package app;

public class Constants {
	// visualization settings
	public static int WINDOW_WIDTH = 1000;
	public static int WINDOW_HEIGHT = 600;

	// animation and simulation settings
	public static int FPS = 60;
	public static int TPF = 1000/FPS;
	public static double TIMESCALE = 1.0;
	public static boolean SIMULATE_WITH_RENDER_TIME = true;
	public static double DT = 1.0 / 60.0; // controls the simulation speed
										// if not simulated at render speed
	public static final double RESTITUTION = 1.0;
	public static final Vector2D GRAVITY = new Vector2D(0.0, 0.0);
	public static final int NUM_BALLS = 1;

	// octagon
	public static final int margin = 30;

	// ball settings
	public static final double INITIAL_VELOCITY = 200.0;
	public static final double MIN_BALL_SIZE = 20;
	public static final double BALL_SIZE_VARIANCE = 30;
}
