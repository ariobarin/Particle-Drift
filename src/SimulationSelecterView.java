/*
 * SimulationSelecterView.java
 * Ario Barin Ostovary
 * Class for the simulation selecter view
 * Displays the simulation selecter view
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SimulationSelecterView extends View {
    private final Button[] mapButtons;
    private static final BufferedImage[] buttonImages;
    private final Button backButton;
    private static final BufferedImage backIcon;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 150;
    private static final int GRID_COLS = 3;
    private static final int GRID_SPACING = 20;

    private final MatrixEffect matrixEffect;

    static {
        // load numbered button images
        buttonImages = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            buttonImages[i] = Util.loadImage("assets/button" + (i+1) + ".png");
        }

        backIcon = Util.loadImage("assets/back_icon.png");
    }

    public SimulationSelecterView(CarGUIPanel panel) {
        super(panel);
        
        matrixEffect = new MatrixEffect(
            () -> 0,
            () -> getWidth(),
            () -> getHeight(),
            15
        );
        
        mapButtons = new Button[5];
        
        // back button
        backButton = new Button(
            () -> 20,
            () -> 120,
            100, 100,
            backIcon,
            true,
            () -> panel.startMenu()
        );
        
        // map buttons
        for (int i = 0; i < 5; i++) {
            final int index = i;
            final int mapNum = i + 1;
            
            // calculate total grid height (2 rows)
            int totalGridHeight = 2 * BUTTON_HEIGHT + GRID_SPACING;
            
            mapButtons[i] = new Button(
                () -> getWidth()/2 - ((GRID_COLS * BUTTON_WIDTH + (GRID_COLS-1) * GRID_SPACING) / 2) + 
                      (index % GRID_COLS) * (BUTTON_WIDTH + GRID_SPACING),
                () -> getHeight()/2 - (totalGridHeight / 2) +
                      (index / GRID_COLS) * (BUTTON_HEIGHT + GRID_SPACING),
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                buttonImages[i],
                true,
                () -> {
                    panel.startSimulation("map" + mapNum + ".txt");
                }
            );
        }
    }

    @Override
    public void draw(Graphics g) {
        // clear background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // draw matrix effect
        matrixEffect.draw(g);
        
        // draw map buttons
        for (Button button : mapButtons) {
            button.draw(g);
        }
        
        // draw back button
        backButton.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // check back button first
        if (backButton.contains(e.getX(), e.getY())) {
            backButton.click();
            return;
        }
        
        // handle button clicks
        for (Button button : mapButtons) {
            if (button.contains(e.getX(), e.getY())) {
                button.click();
                return;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // update back button hover state
        backButton.setHovered(backButton.contains(e.getX(), e.getY()));
        
        // update map buttons hover state
        for (Button button : mapButtons) {
            button.setHovered(button.contains(e.getX(), e.getY()));
        }
    }

    @Override
    public void step(boolean[] keyDown, boolean[] keyDownLast) {
        // update matrix effect
        matrixEffect.update();
    }
}

