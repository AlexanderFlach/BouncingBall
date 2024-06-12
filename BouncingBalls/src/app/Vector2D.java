package app;

public class Vector2D {
    public double x;
    public double y;

    Vector2D(double x, double y) {
       this.x = x;
       this.y = y;
    }

    public Vector2D cloneVector() {
        return new Vector2D(this.x, this.y);
    }

    public void set(double x, double y) {
       this.x = x;
       this.y = y;
    }

    public void addScaled(Vector2D v, double scale) {
        this.x += scale * v.x;
        this.y += scale * v.y;
    }

    public void add(Vector2D v) {
        this.x += v.x;
        this.y += v.y;
    }

    public void subtractVectors(Vector2D a, Vector2D b) {
        this.x = a.x - b.x;
        this.y = a.y - b.y;
    }

    void scale(double s) {
        this.x *= s;
        this.y *= s;
    }

    public Vector2D normalVector() {
        return new Vector2D(this.y, -this.x);
    }

    public Vector2D oppositeVector() {
        return new Vector2D(-this.x, -this.y);
    }

    public Vector2D normalizeVector() {
        return new Vector2D(this.x / this.length(), this.y / this.length());
    }

    public double length() {
        return Math.sqrt(this.dot(this));
    }

    public double dot(Vector2D v) {
        return this.x * v.x + this.y * v.y;
    }

}
