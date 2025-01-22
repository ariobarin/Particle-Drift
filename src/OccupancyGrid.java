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

            // get the width and height of the image
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            // calculate the width and height of the grid
            this.width = (int) Math.ceil(imageWidth / cellSize);
            this.height = (int) Math.ceil(imageHeight / cellSize);
            this.cellSize = cellSize;

            // initialize the grid
            this.grid = filledGrid(width, height);

            // center of the grid at the start
            this.centerX = width / 2;
            this.centerY = height / 2;

            // process each cell in the grid
            for (int gridX = 0; gridX < width; gridX++) {
                for (int gridY = 0; gridY < height; gridY++) {
                    // calculate the corresponding pixel region in the source image
                    int startX = gridX * cellSize;
                    int startY = gridY * cellSize;
                    int endX = Math.min(startX + cellSize, imageWidth);
                    int endY = Math.min(startY + cellSize, imageHeight);

                    int occupiedPixels = 0;
                    int totalPixels = 0;

                    // check each pixel in the cell region
                    for (int x = startX; x < endX; x++) {
                        for (int y = startY; y < endY; y++) {
                            Color pixelColor = new Color(image.getRGB(x, y));
                            if (colorMatch(pixelColor, occupiedColor)) {
                                occupiedPixels++;
                            } else if (!colorMatch(pixelColor, freeColor)) {
                                // if pixel is neither occupied nor free color, treat as occupied
                                occupiedPixels++;
                            }
                            totalPixels++;
                        }
                    }

                    // calculate probability of occupation for this cell
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

        // use default log odds then will be updated later
        this.grid = filledGrid(width, height);
        
        this.cellSize = cellSize;
        this.centerX = centerX;
        this.centerY = centerY;

        // create a new buffered image for this frame
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        updateImage();

        // process each cell in the grid
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // calculate the probability of occupation for this cell
                grid[x][y] = Util.probToLogit(image.getRGB(x, y) == freeColor.getRGB() ? 0.2 : 0.8);
            }
        }

        // update the image with the new grid
        updateImage();
    }

    public double[][] getGrid() { return grid; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCellSize() { return cellSize; }

    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }

    // convert world coordinates to grid coordinates
    public int worldToGridX(double x) {
        return (int) Math.round(x / cellSize) + centerX;
    }
    public int worldToGridY(double y) {
        return (int) Math.round(y / cellSize) + centerY;
    }

    // convert grid coordinates to world coordinates
    public double gridToWorldX(double x) {
        return (x - centerX) * cellSize;
    }
    public double gridToWorldY(double y) {
        return (y - centerY) * cellSize;
    }

    // check if a cell is occupied - world coordinates
    public boolean isOccupied(double x, double y) {
        return isOccupiedCell(worldToGridX(x), worldToGridY(y));
    }
    
    // check if a cell is occupied - grid coordinates
    public boolean isOccupiedCell(int x, int y) {
        return grid[x][y] > LOG_ODDS_OCCUPIED;
    }

    // check if a cell is free - world coordinates
    public boolean isFree(double x, double y) {
        return isFreeCell(worldToGridX(x), worldToGridY(y));
    }

    // check if a cell is free - grid coordinates
    public boolean isFreeCell(int x, int y) {
        return grid[x][y] < LOG_ODDS_FREE;
    }

    // check if a cell is unknown - world coordinates
    public boolean isUnknown(double x, double y) {
        return isUnknownCell(worldToGridX(x), worldToGridY(y));
    }

    // check if a cell is unknown - grid coordinates
    public boolean isUnknownCell(int x, int y) {
        return grid[x][y] == DEFAULT_LOG_ODDS;
    }

    // helper function to fill the grid with default log odds
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

        // keep growing the grid until the cell is within the grid
        while (newX < 0 || newX >= newWidth) {
            if (newX < 0) {
                // grow the grid by a factor of GROW_FACTOR
                int growAmount = (int)(newWidth * (GROW_FACTOR - 1));
                newCenterX += growAmount;

                // update the newX to the cellX plus the new centerX minus the old centerX to check if it is within the grid after the grid is resized
                newX = cellX + (newCenterX - centerX);
            }
            // update the new width
            newWidth = (int)(newWidth * GROW_FACTOR);
            resized = true;
        }

        while (newY < 0 || newY >= newHeight) {
            if (newY < 0) {
                // grow the grid by a factor of GROW_FACTOR
                int growAmount = (int)(newHeight * (GROW_FACTOR - 1));
                newCenterY += growAmount;

                // update the newY to the cellY plus the new centerY minus the old centerY to check if it is within the grid after the grid is resized
                newY = cellY + (newCenterY - centerY);
            }
            // update the new height
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

            // update the image with the new grid
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
        // get the old center of the grid
        int oldCenterX = centerX;
        int oldCenterY = centerY;

        // resize the grid to include the cell
        resizeGrid(x, y);

        // get the new center of the grid
        int newCenterX = centerX;
        int newCenterY = centerY;

        // update the x and y to the new center of the grid
        x += (newCenterX - oldCenterX);
        y += (newCenterY - oldCenterY);

        // increase the probability of the cell
        grid[x][y] += LOG_ODDS_OCCUPIED;
        grid[x][y] = Math.min(grid[x][y], MAX_LOG_ODDS);

        // update the image with the new grid
        updateCellInImage(x, y);
    }

    public void decreaseCellProbability(int x, int y) {
        // get the old center of the grid
        int oldCenterX = centerX;
        int oldCenterY = centerY;

        // resize the grid to include the cell
        resizeGrid(x, y);

        // get the new center of the grid
        int newCenterX = centerX;
        int newCenterY = centerY;

        // update the x and y to the new center of the grid
        x += (newCenterX - oldCenterX);
        y += (newCenterY - oldCenterY);

        // decrease the probability of the cell
        grid[x][y] += LOG_ODDS_FREE;
        grid[x][y] = Math.max(grid[x][y], MIN_LOG_ODDS);

        // update the image with the new grid
        updateCellInImage(x, y);
    }

    public void updateGrid(MyDirectedPoint pose, List<MyVector> lidarReadings) {
        // process each lidar reading to update the grid
        for (MyVector reading : lidarReadings) {
            MyDirectedPoint rayEnd = pose.copy();
            rayEnd.rotate(reading.getDirection());
            rayEnd.move(reading.getMagnitude());

            // get the start and end positions of the ray in the grid
            int startX = worldToGridX(pose.getX());
            int startY = worldToGridY(pose.getY());
            int endX = worldToGridX(rayEnd.getX());
            int endY = worldToGridY(rayEnd.getY());

            // get the cells along the ray
            List<MyPoint> freeCells = RayCaster.getCellsAlongRay(startX, startY, endX, endY);
            
            // remove the last point since it will be occupied
            if (!freeCells.isEmpty() && reading.getMagnitude() < Lidar.MAX_DISTANCE) {
                freeCells.remove(freeCells.size() - 1);
            }

            // decrease the probability of each cell along the ray
            for (MyPoint cell : freeCells) {
                decreaseCellProbability((int) cell.getX(), (int) cell.getY());

                // make the cell red
                // image.setRGB((int) cell.getX(), (int) cell.getY(), Color.RED.getRGB());
            }

            // if the reading is within the maximum distance, increase the probability of the cell at the end of the ray
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
        // TODO
    }

    public void loadGrid(String filename) {
        // TODO
    }

    public OccupancyGrid copy() {
        // create new grid with same dimensions and cell size
        OccupancyGrid copy = new OccupancyGrid(width, height, cellSize);

        // copy center coordinates
        copy.centerX = this.centerX;
        copy.centerY = this.centerY;

        // deep copy the grid array
        for (int x = 0; x < width; x++) {
            System.arraycopy(this.grid[x], 0, copy.grid[x], 0, height);
        }

        return copy;
    }

    private Color getColor(double probability) {
        // get the color based on the probability
        if (probability > OCCUPIED_THRESHOLD) {
            return Color.BLACK; // occupied
        } else if (probability < FREE_THRESHOLD) {
            return Color.WHITE; // free
        } else {
            return new Color((int) (255 * probability), (int) (255 * probability), (int) (255 * probability));
        }
    }

    private void updateCellInImage(int x, int y) {
        // get the log odds of the cell
        double logOdds = grid[x][y];

        // convert the log odds to a probability
        double probability = Util.logitToProb(logOdds);

        // set the color of the cell in the image
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