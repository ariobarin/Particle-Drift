/*
 * View.java
 * Ario Barin Ostovary
 * Abstract class for handling and simplifying the different views in the program
 */

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class View {
    protected CarGUIPanel panel;

    public View(CarGUIPanel panel) {
        this.panel = panel;
    }

    // get the width and height of the panel
    protected int getWidth() { return panel.getWidth(); }
    protected int getHeight() { return panel.getHeight(); }

    // abstract methods to be implemented by each view
    public abstract void step(boolean[] keysDown, boolean[] keysPressed);
    public abstract void draw(Graphics g);  

    // key event handlers
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    // mouse event handlers
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}