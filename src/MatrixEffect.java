/*
 * name: Ario Barin Ostovary
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class MatrixEffect {
    private ArrayList<MatrixColumn> columns;
    private final Random random;
    private final int columnWidth = 30;
    private final Color trailColor = new Color(0, 255, 0, 128);
    private final Color headColor = new Color(200, 255, 200);

    private final Supplier<Integer> widthSupplier;

    private class MatrixColumn {
        Supplier<Integer> xSupplier;
        int y;
        int speed;
        ArrayList<Integer> trail;
        static final int MAX_TRAIL = 25;

        MatrixColumn(Supplier<Integer> xSupplier) {
            this.xSupplier = xSupplier;
            this.y = random.nextInt(200) - 400; // start above screen at random positions
            this.speed = random.nextInt(5) + 3;  // faster speed between 3-7
            this.trail = new ArrayList<>();
        }

        void update(int screenHeight) {
            y += speed;
            trail.add(y);
            
            // remove old trail positions
            while (trail.size() > MAX_TRAIL) {
                trail.remove(0);
            }

            // reset position when reaching bottom
            if (y > screenHeight + 50) {
                y = -50;
                trail.clear();
            }
        }

        void draw(Graphics g) {
            int x = xSupplier.get();
            // draw trail with brighter color
            for (int i = 0; i < trail.size(); i++) {
                int alpha = (int)(255 * (i / (float)MAX_TRAIL));
                g.setColor(new Color(0, 255, 0, 255 - alpha));  // brighter green
                g.fillRect(x, trail.get(i), columnWidth, columnWidth);
            }
            
            // draw head with brighter color
            g.setColor(headColor);
            g.fillRect(x, y, columnWidth, columnWidth);
        }
    }

    public MatrixEffect(Supplier<Integer> widthSupplier) {
        this.widthSupplier = widthSupplier;
        random = new Random();
        columns = new ArrayList<>();
    }

    public void init(int screenWidth) {
        columns.clear();
        // create columns across screen width
        for (int i = 0; i < widthSupplier.get() / columnWidth; i++) {
            final int columnIndex = i;  // need final for lambda
            columns.add(new MatrixColumn(() -> columnIndex * columnWidth));
        }
    }

    public void update(int screenHeight) {
        // check if we need to add/remove columns due to resize
        int expectedColumns = widthSupplier.get() / columnWidth;
        while (columns.size() < expectedColumns) {
            final int columnIndex = columns.size();  // need final for lambda
            columns.add(new MatrixColumn(() -> columnIndex * columnWidth));
        }
        while (columns.size() > expectedColumns) {
            columns.remove(columns.size() - 1);
        }

        // update existing columns
        for (MatrixColumn column : columns) {
            column.update(screenHeight);
        }
    }

    public void draw(Graphics g) {
        for (MatrixColumn column : columns) {
            column.draw(g);
            System.out.println("drawing column");
        }
    }
}