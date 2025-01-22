/*
 * RayCaster.java
 * Ario Barin Ostovary
 * Class for casting rays from a point to a point
 */

import java.util.ArrayList;
import java.util.List;

public class RayCaster {
    // get the cells along the ray from start to end
    public static List<MyPoint> getCellsAlongRay(MyPoint start, MyPoint end) {
        int startX = (int) Math.round(start.getX());
        int startY = (int) Math.round(start.getY());
        int endX = (int) Math.round(end.getX());
        int endY = (int) Math.round(end.getY());

        return getCellsAlongRay(startX, startY, endX, endY);
    }

    // get the cells along the ray from start to end with Bresenham's line algorithm
    public static List<MyPoint> getCellsAlongRay(int startX, int startY, int endX, int endY) {
        // store all points along the ray
        List<MyPoint> cells = new ArrayList<>();

        // calculate absolute differences between start and end points
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        // determine direction of movement (-1 or 1) for both x and y
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;

        // error term for Bresenham's algorithm
        // this helps decide whether to move in x or y direction
        int err = dx - dy;

        while (true) {
            // add current point to our path
            cells.add(new MyPoint(startX, startY));

            // check if we've reached the destination
            if (startX == endX && startY == endY) {
                break;
            }

            // calculate error term for next step
            int e2 = 2 * err;
            
            // if error term is positive enough, move in x direction
            if (e2 > -dy) {
                err -= dy;
                startX += sx;
            }

            // if error term is small enough, move in y direction
            if (e2 < dx) {
                err += dx;
                startY += sy;
            }
        }

        return cells;
    }
}