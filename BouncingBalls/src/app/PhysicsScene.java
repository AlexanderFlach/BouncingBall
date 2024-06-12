package app;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class PhysicsScene {
    public Vector2D gravity;
    public double dt;
    public Vector2D worldSize;
    public ArrayList<Ball> balls;
    public int[] polygonX;
    public int[] polygonY;
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
        this.polygonX = new int[8];
        this.polygonY = new int[8];
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
                Random random = new Random();
                Vector2D pos = new Vector2D(random.nextInt((int) (simWidth - radius*2)), random.nextInt((int) (simHeight - radius*2)));
                Vector2D vel = new Vector2D(initialVelocity * Math.random(), initialVelocity * Math.random());

                System.out.println("Ball #" + i + " created at position " + pos.x + "," + pos.y + " with velocity " + vel.x + "," + vel.y);
                this.balls.add(new Ball(radius, mass, pos, vel, Constants.RESTITUTION));
            }
        }
        if(this.polygonX[0] == 0) {
            this.polygonX = new int[8];
            this.polygonY = new int[8];
            // start with top left
            polygonX[0] = Constants.margin * 4;
            polygonY[0] = Constants.margin;

            polygonX[1] = (int) (simWidth - Constants.margin * 4);
            polygonY[1] = Constants.margin;

            polygonX[2] = (int) (simWidth - Constants.margin);
            polygonY[2] = Constants.margin * 4;

            polygonX[3] = (int) (simWidth - Constants.margin);
            polygonY[3] = (int) (simHeight - Constants.margin * 4);

            polygonX[4] = (int) (simWidth - Constants.margin * 4);
            polygonY[4] = (int) (simHeight - Constants.margin);

            polygonX[5] = Constants.margin * 4;
            polygonY[5] = (int) (simHeight - Constants.margin);

            polygonX[6] = Constants.margin;
            polygonY[6] = (int) (simHeight - Constants.margin * 4);

            polygonX[7] = Constants.margin;
            polygonY[7] = Constants.margin * 4;

            System.out.println("Octagon created.");
        }
        this.isInitialized = true;
    }
    void update(double simWidth, double simHeight) {
        this.worldSize.set(simWidth, simHeight);

        // start with top left
        polygonX[0] = Constants.margin * 4;
        polygonY[0] = Constants.margin;

        polygonX[1] = (int) (simWidth - Constants.margin * 4);
        polygonY[1] = Constants.margin;

        polygonX[2] = (int) (simWidth - Constants.margin);
        polygonY[2] = Constants.margin * 4;

        polygonX[3] = (int) (simWidth - Constants.margin);
        polygonY[3] = (int) (simHeight - Constants.margin * 4);

        polygonX[4] = (int) (simWidth - Constants.margin * 4);
        polygonY[4] = (int) (simHeight - Constants.margin);

        polygonX[5] = Constants.margin * 4;
        polygonY[5] = (int) (simHeight - Constants.margin);

        polygonX[6] = Constants.margin;
        polygonY[6] = (int) (simHeight - Constants.margin * 4);

        polygonX[7] = Constants.margin;
        polygonY[7] = Constants.margin * 4;
    }
}
