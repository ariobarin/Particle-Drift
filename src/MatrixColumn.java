/*
 * Ario Barin Ostovary
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;

public class MatrixColumn {
    private final Supplier<Integer> heightSupplier;
    private final Supplier<Integer> widthSupplier;

    private final ArrayList<Integer> heads;
    private final ArrayList<Integer> speeds;
    private final int numHeads;

    private final ArrayList<Color> headColors;
    private final int trailLength = 50;

    private boolean resetRequired = true;

    public MatrixColumn(Supplier<Integer> widthSupplier, Supplier<Integer> heightSupplier) {
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;

        this.numHeads = Util.randomInt(3, 5);
        this.heads = new ArrayList<>();
        this.speeds = new ArrayList<>();
        this.headColors = new ArrayList<>();
        this.init();
    }

    public void init() {
        heads.clear();
        speeds.clear();
        headColors.clear();
        for (int i = 0; i < numHeads; i++) {
            int pos = Util.randomInt(0, heightSupplier.get() - trailLength);
            int speed = Util.randomInt(1, 5);
            
            // calculate green intensity based on speed
            // slower speed = darker green (speed 1 -> 100-150, speed 5 -> 200-255)
            int greenMin = 50 + (speed * 20);
            int greenMax = 135 + (speed * 20);
            
            Color color = new Color(
                Util.randomInt(0, 100),  // slight red tint
                Util.randomInt(greenMin, greenMax), // speed-based green with randomness
                Util.randomInt(0, 100)   // slight blue tint
            );
            heads.add(pos);
            speeds.add(speed);
            headColors.add(color);
        }
    }

    public void update() {
        if (widthSupplier.get() != 0 && heightSupplier.get() != 0 && resetRequired) {
            init();
            resetRequired = false;
        }
        for (int i = 0; i < heads.size(); i++) {
            heads.set(i, heads.get(i) + speeds.get(i));
            if (heads.get(i)-trailLength >= heightSupplier.get()) {
                heads.set(i, 0);
            }
        }
    }


    public void draw(Graphics g, int xOffset) {
        // draw trails with gradient
        for (int i = 0; i < heads.size(); i++) {
            Color headColor = headColors.get(i);
            for (int j = 0; j < trailLength; j++) {
                // calculate alpha based on position in trail (255 near head, 0 at tail)
                int alpha = (int)(255 * (1 - (float)j / trailLength));
                g.setColor(new Color(
                    headColor.getRed(),
                    headColor.getGreen(),
                    headColor.getBlue(),
                    alpha
                ));
                g.fillRect(xOffset + widthSupplier.get() + (i * 20), heads.get(i) - j, 10, 1);
            }
        }
        
        // draw heads
        for (int i = 0; i < heads.size(); i++) {
            g.setColor(headColors.get(i));
            g.fillRect(xOffset + widthSupplier.get() + (i * 20), heads.get(i), 10, 1);
        }
    }
} 