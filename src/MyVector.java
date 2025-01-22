/*
 * Vector.java
 * Ario Barin Ostovary
 * Class for a vector - direction and magnitude
 */

public class MyVector {
    private MyAngle direction;
    private double magnitude;

    public MyVector(MyAngle direction, double magnitude) {
        this.direction = direction;
        this.magnitude = magnitude;
    }

    public MyVector(double direction, double magnitude) {
        this.direction = new MyAngle(direction);
        this.magnitude = magnitude;
    }

    public MyAngle getDirection() {
        return direction;
    }

    public double getDirectionRadians() {
        return direction.getRadians();
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setDirection(MyAngle direction) {
        this.direction = direction;
    }

    public void setDirection(double direction) {
        this.direction = new MyAngle(direction);
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getX() {
        return direction.getCos() * magnitude;
    }

    public double getY() {
        return direction.getSin() * magnitude;
    }

    public MyVector add(MyVector other) {
        // add the vectors - component-wise
        double x = direction.getCos() * magnitude + other.getDirection().getCos() * other.getMagnitude();
        double y = direction.getSin() * magnitude + other.getDirection().getSin() * other.getMagnitude();
        return new MyVector(new MyAngle(Math.atan2(y, x)), Math.sqrt(x * x + y * y));
    }

    public MyVector subtract(MyVector other) {
        // subtract the vectors - component-wise (add the negative of the other vector)
        return add(other.scaled(-1));
    }

    public MyVector scaled(double scalar) {
        // scale the vector by a scalar
        return new MyVector(direction, magnitude * scalar);
    }

    public MyVector normalized() {
        // normalize the vector - divide by the magnitude
        return scaled(1 / magnitude);
    }

    @Override
    public String toString() {
        return String.format("Vector(mag=%.2f, dir=%.2f)", getMagnitude(), getDirection().getRadians());
    }
}