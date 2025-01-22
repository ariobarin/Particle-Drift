/*
 * DirectedPoint.java
 * Ario Barin Ostovary
 * Class for a point with an angle
 */

public class MyDirectedPoint {
    private final MyPoint point;
    private final MyAngle angle;

    public MyDirectedPoint(double x, double y, double angle) {
        this.point = new MyPoint(x, y);
        this.angle = new MyAngle(angle);
    }

    public MyDirectedPoint(MyPoint point, double angle) {
        this.point = point;
        this.angle = new MyAngle(angle);
    }

    public MyDirectedPoint(double x, double y, MyAngle angle) {
        this.point = new MyPoint(x, y);
        this.angle = angle;
    }

    public MyDirectedPoint(MyPoint point, MyAngle angle) {
        this.point = point;
        this.angle = angle;
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public MyPoint getPoint() {
        return point.copy();
    }

    public MyAngle getAngle() {
        return angle.copy();
    }

    public double getRadians() {
        return angle.getRadians();
    }

    public void move(double distance) {
        point.move(distance, angle);
    }

    public void move(double dx, double dy) {
        point.move(dx, dy);
    }

    public void move(double distance, MyAngle angle) {
        point.move(distance, angle);
    }

    public void rotate(double degrees) {
        angle.rotate(degrees);
    }

    public void rotate(MyAngle angle) {
        this.angle.rotate(angle);
    }

    public double distance(MyDirectedPoint other) {
        return point.distance(other.getPoint());
    }

    public MyDirectedPoint copy() {
        return new MyDirectedPoint(point.copy(), angle.copy());
    }

    @Override
    public int hashCode() {
        return (int) (point.getX() + point.getY() + angle.getRadians());
    }

    @Override
    public String toString() {
        return String.format("(Point: %.2f, %.2f, Angle: %.2f)", point.getX(), point.getY(), angle.getRadians());
    }
}