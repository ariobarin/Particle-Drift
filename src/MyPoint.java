/*  
 * Point.java
 * Ario Barin Ostovary
 * Class for a point in 2D space
 */

public class MyPoint {
    private double x;
    private double y;

    public MyPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void move(double distance, MyAngle angle) {
        // move the point by the given distance and angle - component-wise
        x += distance * angle.getCos();
        y += distance * angle.getSin();
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public double distance(MyPoint p) {
        return distance(p.getX(), p.getY());
    }

    public double distance(MyDirectedPoint p) {
        return distance(p.getX(), p.getY());
    }

    public double distance(double x, double y) {
        // calculate the distance between two points
        return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
    }

    public MyPoint copy() {
        // copy the point deep
        return new MyPoint(x, y);
    }

    @Override
    public String toString() {
        return String.format("(X: %.2f, Y: %.2f)", x, y);
    }
}