/*
 * Ario Barin Ostovary
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

    static {
        simulationButtonIcon = Util.loadImage("assets/sim_icon.png");
        realButtonIcon = Util.loadImage("assets/real_icon.png");
        backgroundRoad = Util.loadImage("assets/main_bg.png");
    }

    public MenuView(CarGUIPanel panel) {
        super(panel);
        
        // create left and right matrix effects
        leftMatrixEffect = new MatrixEffect(
            () -> 0,                                 // x offset for left side
            () -> (panel.getWidth() - MIDDLE_GAP) / 2,     // width for left side
            () -> panel.getHeight(),
            60  // half the original columns
        );
        
        rightMatrixEffect = new MatrixEffect(
            () -> panel.getWidth() / 2 + MIDDLE_GAP / 2,   // x offset for right side
            () -> (panel.getWidth() - MIDDLE_GAP) / 2,     // width for right side
            () -> panel.getHeight(),
            60  // half the original columns
        );
        
        // create buttons with dynamic positioning
        simulationButton = new Button(
            () -> panel.getWidth()/2 - 40,           // x supplier
            () -> panel.getHeight()/2 - 100,           // y supplier
            80, 100,                                  // width, height
            simulationButtonIcon,
            true,                                     // initial state
            () -> panel.startSimulationSelecter()         // onClick handler
        );
        
        realButton = new Button(
            () -> panel.getWidth()/2 - 40,           // x supplier
            () -> panel.getHeight()/2 + 50,           // y supplier
            80, 100,                                  // width, height
            realButtonIcon,
            true,                                     // initial state
            () -> panel.startReal()         // onClick handler
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
        
        // draw buttons on top
        simulationButton.draw(g);
        realButton.draw(g);
    }
}
