/*
 * Angle.java
 * Ario Barin Ostovary
 * Angle class in radians
 */

public class MyAngle {
    private double angle; // in radians

    public MyAngle(double angle) {
        this.angle = angle;
    }

    public double getRadians() {
        return angle;
    }

    public double getCos() {
        return Math.cos(angle);
    }

    public double getSin() {
        return Math.sin(angle);
    }

    private double normalize(double angle) {
        // normalize the angle to be between 0 and 2*pi
        return (angle + Math.PI * 2) % (Math.PI * 2);
    }

    public void rotate(double angle) {
        // normalize after adding the angle 
        this.angle = normalize(this.angle + angle);
    }

    public void rotate(MyAngle other) {
        rotate(other.getRadians());
    }

    public MyAngle copy() {
        return new MyAngle(angle);
    }

    @Override
    public String toString() {
        return String.format("Radians: %.2f", angle);
    }
}