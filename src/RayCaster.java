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
        List<MyPoint> cells = new ArrayList<>();

        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;

        int err = dx - dy;

        while (true) {
            cells.add(new MyPoint(startX, startY));

            if (startX == endX && startY == endY) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                startX += sx;
            }

            if (e2 < dx) {
                err += dx;
                startY += sy;
            }
        }

        return cells;
    }
}