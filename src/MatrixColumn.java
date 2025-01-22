/*
 * MatrixColumn.java
 * Ario Barin Ostovary
 * Class for creating a single matrix column and managing and drawing its heads and tails
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;

public class MatrixColumn {
    // suppliers for the width and height of the column
    private final Supplier<Integer> heightSupplier;
    private final Supplier<Integer> widthSupplier;

    // lists for the heads and speeds of the column
    private final ArrayList<Integer> heads;
    private final ArrayList<Integer> speeds;
    private final int numHeads;

    // list for the colors of the heads
    private final ArrayList<Color> headColors;
    
    // list for the varying trail lengths of the column
    private final ArrayList<Integer> trailLengths;

    private boolean resetRequired = true;

    public MatrixColumn(Supplier<Integer> widthSupplier, Supplier<Integer> heightSupplier) {
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;

        // number of heads in the column
        this.numHeads = Util.randomInt(3, 5);

        this.heads = new ArrayList<>();
        this.speeds = new ArrayList<>();
        this.headColors = new ArrayList<>();
        this.trailLengths = new ArrayList<>();
        this.init();
    }

    public void init() {
        heads.clear();
        speeds.clear();
        headColors.clear();
        trailLengths.clear();
        for (int i = 0; i < numHeads; i++) {
            int speed = Util.randomInt(1, 5);
            // calculate trail length based on speed (faster = longer trail)
            int trailLength = 20 + (speed * 16) + Util.randomInt(0, 20);
            
            int pos = Util.randomInt(0, heightSupplier.get() - trailLength);
            
            // calculate green intensity based on speed
            int greenMin = 50 + (speed * 20);
            int greenMax = 135 + (speed * 20);
            
            Color color = new Color(
                Util.randomInt(0, 128),  // slight red tint
                Util.randomInt(greenMin, greenMax), // speed-based green with randomness
                Util.randomInt(0, 192)   // slight blue tint
            );
            heads.add(pos);
            speeds.add(speed);
            headColors.add(color);
            trailLengths.add(trailLength);
        }
    }

    public void update() {
        // the width and height of the column must be greater than 0, wait until they are to reset
        if (widthSupplier.get() != 0 && heightSupplier.get() != 0 && resetRequired) {
            init();
            resetRequired = false;
        }

        // update the heads of the column
        for (int i = 0; i < heads.size(); i++) {
            // move the head down by the speed
            heads.set(i, heads.get(i) + speeds.get(i));

            // if the head is out of bounds, reset it
            if (heads.get(i) - trailLengths.get(i) >= heightSupplier.get()) {
                heads.set(i, Util.randomInt(-trailLengths.get(i), 0));
            }
        }
    }


    public void draw(Graphics g, int xOffset) {
        // draw trails with gradient
        for (int i = 0; i < heads.size(); i++) {
            Color headColor = headColors.get(i);
            int trailLength = trailLengths.get(i);
            for (int j = 0; j < trailLength; j++) {

                // calculate alpha based on position in trail (255 near head, 0 at tail)
                int alpha = (int)(255 * (1 - (float)j / trailLength));
                g.setColor(new Color(
                    headColor.getRed(),
                    headColor.getGreen(),
                    headColor.getBlue(),
                    alpha
                ));

                // draw the trail
                g.fillRect(xOffset + widthSupplier.get() + (i * 20), heads.get(i) - j, 10, 1);
            }
        }
        
        // draw heads based on the offset and width of the column
        for (int i = 0; i < heads.size(); i++) {
            g.setColor(headColors.get(i));
            g.fillRect(xOffset + widthSupplier.get() + (i * 20), heads.get(i), 10, 1);
        }
    }
} 