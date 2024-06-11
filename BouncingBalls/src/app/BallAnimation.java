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
            handleWallCollision(ball1, physicsScene.worldSize);

        }
    }

    void handleBallCollision(Ball ball1, Ball ball2, double restitution) {
        Vector2D distance = new Vector2D(0.0, 0.0);
        // middle points of balls
        Vector2D vec1 = new Vector2D(ball1.radius + ball1.pos.x, ball1.radius + ball1.pos.y);
        Vector2D vec2 = new Vector2D(ball2.radius + ball2.pos.x, ball2.radius + ball2.pos.y);
        // vector between middle points of balls
        distance.subtractVectors(vec2, vec1);
        double d = distance.length();
        // no collision
        if(d == 0.0 || d > ball1.radius + ball2.radius) {
            System.out.println("no Collision");
            return;
        }

        System.out.println("Collision!!!");
        // collision response
        // Normale des Stoßes
        double nx = distance.x / d;
        double ny = distance.y / d;
        // Relativgeschwindigkeit
        double dvx = ball2.vel.x - ball1.vel.x;
        double dvy = ball2.vel.y - ball1.vel.y;

        // Relativgeschwindigkeit in Richtung der Normalen
        double vn = dvx * nx + dvy * ny;

        // Keine Kollision, wenn die Scheiben sich voneinander entfernen
        if (vn > 0) {
            return;
        }

        // Impulsänderung
        double impulse = 2 * vn / (ball1.mass + ball2.mass);

        // Neue Geschwindigkeiten
        ball1.vel.x += impulse * ball2.mass * nx;
        ball1.vel.y += impulse * ball2.mass * ny;
        ball2.vel.x -= impulse * ball1.mass * nx;
        ball2.vel.y -= impulse * ball1.mass * ny;

        // Zur Vermeidung des Überschneidens, korrigieren wir die Positionen
        double overlap = 0.5 * (ball1.radius + ball2.radius - d);
        ball1.pos.x -= overlap * nx;
        ball1.pos.y -= overlap * ny;
        ball2.pos.x += overlap * nx;
        ball2.pos.y += overlap * ny;
    }

// Stoß mit Energieverlust
//    void handleBallCollision(Ball ball1, Ball ball2, double restitution) {
//        Vector2D distance = new Vector2D(0.0, 0.0);
//        distance.subtractVectors(ball2.pos, ball1.pos);
//        double d = distance.length();
//        // no collision
//        if(d == 0.0 || d > ball1.radius + ball2.radius) {
//            System.out.println("no Collision");
//            return;
//        }
//        System.out.println("Collision!!!");
//        // collision response
//        // Normale des Stoßes
//        double nx = distance.x / d;
//        double ny = distance.y / d;
//        // Geschwindigkeit in Richtung der Normalen projizieren
//        double v1n = ball1.vel.x * nx + ball1.vel.y * ny;
//        double v2n = ball2.vel.x * nx + ball2.vel.y * ny;
//
//        // Geschwindigkeit in tangentialer Richtung projizieren (bleibt unverändert)
//        double v1t = -ball1.vel.x * ny + ball1.vel.y * nx;
//        double v2t = -ball2.vel.x * ny + ball2.vel.y * nx;
//
//        // Neue Geschwindigkeiten in Normalrichtung nach dem Stoß
//        double v1nNew = (v1n * (ball1.mass - ball2.mass) + 2 * ball2.mass * v2n) / (ball1.mass + ball2.mass);
//        double v2nNew = (v2n * (ball2.mass - ball1.mass) + 2 * ball1.mass * v1n) / (ball1.mass + ball2.mass);
//
//        // Zurückprojektion auf x- und y-Koordinaten
//        ball1.vel.x = v1nNew * nx - v1t * ny;
//        ball1.vel.y = v1nNew * ny + v1t * nx;
//        ball2.vel.x = v2nNew * nx - v2t * ny;
//        ball2.vel.y = v2nNew * ny + v2t * nx;
//    }

    void handleWallCollision(Ball ball, Vector2D worldSize) {
        if (ball.pos.x <= 0) { // left
            ball.pos.x = 0.0;
            ball.vel.x = -ball.vel.x;
        }
        if (ball.pos.x + ball.radius*2 > worldSize.x) { // right
            ball.pos.x = worldSize.x - ball.radius*2;
            ball.vel.x = -ball.vel.x;
        }
        if (ball.pos.y <= 0) { // top
            ball.pos.y = 0.0;
            ball.vel.y = -ball.vel.y;
        }

        if (ball.pos.y + ball.radius*2 > worldSize.y) { // bottom
            ball.pos.y = worldSize.y - ball.radius*2;
            ball.vel.y = -ball.vel.y;
        }

    }

}



