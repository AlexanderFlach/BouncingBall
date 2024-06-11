package app;

import utils.ApplicationTime;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;

public class BallAnimation extends Animation {

    @Override
    protected ArrayList<JFrame> createFrames(ApplicationTime applicationTimeThread) {
        // a list of all frames (windows) that will be shown
        ArrayList<JFrame> frames = new ArrayList<>();

        // Create main frame (window)
        JFrame frame = new JFrame("Mathematics and Simulation: Workshop 04");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BallAnimationPanel panel = new BallAnimationPanel(applicationTimeThread);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.pack(); // adjusts size of the JFrame to fit the size of it's components
        frame.setVisible(true);

        frames.add(frame);
        return frames;
    }
}

class BallAnimationPanel extends JPanel {
    private final PhysicsScene physicsScene;
    private final ApplicationTime thread;
    private double simWidth;
    private double simHeight;
    private double lastFrameTime;

    public BallAnimationPanel(ApplicationTime thread) {
        physicsScene = new PhysicsScene();
        this.thread = thread;
    }

    public Dimension getPreferredSize() {
        return new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }

    // drawing
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        //System.out.println("paintComponent");
        updateSize();
        if (!physicsScene.isInitialized) {
            physicsScene.setup(this.simWidth, this.simHeight);
        }
        simulate();
        draw(g);
    }

    public void updateSize() {
        // set simulation size with respect to window size
        simWidth = this.getWidth();
        simHeight = this.getHeight();
        physicsScene.update(this.simWidth, this.simHeight);
    }

    void draw(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // iterate through balls
        for (int i = 0; i < physicsScene.balls.size(); i++) {
            Ball ball = physicsScene.balls.get(i);
            g.setColor(Color.RED);
            g.fillOval((int) ball.pos.x, (int) ball.pos.y, (int) ball.radius*2, (int) ball.radius*2);
        }
    }

    void simulate() {
        double dt;
        if (Constants.SIMULATE_WITH_RENDER_TIME) {
            double time = this.thread.getTimeInSeconds();
            dt = time - lastFrameTime;
            lastFrameTime = time;
        }
        else {
            dt = physicsScene.dt;
        }
        // iterate through balls
        for (int i = 0; i < physicsScene.balls.size(); i++) {
            Ball ball1 = physicsScene.balls.get(i);
            ball1.simulate(dt, physicsScene.gravity);
            for (int j = i + 1; j < physicsScene.balls.size(); j++) {
                var ball2 = physicsScene.balls.get(j);
                handleBallCollision(ball1, ball2, physicsScene.restitution);
            }
//            handleWallCollision(ball1, physicsScene.worldSize);

        }
    }

    void handleBallCollision(Ball ball1, Ball ball2, double restitution) {
        Vector2D distance = new Vector2D(0.0, 0.0);
        distance.subtractVectors(ball2.pos, ball1.pos);
        double d = distance.length();
        // no collision
        if(d == 0.0 || d > ball1.radius + ball2.radius) {
            return;
        }

        // collision response

    }

//    void handleWallCollision(Ball ball, Vector2D worldSize) {
//        if (ball.pos.x < ball.radius/2) {
//            ball.pos.x = ball.radius/2;
//            ball.vel.x = -ball.vel.x;
//            System.out.println(ball.pos.x + " Ball pos");
//            System.out.println(ball.radius + " Ball radius");
//        }
//        if (ball.pos.x > worldSize.x - ball.radius) {
//            ball.pos.x = worldSize.x - ball.radius;
//            ball.vel.x = -ball.vel.x;
//        }
//        if (ball.pos.y < ball.radius) {
//            ball.pos.y = ball.radius;
//            ball.vel.y = -ball.vel.y;
//        }
//
//        if (ball.pos.y > worldSize.y - ball.radius) {
//            ball.pos.y = worldSize.y - ball.radius;
//            ball.vel.y = -ball.vel.y;
//        }
//
//    }

}



