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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

public class Simulation {
    public final int WORLD_WIDTH;
    public final int WORLD_HEIGHT;


    private final String mapFile;
    private final String maskFile;
    private final BufferedImage mask;
    private final BufferedImage bg;

    private final String exploredFile;
    // private final BufferedImage explored;

    private final LiCar licar;

    public static final Color AIR = new Color(255, 174, 201);

    public Simulation(String mapFile) throws IOException {
        // load txt from "maps/" + mapFile
        BufferedReader reader = new BufferedReader(new FileReader("maps/" + mapFile));

        // read map data - i wish i could've used a json file instead but i couldn't get the json library to work
        this.maskFile = reader.readLine();
        this.mapFile = reader.readLine();
        int width = Integer.parseInt(reader.readLine());
        int height = Integer.parseInt(reader.readLine());
        int resolution = Integer.parseInt(reader.readLine());
        int centerX = Integer.parseInt(reader.readLine());
        int centerY = Integer.parseInt(reader.readLine());
        int startX = Integer.parseInt(reader.readLine());
        int startY = Integer.parseInt(reader.readLine());
        double startAngle = Double.parseDouble(reader.readLine());
        this.exploredFile = reader.readLine();
        
        // calculate particle filter dimensions
        this.WORLD_WIDTH = width * resolution;
        this.WORLD_HEIGHT = height * resolution;
        
        // load images from maps/
        mask = ImageIO.read(new File("maps/" + maskFile));
        bg = ImageIO.read(new File("maps/" + this.mapFile));
        
        // OccupancyGrid occupancyGrid = new OccupancyGrid("maps/" + maskFile, AIR, WORLD_WIDTH, WORLD_HEIGHT, WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 1);
        licar = new LiCar(this, centerX + startX, centerY + startY, startAngle, new ParticleFilter());
    }

    public boolean isAir(int x, int y) {
        if (x < 0 || x >= WORLD_WIDTH || y < 0 || y >= WORLD_HEIGHT) {
            return false;
        }

        try {
            return mask.getRGB(x, y) == AIR.getRGB();
        } catch (Exception e) {
            return false;
        }
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
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
    }

    // handles drawing a view of the world
    // centerCar: whether to center the car on the view
    // g2d: the graphics context to draw on
    // backgroundColor: the color of the background
    // drawOperations: the operations to draw on the view
    private void drawView(
            boolean centerCar,
            Graphics2D g2d,
            Color backgroundColor,
            Consumer<Graphics2D> drawOperations) {
    
        // get dimensions from the graphics context
        int viewportWidth = g2d.getClipBounds().width;
        int viewportHeight = g2d.getClipBounds().height;

        // create a buffered image to draw on and the graphics context
        BufferedImage view = new BufferedImage(viewportWidth, viewportHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = view.createGraphics();

        // calculate scaling factors
        double scaleX = viewportWidth / (double) WORLD_WIDTH;
        double scaleY = viewportHeight / (double) WORLD_HEIGHT;
        double scale = Math.min(scaleX, scaleY);

        // fill background with background color
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, viewportWidth, viewportHeight);

        // center the view in the viewport
        int centerX = viewportWidth / 2;
        int centerY = viewportHeight / 2;

        // apply transformations to center the view and scale it
        graphics.translate(centerX, centerY);
        graphics.scale(scale, scale);

        // only apply following transformations if this is not the fixed world view
        if (centerCar) {
            MyDirectedPoint tankPosition = licar.getActualPosition();
            double tankX = tankPosition.getX();
            double tankY = tankPosition.getY();
            double tankAngle = tankPosition.getAngle().getRadians();

            graphics.rotate(-tankAngle - Math.PI / 2);
            graphics.translate(-tankX, -tankY);
        } else {
            // for fixed world view, just center the world
            graphics.translate(-WORLD_WIDTH / 2, -WORLD_HEIGHT / 2);
        }

        // execute unique drawing operations
        drawOperations.accept(graphics);

        graphics.dispose();

        g2d.drawImage(view, 0, 0, null);
    }

    public void update(boolean[] keys) {
        licar.update(keys);
    }

    // draws the POV view of the car from above
    public void drawPOV(Graphics g) {
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

    // draws the lidar view of the car
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

    // draws the simulation view of the world - overview of the world
    public void drawSimulation(Graphics g) {
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

    // draws the occupancy grid of the world
    public void drawOccupancyGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        licar.drawOccupancyGrid(g2d);
        g2d.dispose();
    }
}