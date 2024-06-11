package app;

import java.util.ArrayList;

public class PhysicsScene {
    public Vector2D gravity;
    public double dt;
    public Vector2D worldSize;
    public ArrayList<Ball> balls;
    public double restitution;
    int numBalls;
    boolean isInitialized;


    PhysicsScene() {
        this.gravity = Constants.GRAVITY;
        this.dt = Constants.DT;
        this.restitution = Constants.RESTITUTION;

        this.worldSize = new Vector2D(0.0, 0.0); // is set later and
        // depends on frame (window) size

        this.balls = new ArrayList<>();
        this.numBalls = Constants.NUM_BALLS;
        this.isInitialized = false; // flag to avoid multiple initializations
    }

    void setup(double simWidth, double simHeight) {
        if (this.balls.isEmpty() ) {
            this.balls = new ArrayList<>();
            double initialVelocity = Constants.INITIAL_VELOCITY;
            double minBallSize = Constants.MIN_BALL_SIZE;
            double ballSizeVariance = Constants.BALL_SIZE_VARIANCE;
            for (int i = 0; i < this.numBalls; i++) {
                double radius = minBallSize + Math.random() * ballSizeVariance;
                double mass = Math.PI * radius * radius;
//                Vector2D pos = new Vector2D(Math.random() * simWidth, Math.random() * simHeight);
                Vector2D pos = new Vector2D(0, 0);
//                Vector2D vel = new Vector2D(initialVelocity * Math.random(), initialVelocity * Math.random());
                Vector2D vel = new Vector2D(0, 0);
                System.out.println("Ball #" + i + " created at position " + pos.x + "," + pos.y + " with velocity " + vel.x + "," + vel.y);
                this.balls.add(new Ball(radius, mass, pos, vel, Constants.RESTITUTION));
            }
        }
        this.isInitialized = true;
    }
    void update(double simWidth, double simHeight) {
        this.worldSize.set(simWidth, simHeight);
    }
}
