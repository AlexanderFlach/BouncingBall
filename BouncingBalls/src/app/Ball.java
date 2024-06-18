package app;

import java.awt.*;

public class Ball {
    double radius;
    double mass;
    double restitution;
    Vector2D pos;
    Vector2D vel;
    Color color;

    Ball(double radius, double mass, Vector2D pos, Vector2D vel, double restitution, Color color) {
        this.radius = radius;
        this.mass = mass;
        this.restitution = restitution; // elasticity factor
        this.pos = pos.cloneVector();
        this.vel = vel.cloneVector();
        this.color = color;
    }

    void simulate(double dt, Vector2D gravity) {
        this.vel.addScaled(gravity, dt);
        this.pos.addScaled(this.vel, dt);
    }
}
