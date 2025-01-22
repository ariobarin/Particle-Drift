/*
 * Button.java
 * Ario Barin Ostovary
 * Class for creating interactive GUI buttons with hover effects and state management
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

public class Button {
    private int x, y, width, height;
    private final BufferedImage icon;
    
    // suppliers for dynamic positioning
    private Supplier<Integer> xSupplier;
    private Supplier<Integer> ySupplier;
    private boolean isDynamicPosition;

    // action to execute when button is clicked
    private final Runnable onClick;

    // current button state (enabled/disabled)
    private boolean state;

    // tracks if mouse is currently hovering over button
    private boolean isHovered;

    private static final Color DISABLED_COLOR = new Color(128, 0, 0, 200);
    private static final Color ENABLED_COLOR = new Color(0, 128, 0, 200);
    private static final Color ENABLED_HOVER_COLOR = new Color(32, 64, 0, 200);
    private static final Color DISABLED_HOVER_COLOR = new Color(64, 32, 0, 200);

    // constructor for static positioning
    public Button(int x, int y, int width, int height, BufferedImage icon, boolean initialState, Runnable onClick) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.icon = icon;
        this.state = initialState;
        this.onClick = onClick;
        this.isHovered = false;
        this.isDynamicPosition = false;
    }

    // constructor for dynamic positioning
    public Button(Supplier<Integer> xSupplier, Supplier<Integer> ySupplier, int width, int height, 
                 BufferedImage icon, boolean initialState, Runnable onClick) {
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.width = width;
        this.height = height;
        this.icon = icon;
        this.state = initialState;
        this.onClick = onClick;
        this.isHovered = false;
        this.isDynamicPosition = true;
        updatePosition();
    }

    // updates position if using dynamic positioning
    private void updatePosition() {
        if (isDynamicPosition) {
            this.x = xSupplier.get();
            this.y = ySupplier.get();
        }
    }

    // draws the button with colors based on state and hover status
    public void draw(Graphics g) {
        updatePosition();  // update position before drawing
        
        // can be hovered and/or disabled
        if (isHovered) {
            g.setColor(state ? ENABLED_HOVER_COLOR : DISABLED_HOVER_COLOR);
        } else {
            g.setColor(state ? ENABLED_COLOR : DISABLED_COLOR);
        }

        g.fillRect(x, y, width, height);
        Util.drawImage(g, icon, x, y, width, height);
    }

    // checks if given coordinates are within button boundaries
    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + this.width &&
                y >= this.y && y <= this.y + this.height;
    }

    // executes the button's assigned action
    public void click() {
        onClick.run();
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }
}