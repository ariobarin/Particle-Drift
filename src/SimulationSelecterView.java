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
    private final Button exitButton;  // add exit button field
    private static final BufferedImage exitIcon;  // add exit icon field
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 150;
    private static final int GRID_COLS = 3;
    private static final int GRID_SPACING = 20;

    private final MatrixEffect matrixEffect;  // add matrix effect

    static {
        // load numbered button images
        buttonImages = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            buttonImages[i] = Util.loadImage("assets/button" + (i+1) + ".png");
        }
        exitIcon = Util.loadImage("assets/back_icon.png");  // load exit icon
    }

    public SimulationSelecterView(CarGUIPanel panel) {
        super(panel);
        
        // initialize matrix effect with 15 columns
        matrixEffect = new MatrixEffect(
            () -> 0,                // x offset
            () -> getWidth(),      // width
            () -> getHeight(),     // height
            15                     // number of columns
        );
        
        mapButtons = new Button[5];
        
        // create exit button in top-left corner with 2x size
        exitButton = new Button(
            () -> 20,  // x position (adjusted for larger size)
            () -> 20,  // y position (adjusted for larger size)
            100, 100,  // width, height (2x larger)
            exitIcon,  // icon
            true,      // initial state
            () -> panel.startMenu()  // onClick handler
        );
        
        // create buttons in a grid layout with numbered images
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
        
        // draw all buttons
        for (Button button : mapButtons) {
            button.draw(g);
        }
        
        // draw exit button
        exitButton.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // check exit button first
        if (exitButton.contains(e.getX(), e.getY())) {
            exitButton.click();
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
        // update exit button hover state
        exitButton.setHovered(exitButton.contains(e.getX(), e.getY()));
        
        // update hover states
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

