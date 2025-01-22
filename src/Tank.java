/*
 * Tank.java
 * Ario Barin Ostovary
 * Class for the tank
 * Contains the position, movement, and drawing of the tank
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Tank {
    // tank position
    private final MyDirectedPoint position;

    // movement constants
    private final double MAX_SPEED = 2;
    private final double MAX_ACCELERATION = 1;
    private final double FRICTION = MAX_SPEED / (MAX_SPEED + MAX_ACCELERATION);

    // movement variables
    private double speed;
    private double acceleration;

    // rotation constants
    private final double MAX_ROTATION_SPEED = Math.toRadians(3);
    private final double MAX_ANGULAR_ACCELERATION = Math.toRadians(0.7);
    private final double ROTATION_FRICTION = MAX_ROTATION_SPEED / (MAX_ROTATION_SPEED + MAX_ANGULAR_ACCELERATION);

    // rotation variables
    private double rotationSpeed;
    private double rotationAcceleration;

    // tank dimensions
    private final double TANK_LENGTH = 50;
    private final double TANK_WIDTH = 40;

    private final double DRIVE_TIRE_LENGTH = 14;
    private final double DRIVE_TIRE_WIDTH = 5;

    private final double SECONDARY_TIRE_LENGTH = 4;
    private final double SECONDARY_TIRE_WIDTH = 2;

    // noise constants
    private final double SPEED_NOISE = 0.0;
    private final double ROTATION_NOISE = 0.0;

    // movement keys
    private static final int moveForward = KeyEvent.VK_W;
    private static final int moveBackward = KeyEvent.VK_S;
    private static final int rotateAntiClockwise = KeyEvent.VK_A;
    private static final int rotateClockwise = KeyEvent.VK_D;

    private BufferedImage carImage;

    public Tank(int x, int y, double angle) {
        position = new MyDirectedPoint(x, y, angle);

        speed = 0;
        acceleration = 0;

        rotationSpeed = 0;
        rotationAcceleration = 0;
        
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
        // update acceleration
        acceleration = 0;
        if (keys[moveForward] && keys[moveBackward]) {
        } else if (keys[moveForward]) {
            acceleration = MAX_ACCELERATION;
        } else if (keys[moveBackward]) {
            acceleration = -MAX_ACCELERATION;
        }

        // update speed based on acceleration
        speed += acceleration;
        speed *= FRICTION;

        // update rotation acceleration
        rotationAcceleration = 0;
        if (keys[rotateAntiClockwise] && keys[rotateClockwise]) {
        } else if (keys[rotateAntiClockwise]) {
            rotationAcceleration = -MAX_ANGULAR_ACCELERATION;
        } else if (keys[rotateClockwise]) {
            rotationAcceleration = MAX_ANGULAR_ACCELERATION;
        }

        // update rotation speed based on rotation acceleration
        rotationSpeed += rotationAcceleration;
        rotationSpeed *= ROTATION_FRICTION;

        // update position based on speed and rotation speed
        position.rotate(rotationSpeed);
        position.move(speed);
    }

    // draws the tank - rotated and scaled to fit the tank dimensions
    public void draw(Graphics g) {
        if (carImage != null) {
            // save the current transform
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform old = g2d.getTransform();
            
            // translate to car position
            g2d.translate(position.getX(), position.getY());
            
            // rotate - image faces up so rotate extra 90 degrees
            g2d.rotate(position.getRadians() + Math.PI/2);
            
            // scale to match tank dimensions
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