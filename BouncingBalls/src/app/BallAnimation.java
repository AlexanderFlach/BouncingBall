package app;

import utils.ApplicationTime;

import javax.swing.*;
import java.awt.*;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;

public class BallAnimation extends Animation {

    @Override
    protected ArrayList<JFrame> createFrames(ApplicationTime applicationTimeThread) {
        ArrayList<JFrame> frames = new ArrayList<>();

        JFrame frame = new JFrame("Mathematics and Simulation: Workshop 04");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BallAnimationPanel panel = new BallAnimationPanel(applicationTimeThread);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        frames.add(frame);

        JFrame frame2 = new JFrame("Values");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AnimationValuesPanel panel2 = new AnimationValuesPanel(applicationTimeThread, panel.physicsScene);
        frame2.add(panel2);
        frame2.setLocation(800, 0);
        frame2.pack();
        frame2.setVisible(true);


        frames.add(frame2);


        return frames;
    }
}

class BallAnimationPanel extends JPanel {
    public final PhysicsScene physicsScene;
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
        updateSize();
        if (!physicsScene.isInitialized) {
            physicsScene.setup(this.simWidth, this.simHeight);
        }
        simulate();
        draw(g);
    }

    public void updateSize() {
        // updates window size
        simWidth = this.getWidth();
        simHeight = this.getHeight();
        physicsScene.update(this.simWidth, this.simHeight);
    }

    void draw(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // draw octagon with corner points
        g.setColor(Color.DARK_GRAY);
        g.drawPolygon(physicsScene.polygonX, physicsScene.polygonY, 8);
        g.fillPolygon(physicsScene.polygonX, physicsScene.polygonY, 8);

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
                handleBallCollision(ball1, ball2);
            }
            handleWallCollision(ball1);
        }
    }

    void handleBallCollision(Ball ball1, Ball ball2) {
        Vector2D distance = new Vector2D(0.0, 0.0);
        // middle points of balls
        Vector2D vec1 = new Vector2D(ball1.radius + ball1.pos.x, ball1.radius + ball1.pos.y);
        Vector2D vec2 = new Vector2D(ball2.radius + ball2.pos.x, ball2.radius + ball2.pos.y);
        // vector between middle points of balls
        distance.subtractVectors(vec2, vec1);
        double d = distance.length();
        // no collision
        if(d == 0.0 || d > ball1.radius + ball2.radius) {
            return;
        }

        // collision response

        // normals
        double nx = distance.x / d;
        double ny = distance.y / d;
        // Relativgeschwindigkeit
        double dvx = ball2.vel.x - ball1.vel.x;
        double dvy = ball2.vel.y - ball1.vel.y;

        // Relativgeschwindigkeit in Richtung der Normalen
        double vn = dvx * nx + dvy * ny;

        // Impulsänderung
        double impulse = 2 * vn / (ball1.mass + ball2.mass);

        // new velocity
        ball1.vel.x += impulse * ball2.mass * nx;
        ball1.vel.y += impulse * ball2.mass * ny;
        ball2.vel.x -= impulse * ball1.mass * nx;
        ball2.vel.y -= impulse * ball1.mass * ny;

        // set ball to correct position
        double overlap = 0.5 * (ball1.radius + ball2.radius - d);
        ball1.pos.x -= overlap * nx;
        ball1.pos.y -= overlap * ny;
        ball2.pos.x += overlap * nx;
        ball2.pos.y += overlap * ny;
    }

// Stoß mit Energieverlust
//    void handleBallCollision(Ball ball1, Ball ball2) {
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

    public boolean collidesWith(Ball ball, Line2D line) {
        // check collision of ball and line
        return Line2D.ptSegDist(line.getX1(), line.getY1(), line.getX2(), line.getY2(), ball.pos.x + ball.radius, ball.pos.y + ball.radius) <= ball.radius;
    }

    void handleWallCollision(Ball ball) {
        Line2D line = new Line2D.Double();
        // create vector array to store corner points of octagon
        Point[] octagon = new Point[8];
        for(int i = 0; i < octagon.length; i++) {
            octagon[i] = new Point(physicsScene.polygonX[i], physicsScene.polygonY[i]);
        }
        // check collision
        for(int i = 0; i < physicsScene.polygonX.length; i++) {
            if(i == physicsScene.polygonX.length - 1) {
                line.setLine(octagon[i], octagon[0]);
            }
            else {
                line.setLine(octagon[i], octagon[i + 1]);
            }
            if(collidesWith(ball, line)) {
                // collision response
                // create vector of wall segment and its normal vector
                Vector2D v = new Vector2D(line.getX2() - line.getX1(), line.getY2() - line.getY1());
                Vector2D nv = v.normalVector();
                nv = nv.normalizeVector();
                //normal vector facing inwards
                nv = nv.oppositeVector();
                // mirror velocity
                double dotProduct = ball.vel.dot(nv);
                ball.vel = new Vector2D(ball.vel.x - 2 * dotProduct * nv.x,
                        ball.vel.y - 2 * dotProduct * nv.y);
                // push ball into boundaries until it is inside
                boolean outsideBorder = true;
                while(outsideBorder) {
                    if(!collidesWith(ball, line)) {
                        outsideBorder = false;
                    }
                    ball.pos.add(nv);
                }
            }
        }
    }

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

class AnimationValuesPanel extends JPanel{
    private final PhysicsScene physicsScene;
    private final ApplicationTime thread;


    public AnimationValuesPanel(ApplicationTime thread, PhysicsScene physicsScene) {
        this.physicsScene = physicsScene;
        this.thread = thread;
    }

    public Dimension getPreferredSize() {
        return new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
    }

    // drawing
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        String X;
        String Y;
        String Z;
        int i = 0;

        for (Ball ball: physicsScene.balls){
            X = "x: " + ball.pos.x + ball.radius + " y: " + ball.pos.y + ball.radius;
            g.drawString(X, 50, 50 + i * 20);
            i++;
        }




    }
}



