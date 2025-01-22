/*
 * LiCar.java
 * Ario Barin Ostovary & Kevin Dang
 * Class combining the tank and lidar - uses particlefilter to create and draw a map of the world
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class LiCar {
    // reference to the simulation - to check for walls
    private final Simulation simulation;

    private final Lidar lidar;
    private final Tank tank;
    private List<MyVector> lidarReadings;

    private final ParticleFilter particleFilter;

    public LiCar(Simulation simulation, int x, int y, double angle, ParticleFilter particleFilter) {
        this.simulation = simulation;
        tank = new Tank(x, y, angle);
        lidar = new Lidar(simulation);
        lidarReadings = new ArrayList<>();

        this.particleFilter = particleFilter;
    }

    public MyDirectedPoint getActualPosition() {
        // get where the tank is actually located (not the estimated position)
        return tank.getPosition();
    }

    public MyDirectedPoint getEstimatedPosition() {
        // get the estimated position of the tank using the particle filter
        return particleFilter.getEstimatedPosition();
    }

    public void update(boolean[] keysPressed) {
        // update the tank's position and rotation based on the keys pressed
        tank.update(keysPressed);
        
        // scan the lidar to get the readings
        lidarReadings = lidar.scan(tank.getPosition());

        // update the particle filter with the new readings
        double speed = tank.getSpeed();
        double angle = tank.getRotationSpeed();
        particleFilter.update(speed, angle, lidarReadings);
    }

    private void drawCar(Graphics g) {
        // draw the tank and the lidar
        MyDirectedPoint tankPosition = tank.getPosition();
        lidar.draw(g, tankPosition);
        tank.draw(g);
    }

    private void drawEstimatedPosition(Graphics g) {
        // draw the estimated position of the tank - looks ugly
        int width = 800;
        int height = 600;
        int x = (int) Math.round(getEstimatedPosition().getX() + width / 2);
        int y = (int) Math.round(getEstimatedPosition().getY() + height / 2);
        g.setColor(Color.RED);
        int radius = 10;
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        // make a ray to show direction of the tank
        MyDirectedPoint end = getEstimatedPosition().copy();
        end.move(radius * 3);
        int endX = (int) Math.round(end.getX() + width / 2);
        int endY = (int) Math.round(end.getY() + height / 2);
        g.drawLine(x, y, endX, endY);
    }

    private void drawRays(Graphics g) {
        // draw the rays from the lidar readings
        MyDirectedPoint tankPosition = tank.getPosition();
        g.setColor(Color.RED);

        // go through each ray and travel forward by the magnitude of the vector
        for (MyVector v : lidarReadings) {
            MyDirectedPoint rayEnd = tankPosition.copy();
            rayEnd.rotate(v.getDirection());
            rayEnd.move(v.getMagnitude());
            g.drawLine((int) tankPosition.getX(), (int) tankPosition.getY(), (int) rayEnd.getX(), (int) rayEnd.getY());
        }
    }

    // draw the readings from the lidar
    private void drawReadings(Graphics g) {
        MyDirectedPoint tankPosition = tank.getPosition();
        int radius = 3;
        g.setColor(Color.GREEN);

        // go through each ray and draw a circle at the end of the ray
        for (MyVector v : lidarReadings) {
            MyDirectedPoint rayEnd = tankPosition.copy();
            rayEnd.rotate(v.getDirection());
            rayEnd.move(v.getMagnitude());
            g.drawOval((int) rayEnd.getX() - radius, (int) rayEnd.getY() - radius, radius * 2, radius * 2);
        }
    }

    public void draw(Graphics g, boolean drawRays, boolean drawReadings) {
        drawCar(g);
        // drawEstimatedPosition(g);

        if (drawRays) {
            drawRays(g);
        }
        if (drawReadings) {
            drawReadings(g);
        }
    }

    public void drawOccupancyGrid(Graphics g) {
        particleFilter.draw(g);
    }
}