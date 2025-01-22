/* 
 * World.java
 * Ario Barin Ostovary
 * Class for the world, contains the tank and the map
 * Contains the drawing of the world, the tank, and the lidar
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import java.io.File;
import java.io.FileReader;
import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;

public class Simulation {
    public final int WORLD_WIDTH;
    public final int WORLD_HEIGHT;

    private final BufferedImage mask;
    private final BufferedImage bg;
    private final LiCar licar;

    public static final Color AIR = new Color(255, 174, 201);

    public Simulation(String mapFile) throws IOException {
        // load json from "maps/" + mapFile
        JsonObject mapData = Json.createReader(new FileReader("maps/" + mapFile))
                                   .readObject();
        WORLD_WIDTH = mapData.getInt("width");
        WORLD_HEIGHT = mapData.getInt("height");

        // load mask and bg (buffered images)
        mask = ImageIO.read(new File("maps/" + mapData.getString("mask")));
        bg = ImageIO.read(new File("maps/" + mapData.getString("map")));

        licar = new LiCar(this, 
            mapData.getInt("centerX"),
            mapData.getInt("centerY"),
            0
        );
    }

    public boolean isAir(int x, int y) {
        if (x < 0 || x >= WORLD_WIDTH || y < 0 || y >= WORLD_HEIGHT) {
            return false;
        }

        return mask.getRGB(x, y) == AIR.getRGB();
    }

    public int getWidth() {
        return WORLD_WIDTH;
    }

    public int getHeight() {
        return WORLD_HEIGHT;
    }

    public void drawMask(Graphics g) {
        g.drawImage(mask, 0, 0, null);
    }

    public void drawBackground(Graphics g) {
        g.drawImage(bg, 0, 0, null);
    }

    private void drawView(
            boolean centerCar,
            Graphics2D g2d,
            Color backgroundColor,
            Consumer<Graphics2D> drawOperations) {
        // Get dimensions from the graphics context
        int viewportWidth = g2d.getClipBounds().width;
        int viewportHeight = g2d.getClipBounds().height;

        // Initialize BufferedImage
        BufferedImage view = new BufferedImage(viewportWidth, viewportHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = view.createGraphics();

        // Calculate scaling factors
        double scaleX = viewportWidth / (double) WORLD_WIDTH;
        double scaleY = viewportHeight / (double) WORLD_HEIGHT;
        double scale = Math.min(scaleX, scaleY);

        // Fill background
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, viewportWidth, viewportHeight);

        // Center the view in the viewport
        int centerX = viewportWidth / 2;
        int centerY = viewportHeight / 2;

        // Apply transformations
        graphics.translate(centerX, centerY);
        graphics.scale(scale, scale);

        // Only apply car-following transformations if this is not the fixed world view
        if (centerCar) {
            MyDirectedPoint tankPosition = licar.getActualPosition();
            double tankX = tankPosition.getX();
            double tankY = tankPosition.getY();
            double tankAngle = tankPosition.getAngle().getRadians();

            graphics.rotate(-tankAngle - Math.PI / 2);
            graphics.translate(-tankX, -tankY);
        } else {
            // For fixed world view, just center the world
            graphics.translate(-WORLD_WIDTH / 2, -WORLD_HEIGHT / 2);
        }

        // Execute unique drawing operations
        drawOperations.accept(graphics);

        // Dispose temporary Graphics2D
        graphics.dispose();

        // Draw the buffered image onto the main Graphics2D context
        g2d.drawImage(view, 0, 0, null);
    }

    public void update(boolean[] keys) {
        licar.update(keys);
    }

    public void drawSimulation(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            drawView(
                    true,
                    g2d,
                    Color.WHITE,
                    graphics -> {
                        drawBackground(graphics);
                        licar.draw(graphics, false, true);
                    });
        } finally {
            g2d.dispose();
        }
    }

    public void drawLidar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            drawView(
                    true,
                    g2d,
                    Color.BLACK,
                    graphics -> {
                        licar.draw(graphics, false, true);
                    });
        } finally {
            g2d.dispose();
        }
    }

    public void drawPOV(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            drawView(
                    false,
                    g2d,
                    Color.WHITE,
                    graphics -> {
                        drawBackground(graphics);
                        licar.draw(graphics, false, true);
                    });
        } finally {
            g2d.dispose();
        }
    }

    public void drawOccupancyGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        licar.drawOccupancyGrid(g2d);
        g2d.dispose();
    }
}