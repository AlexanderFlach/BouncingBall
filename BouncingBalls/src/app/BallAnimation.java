package app;

import utils.ApplicationTime;

import javax.swing.*;
import java.awt.*;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class BallAnimation extends Animation {

    @Override
    protected ArrayList<JFrame> createFrames(ApplicationTime applicationTimeThread) {
        ArrayList<JFrame> frames = new ArrayList<>();

        JFrame frame = new JFrame("Ball Collision Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BallAnimationPanel panel = new BallAnimationPanel(applicationTimeThread);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        frames.add(frame);

        JFrame frame2 = new JFrame("Coordinates of Balls");
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AnimationCoordinatesPanel panel2 = new AnimationCoordinatesPanel(applicationTimeThread, panel.physicsScene);
        frame2.add(panel2);
        frame2.setLocation(1100, 0);
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
                handleBallCollision(ball1, ball2, Constants.RESTITUTION);
            }
            handleWallCollision(ball1);
        }
    }


    void handleBallCollision(Ball ball1, Ball ball2, double restitution) {
        // vector between middle points of balls
        Vector2D dir = new Vector2D(0.0, 0.0);
        // middle points of balls
        Vector2D vec1 = new Vector2D(ball1.radius + ball1.pos.x, ball1.radius + ball1.pos.y);
        Vector2D vec2 = new Vector2D(ball2.radius + ball2.pos.x, ball2.radius + ball2.pos.y);

        dir.subtractVectors(vec2, vec1);
        double d = dir.length();
        if (d == 0.0 || d > ball1.radius + ball2.radius) {
            // no collision
            return;
        }

        // collision response
        dir.scale(1.0 / d);

        double corr = (ball1.radius + ball2.radius - d) / 2.0;
        ball1.pos.addScaled(dir, -corr);
        ball2.pos.addScaled(dir, corr);

        double v1 = ball1.vel.dot(dir);
        double v2 = ball2.vel.dot(dir);

        double m1 = ball1.mass;
        double m2 = ball2.mass;

        double newV1 = (m1 * v1 + m2 * v2 - m2 * (v1 - v2) * restitution)
                / (m1 + m2);
        double newV2 = (m1 * v1 + m2 * v2 - m1 * (v2 - v1) * restitution)
                / (m1 + m2);

        ball1.vel.addScaled(dir, newV1 - v1);
        ball2.vel.addScaled(dir, newV2 - v2);
    }


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
}

class AnimationCoordinatesPanel extends JPanel{
    private final PhysicsScene physicsScene;
    private final ApplicationTime thread;


    public AnimationCoordinatesPanel(ApplicationTime thread, PhysicsScene physicsScene) {
        this.physicsScene = physicsScene;
        this.thread = thread;
    }

    public Dimension getPreferredSize() {
        return new Dimension(Constants.WINDOW_WIDTH -750, Constants.WINDOW_HEIGHT);
    }

    // drawing
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        String X;
        String Y;
        String Z;
        int i = 0;

        g.setFont(new Font("Serif", Font.PLAIN, 20));
        for (Ball ball: physicsScene.balls){
            X = "x: " + Math.round(ball.pos.x + ball.radius) + " y: " + Math.round(ball.pos.y + ball.radius);
            g.drawString(X, 50, 50 + i * 20);
            i++;
        }
    }
}



