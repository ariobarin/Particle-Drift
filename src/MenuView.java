/*
 * MenuView.java
 * Ario Barin Ostovary
 * Menu view for the project
 * Can select between simulation and real car
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class MenuView extends View {
    private final Button simulationButton;
    private final Button realButton;

    private final MatrixEffect leftMatrixEffect;
    private final MatrixEffect rightMatrixEffect;

    private static final int MIDDLE_GAP = 200;
    private static final BufferedImage simulationButtonIcon;
    private static final BufferedImage realButtonIcon;
    private static final BufferedImage backgroundRoad;
    private static final BufferedImage titleImage;

    // load images
    static {
        simulationButtonIcon = Util.loadImage("assets/sim_icon.png");
        realButtonIcon = Util.loadImage("assets/real_icon.png");
        backgroundRoad = Util.loadImage("assets/main_bg.png");
        titleImage = Util.loadImage("assets/title.png");
    }

    public MenuView(CarGUIPanel panel) {
        super(panel);
        
        // create left and right matrix effects
        leftMatrixEffect = new MatrixEffect(
            () -> 0,
            () -> (panel.getWidth() - MIDDLE_GAP) / 2 - 40,
            () -> panel.getHeight(),
            30
        );
        
        rightMatrixEffect = new MatrixEffect(
            () -> panel.getWidth() / 2 + MIDDLE_GAP / 2,
            () -> (panel.getWidth() - MIDDLE_GAP) / 2,
            () -> panel.getHeight(),
            30
        );
        
        // create buttons with dynamic positioning
        simulationButton = new Button(
            () -> panel.getWidth()/2 - 40,
            () -> panel.getHeight()/2 - 100,
            80, 100,
            simulationButtonIcon,
            true,
            () -> panel.startSimulationSelecter()
        );
        
        realButton = new Button(
            () -> panel.getWidth()/2 - 40,
            () -> panel.getHeight()/2 + 50,
            80, 100,
            realButtonIcon,
            true,
            () -> panel.startReal()
        );
    }

    @Override
    public void step(boolean[] keysDown, boolean[] keysPressed) {
        leftMatrixEffect.update();
        rightMatrixEffect.update();
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // handle button clicks
        if (simulationButton.contains(e.getX(), e.getY())) {
            simulationButton.click();
        }
        if (realButton.contains(e.getX(), e.getY())) {
            realButton.click();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // update hover states
        simulationButton.setHovered(simulationButton.contains(e.getX(), e.getY()));
        realButton.setHovered(realButton.contains(e.getX(), e.getY()));
    }

    @Override
    public void draw(Graphics g) {
        // clear background to black first
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, panel.getWidth(), panel.getHeight());

        // draw background road
        g.drawImage(backgroundRoad, getWidth()/2 - 50, 0, 100, getHeight(), null);
        
        // draw matrix effects
        leftMatrixEffect.draw(g);
        rightMatrixEffect.draw(g);
        
        // draw title image
        g.drawImage(titleImage, getWidth()/2 - 400, 10, 800, 200, null);
        
        // draw buttons on top
        simulationButton.draw(g);
        realButton.draw(g);
    }
}
