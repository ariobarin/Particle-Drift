/*
 * Tank.java
 * Ario Barin Ostovary
 * Class for the tank
 * Contains the position, movement, and drawing of the tank
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Tank {
    // Tank position
    private final MyDirectedPoint position;

    // Movement Constants
    private final double MAX_SPEED = 2;
    private final double MAX_ACCELERATION = 1;
    private final double FRICTION = MAX_SPEED / (MAX_SPEED + MAX_ACCELERATION);

    // Movement variables
    private double speed;
    private double acceleration;

    // Rotation Constants
    private final double MAX_ROTATION_SPEED = Math.toRadians(3);
    private final double MAX_ANGULAR_ACCELERATION = Math.toRadians(0.7);
    private final double ROTATION_FRICTION = MAX_ROTATION_SPEED / (MAX_ROTATION_SPEED + MAX_ANGULAR_ACCELERATION);

    // Rotation variables
    private double rotationSpeed;
    private double rotationAcceleration;

    // Tank dimensions
    private final double TANK_LENGTH = 50;
    private final double TANK_WIDTH = 40;

    private final double DRIVE_TIRE_LENGTH = 14;
    private final double DRIVE_TIRE_WIDTH = 5;

    private final double SECONDARY_TIRE_LENGTH = 4;
    private final double SECONDARY_TIRE_WIDTH = 2;

    // Noise constants
    private final double SPEED_NOISE = 0.0;
    private final double ROTATION_NOISE = 0.0;

    // movement keys
    private static final int moveForward = KeyEvent.VK_W;
    private static final int moveBackward = KeyEvent.VK_S;
    private static final int rotateAntiClockwise = KeyEvent.VK_A;
    private static final int rotateClockwise = KeyEvent.VK_D;

    // add image field
    private BufferedImage carImage;

    public Tank(int x, int y, double angle) {
        position = new MyDirectedPoint(x, y, angle);

        // Movement variables
        speed = 0;
        acceleration = 0;

        // Rotation variables
        rotationSpeed = 0;
        rotationAcceleration = 0;
        
        // load the car image
        try {
            carImage = ImageIO.read(new File("assets/licar.png"));
        } catch (IOException e) {
            System.err.println("Error loading car image: " + e.getMessage());
        }
    }

    public MyDirectedPoint getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed + Util.randomGaussian(SPEED_NOISE);
        // return -speed;
    }

    public double getRotationSpeed() {
        return rotationSpeed + Util.randomGaussian(ROTATION_NOISE);
        // return -rotationSpeed;
    }

    public void update(boolean[] keys) {
        // Update acceleration
        acceleration = 0;
        if (keys[moveForward] && keys[moveBackward]) {
        } else if (keys[moveForward]) {
            acceleration = MAX_ACCELERATION;
        } else if (keys[moveBackward]) {
            acceleration = -MAX_ACCELERATION;
        }

        // Update speed based on acceleration
        speed += acceleration;
        speed *= FRICTION;

        // Update rotation acceleration
        rotationAcceleration = 0;
        if (keys[rotateAntiClockwise] && keys[rotateClockwise]) {
        } else if (keys[rotateAntiClockwise]) {
            rotationAcceleration = -MAX_ANGULAR_ACCELERATION;
        } else if (keys[rotateClockwise]) {
            rotationAcceleration = MAX_ANGULAR_ACCELERATION;
        }

        // Update rotation speed based on rotation acceleration
        rotationSpeed += rotationAcceleration;
        rotationSpeed *= ROTATION_FRICTION;

        // Update position based on speed and rotation speed
        position.rotate(rotationSpeed);
        position.move(speed);
    }

    public void drawTire(Graphics g, MyDirectedPoint tire, Color color, boolean isDriveTire) {
        g.setColor(color);
        // Use appropriate tire dimensions based on type
        double tireLength = isDriveTire ? DRIVE_TIRE_LENGTH : SECONDARY_TIRE_LENGTH;
        double tireWidth = isDriveTire ? DRIVE_TIRE_WIDTH : SECONDARY_TIRE_WIDTH;

        Util.drawOrientedRoundedRect(g, tire, (int) tireLength, (int) tireWidth, 2, true);
    }

    public void draw(Graphics g) {
        if (carImage != null) {
            // save the current transform
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
            java.awt.geom.AffineTransform old = g2d.getTransform();
            
            // translate to car position
            g2d.translate(position.getX(), position.getY());
            
            // rotate (add 90 degrees since image faces up)
            g2d.rotate(position.getRadians() + Math.PI/2);
            
            // calculate scale factors to match tank dimensions
            double scaleX = TANK_WIDTH / carImage.getWidth();
            double scaleY = TANK_LENGTH / carImage.getHeight();
            g2d.scale(scaleX, scaleY);
            
            // draw image centered
            g2d.drawImage(carImage, 
                -carImage.getWidth()/2, 
                -carImage.getHeight()/2, 
                null);
            
            // restore the old transform
            g2d.setTransform(old);
        }
    }
}