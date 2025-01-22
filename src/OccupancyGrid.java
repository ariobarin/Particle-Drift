/* 
 * OccupancyGrid.java
 * Ario Barin Ostovary
 * Class for the occupancy grid, a grid that represents 
 * the probability of a cell being occupied by an object
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class OccupancyGrid {
    private double[][] grid;

    private int width;
    private int height;

    private final int cellSize;

    // center of the grid at the start
    private int centerX, centerY;

    private static final double GROW_FACTOR = 1.1;

    // log odds
    private static final double DEFAULT_LOG_ODDS = Util.probToLogit(0.5);
    private static final double LOG_ODDS_OCCUPIED = Util.probToLogit(0.75);
    private static final double LOG_ODDS_FREE = Util.probToLogit(0.25);

    // max and min log odds
    private static final double MAX_LOG_ODDS = Util.probToLogit(0.99);
    private static final double MIN_LOG_ODDS = Util.probToLogit(0.01);

    private static final double OCCUPIED_THRESHOLD = 0.7;
    private static final double FREE_THRESHOLD = 0.3;

    private BufferedImage image;

    public OccupancyGrid(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;

        this.grid = new double[width][height];

        // initialize grid
        this.grid = filledGrid(width, height);

        // center of the grid at the start
        this.centerX = width / 2;
        this.centerY = height / 2;

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        updateImage();
    }

    public OccupancyGrid(String filename, Color occupiedColor, Color freeColor, int cellSize) {
        try {
            this.image = ImageIO.read(new File(filename));

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            this.width = (int) Math.ceil(imageWidth / cellSize);
            this.height = (int) Math.ceil(imageHeight / cellSize);
            this.cellSize = cellSize;

            this.grid = filledGrid(width, height);
            this.centerX = width / 2;
            this.centerY = height / 2;

            // Process each cell in the grid
            for (int gridX = 0; gridX < width; gridX++) {
                for (int gridY = 0; gridY < height; gridY++) {
                    // Calculate the corresponding pixel region in the source image
                    int startX = gridX * cellSize;
                    int startY = gridY * cellSize;
                    int endX = Math.min(startX + cellSize, imageWidth);
                    int endY = Math.min(startY + cellSize, imageHeight);

                    int occupiedPixels = 0;
                    int totalPixels = 0;

                    // Check each pixel in the cell region
                    for (int x = startX; x < endX; x++) {
                        for (int y = startY; y < endY; y++) {
                            Color pixelColor = new Color(image.getRGB(x, y));
                            if (colorMatch(pixelColor, occupiedColor)) {
                                occupiedPixels++;
                            } else if (!colorMatch(pixelColor, freeColor)) {
                                // If pixel is neither occupied nor free color, treat as occupied
                                occupiedPixels++;
                            }
                            totalPixels++;
                        }
                    }

                    // Calculate probability of occupation for this cell
                    double probability = (double) occupiedPixels / totalPixels;
                    grid[gridX][gridY] = Util.probToLogit(Math.max(0.01, Math.min(0.99, probability)));
                }
            }

            updateImage();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + filename, e);
        }
    }

    public OccupancyGrid(String filename, Color freeColor, int width, int height, int centerX, int centerY, int cellSize) throws IOException {
        BufferedImage image = ImageIO.read(new File(filename));

        this.width = width;
        this.height = height;
        this.grid = filledGrid(width, height);
        
        this.cellSize = cellSize;
        this.centerX = centerX;
        this.centerY = centerY;

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        updateImage();


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = Util.probToLogit(image.getRGB(x, y) == freeColor.getRGB() ? 0.2 : 0.8);
            }
        }

        updateImage();
    }

    public double[][] getGrid() { return grid; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCellSize() { return cellSize; }

    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }

    public int worldToGridX(double x) {
        return (int) Math.round(x / cellSize) + centerX;
    }
    public int worldToGridY(double y) {
        return (int) Math.round(y / cellSize) + centerY;
    }

    public double gridToWorldX(double x) {
        return (x - centerX) * cellSize;
    }

    public double gridToWorldY(double y) {
        return (y - centerY) * cellSize;
    }

    public boolean isOccupied(double x, double y) {
        return isOccupiedCell(worldToGridX(x), worldToGridY(y));
    }
    
    public boolean isOccupiedCell(int x, int y) {
        return grid[x][y] > LOG_ODDS_OCCUPIED;
    }

    public boolean isFree(double x, double y) {
        return isFreeCell(worldToGridX(x), worldToGridY(y));
    }

    public boolean isFreeCell(int x, int y) {
        return grid[x][y] < LOG_ODDS_FREE;
    }

    // helper function
    private double[][] filledGrid(int width, int height) {
        double[][] filledGrid = new double[width][height];
        for (int x = 0; x < width; x++) {
            Arrays.fill(filledGrid[x], DEFAULT_LOG_ODDS);
        }
        return filledGrid;
    }
    
    private void resizeGrid(int cellX, int cellY) {
        boolean resized = false;

        int newWidth = width;
        int newHeight = height;
        int newCenterX = centerX;
        int newCenterY = centerY;

        int newX = cellX;
        int newY = cellY;

        // Modified to handle double GROW_FACTOR
        while (newX < 0 || newX >= newWidth) {
            if (newX < 0) {
                int growAmount = (int)(newWidth * (GROW_FACTOR - 1));
                newCenterX += growAmount;
                newX = cellX + (newCenterX - centerX);
            }
            newWidth = (int)(newWidth * GROW_FACTOR);
            resized = true;
        }

        while (newY < 0 || newY >= newHeight) {
            if (newY < 0) {
                int growAmount = (int)(newHeight * (GROW_FACTOR - 1));
                newCenterY += growAmount;
                newY = cellY + (newCenterY - centerY);
            }
            newHeight = (int)(newHeight * GROW_FACTOR);
            resized = true;
        }

        if (resized) {
            // new grid
            double[][] newGrid = filledGrid(newWidth, newHeight);

            int xOffset = newCenterX - centerX;
            int yOffset = newCenterY - centerY;

            // copy the previous grid into the new grid
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int x = i + xOffset;
                    int y = j + yOffset;
                    newGrid[x][y] = grid[i][j];
                }
            }

            // update grid and dimensions
            grid = newGrid;
            width = newWidth;
            height = newHeight;
            centerX = newCenterX;
            centerY = newCenterY;

            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            updateImage();
        }
    }

    public void increaseProbability(double x, double y) {
        increaseProbability((int) Math.round(x), (int) Math.round(y));
    }

    public void decreaseProbability(int dx, int dy) {
        decreaseCellProbability(worldToGridX(dx), worldToGridY(dy));
    }

    public void increaseProbability(int dx, int dy) {
        increaseCellProbability(worldToGridX(dx), worldToGridY(dy));
    }

    public void decreaseProbability(double x, double y) {
        decreaseProbability((int) Math.round(x), (int) Math.round(y));
    }

    public void increaseCellProbability(int x, int y) {
        int oldCenterX = centerX;
        int oldCenterY = centerY;
        resizeGrid(x, y);

        int newCenterX = centerX;
        int newCenterY = centerY;

        x += (newCenterX - oldCenterX);
        y += (newCenterY - oldCenterY);

        grid[x][y] += LOG_ODDS_OCCUPIED;
        grid[x][y] = Math.min(grid[x][y], MAX_LOG_ODDS);

        updateCellInImage(x, y);
    }

    public void decreaseCellProbability(int x, int y) {
        int oldCenterX = centerX;
        int oldCenterY = centerY;
        resizeGrid(x, y);

        int newCenterX = centerX;
        int newCenterY = centerY;

        x += (newCenterX - oldCenterX);
        y += (newCenterY - oldCenterY);

        grid[x][y] += LOG_ODDS_FREE;
        grid[x][y] = Math.max(grid[x][y], MIN_LOG_ODDS);

        updateCellInImage(x, y);
    }

    public void updateGrid(MyDirectedPoint pose, List<MyVector> lidarReadings) {
        for (MyVector reading : lidarReadings) {
            MyDirectedPoint rayEnd = pose.copy();
            rayEnd.rotate(reading.getDirection());
            rayEnd.move(reading.getMagnitude());

            int startX = worldToGridX(pose.getX());
            int startY = worldToGridY(pose.getY());
            int endX = worldToGridX(rayEnd.getX());
            int endY = worldToGridY(rayEnd.getY());

            List<MyPoint> freeCells = RayCaster.getCellsAlongRay(startX, startY, endX, endY);
            
            // remove the last point since it will be occupied
            if (!freeCells.isEmpty() && reading.getMagnitude() < Lidar.MAX_DISTANCE) {
                freeCells.remove(freeCells.size() - 1);
            }
            
            for (MyPoint cell : freeCells) {
                decreaseCellProbability((int) cell.getX(), (int) cell.getY());

                // make the cell red
                // image.setRGB((int) cell.getX(), (int) cell.getY(), Color.RED.getRGB());
            }

            if (reading.getMagnitude() < Lidar.MAX_DISTANCE) {
                increaseCellProbability(endX, endY);
            }
        }
    }

    public double getProbability(int dx, int dy) {
        int x = worldToGridX(dx);
        int y = worldToGridY(dy);

        resizeGrid(x, y);

        return Util.logitToProb(grid[x][y]);
    }

    public void clearGrid() {
        grid = filledGrid(width, height);
    }

    public void saveGrid(String filename) {
    }

    public void loadGrid(String filename) {
    }

    public OccupancyGrid copy() {
        // Create new grid with same dimensions and cell size
        OccupancyGrid copy = new OccupancyGrid(width, height, cellSize);

        // Copy center coordinates
        copy.centerX = this.centerX;
        copy.centerY = this.centerY;

        // Deep copy the grid array
        for (int x = 0; x < width; x++) {
            System.arraycopy(this.grid[x], 0, copy.grid[x], 0, height);
        }

        return copy;
    }

    private Color getColor(double probability) {
        if (probability > OCCUPIED_THRESHOLD) {
            return Color.BLACK; // Occupied
        } else if (probability < FREE_THRESHOLD) {
            return Color.WHITE; // Free
        } else {
            return new Color((int) (255 * probability), (int) (255 * probability), (int) (255 * probability));
        }
    }

    private void updateCellInImage(int x, int y) {
        double logOdds = grid[x][y];
        double probability = Util.logitToProb(logOdds);
        image.setRGB(x, y, getColor(probability).getRGB());
    }

    private void updateImage() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                updateCellInImage(x, y);
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    private boolean colorMatch(Color c1, Color c2) {
        return c1.getRed() == c2.getRed() && 
               c1.getGreen() == c2.getGreen() && 
               c1.getBlue() == c2.getBlue();
    }
}