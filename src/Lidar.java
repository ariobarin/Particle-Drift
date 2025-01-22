/*
 * Lidar.java
 * Kevin Dang
 * Class for the lidar, can read the distance to the nearest object in a certain direction
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Lidar {
    private final Simulation simulation;
    private final MyAngle bearing;

    // tick = 1.8 degrees
    private static final double TICK = Math.PI / 180 * 1.8;

    private static final double TICK_PER_SCAN = 3;
    private static final int SCANS_PER_FRAME = 67;

    public static final double MIN_DISTANCE = 1;
    public static final double MAX_DISTANCE = 250;

    private final double lidarRadius = 10;

    public static final double READING_NOISE = 1.0;

    public Lidar(Simulation simulation) {
        this.simulation = simulation;
        bearing = new MyAngle(0);
    }

    public MyAngle getBearing() {
        return bearing;
    }

    public void update() {
        bearing.rotate(TICK * TICK_PER_SCAN);
    }

    public MyVector randomizeReading(double reading) {
        return new MyVector(bearing.copy(), reading + Util.randomGaussian(READING_NOISE));
    }

    public MyVector read(MyDirectedPoint lidarPosition) {
        MyDirectedPoint beam = lidarPosition.copy();
        beam.rotate(bearing); // Make the beam face in the direction of the lidar
        beam.move(MAX_DISTANCE);

        MyPoint start = lidarPosition.getPoint();
        MyPoint end = beam.getPoint();

        List<MyPoint> points = RayCaster.getCellsAlongRay(start, end);

        for (MyPoint point : points) {
            if (!simulation.isAir((int) point.getX(), (int) point.getY())) {
                return randomizeReading(point.distance(lidarPosition));
            }
        }
        return new MyVector(bearing.copy(), MAX_DISTANCE);
    }

    public List<MyVector> scan(MyDirectedPoint lidarPosition) {
        List<MyVector> readings = new ArrayList<>();
        for (int i = 0; i < SCANS_PER_FRAME; i++) {
            readings.add(read(lidarPosition));
            update();
        }
        return readings;
    }

    public void draw(Graphics g, MyDirectedPoint lidarPosition) {
        g.setColor(new Color(10, 10, 10, 32));
        g.drawOval((int) (lidarPosition.getX() - lidarRadius / 2), (int) (lidarPosition.getY() - lidarRadius / 2),
                (int) lidarRadius, (int) lidarRadius);

        // Draw where the lidar is pointing
        MyDirectedPoint sensor = lidarPosition.copy();
        sensor.rotate(bearing);
        sensor.move(MAX_DISTANCE);
        g.drawLine((int) lidarPosition.getX(), (int) lidarPosition.getY(), (int) sensor.getX(), (int) sensor.getY());
    }
}