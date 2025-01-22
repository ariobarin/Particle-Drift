/*
 * Util.java
 * Ario Barin Ostovary
 * Class for utility functions
 */

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;

public class Util {
    // these methods are thread safe
    public static double randomGaussian(double standardDeviation) {
        return ThreadLocalRandom.current().nextGaussian() * standardDeviation;
    }
    public static double randomDouble(double start, double end) {
        return ThreadLocalRandom.current().nextDouble(start, end);
    }
    public static int randomInt(int start, int end) {
        if (start == end) {
            return start;
        }
        // validate that end is greater than start
        if (end < start) {
            // hopefully this doesn't cause bugs
            return ThreadLocalRandom.current().nextInt(end, start);
        }
        return ThreadLocalRandom.current().nextInt(start, end);
    }

    public static double logitToProb(double logit) {
        if (logit > 0) {
            // avoid overflow
            return 1.0 / (1.0 + Math.exp(-logit));
        } else {
            // avoid underflow
            return Math.exp(logit) / (1.0 + Math.exp(logit));
        }
    }

    public static double probToLogit(double prob) {
        if (prob <= 0.0 || prob >= 1.0) {
            throw new IllegalArgumentException("Probability must be between 0 and 1 exclusive");
        }
        return Math.log(prob / (1.0 - prob));
    }

    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException("Could not load image at path: " + path, e);
        }
    }

    public static void drawImage(Graphics g, BufferedImage img, int x, int y, int width, int height) {
        // Draw the image with nearest neighbor interpolation - no blurring
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(img, x, y, width, height, null);
    }

    public static void drawText(Graphics g, String text, int x, int y, int size) {
        g.setFont(new Font("Arial", Font.BOLD, size));
        g.drawString(text, x, y);
    }

    public static void drawCenteredText(Graphics g, String text, int x, int y, int size) {
        FontMetrics metrics = g.getFontMetrics(new Font("Arial", Font.BOLD, size));
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g.drawString(text, x - textWidth / 2, y - textHeight / 2);
    }

    public static void drawOrientedRoundedRect(Graphics g, MyDirectedPoint position, int width, int height,
            int radius) {
        drawOrientedRoundedRect(g, position, width, height, radius, true);
    }


    public static void drawOrientedRoundedRect(Graphics g, MyDirectedPoint position, int width, int height,
            int radius, boolean filled) {
        // draw the rectangle at the position with the rotation of the directed point
        MyPoint point = position.getPoint();
        double angle = position.getRadians();

        Graphics2D g2d = (Graphics2D) g;

        // Save the original transform
        var oldTransform = g2d.getTransform();

        try {
            // Translate to the position and rotate
            g2d.translate(point.getX(), point.getY());
            g2d.rotate(angle);

            // Move back by half width/height to center the rectangle
            g2d.translate(-width / 2, -height / 2);

            // Draw the rounded rectangle (filled or outlined)
            if (filled) {
                g2d.fillRoundRect(0, 0, width, height, radius * 2, radius * 2);
            } else {
                g2d.drawRoundRect(0, 0, width, height, radius * 2, radius * 2);
            }
        } finally {
            // Restore the original transform
            g2d.setTransform(oldTransform);
        }
    }

    public static void drawOrientedImage(Graphics g, BufferedImage img, MyDirectedPoint position, int width,
            int height) {
        MyPoint point = position.getPoint();
        double angle = position.getRadians();

        Graphics2D g2d = (Graphics2D) g;

        // Save the original transform
        var oldTransform = g2d.getTransform();

        try {
            // Translate to the position and rotate
            g2d.translate(point.getX(), point.getY());
            g2d.rotate(angle);

            // Move back by half width/height to center the image
            g2d.translate(-width / 2, -height / 2);

            // Draw the image with nearest neighbor interpolation - no blurring
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(img, 0, 0, width, height, null);
        } finally {
            // Restore the original transform
            g2d.setTransform(oldTransform);
        }
    }
}