/*
 * name: Ario Barin Ostovary
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MenuView extends View {
    private final Button simulationButton;
    private final Button realButton;
    private final MatrixEffect matrixEffect;

    public MenuView(CarGUIPanel panel, int viewIndex) {
        super(panel, viewIndex);
        
        matrixEffect = new MatrixEffect(() -> panel.getWidth());
        
        // create buttons with dynamic positioning
        simulationButton = new Button(
            () -> panel.getWidth()/2 - 150,           // x supplier
            () -> panel.getHeight()/2 - 75,           // y supplier
            300, 50,                                  // width, height
            Util.loadImage("assets/simulation_button.png"),
            true,                                     // initial state
            () -> nextView = CarGUIPanel.SIM         // onClick handler
        );
        
        realButton = new Button(
            () -> panel.getWidth()/2 - 150,           // x supplier
            () -> panel.getHeight()/2 + 25,           // y supplier
            300, 50,                                  // width, height
            Util.loadImage("assets/real_button.png"),
            true,                                     // initial state
            () -> nextView = CarGUIPanel.REAL         // onClick handler
        );
    }

    @Override
    public void step(boolean[] keysDown, boolean[] keysPressed) {
        matrixEffect.update(panel.getWidth(), panel.getHeight());
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
        
        // draw matrix effect
        matrixEffect.draw(g);
        
        // draw buttons on top
        simulationButton.draw(g);
        realButton.draw(g);
    }
}
