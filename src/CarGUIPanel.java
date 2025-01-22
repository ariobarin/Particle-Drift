/* 
 * CarGUIPanel.java
 * Ario Barin Ostovary & Kevin Dang
 * 
 * this class implements a GUI panel for a car simulation/visualization system
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CarGUIPanel extends JPanel implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
    public static final int BASE_WIDTH = 1200, BASE_HEIGHT = 800;
    private static final int FPS = 100;
    private static final int ESC_KEY = KeyEvent.VK_ESCAPE;

    // track keyboard input state
    private final boolean[] keysDown;    // tracks continuously held keys
    private final boolean[] keysPressed; // tracks single key press events

    private final Timer timer;
    private int currentFPS = 0;
    private int frameCount = 0;
    private long lastFPSCheck = 0;
    
    private View currentView;   // active view object

    public CarGUIPanel() {
        keysDown = new boolean[KeyEvent.KEY_LAST + 1];
        keysPressed = new boolean[KeyEvent.KEY_LAST + 1];
        timer = new Timer(1000 / FPS, this);
        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        setFocusable(true);
        requestFocus();

        // startSimulation();
        // startReal();
        startMenu();
        
        // Add listeners
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        timer.start();
    }

    public void startMenu() {
        currentView = new MenuView(this);
    }

    public void startSimulationSelecter() {
        currentView = new SimulationSelecterView(this);
    }

    public void startSimulation(String mapFile) {
        currentView = new SimulationView(this, mapFile);
    }

    public void startReal() {
        currentView = new RealView(this);
    }

    public void step() {
        frameCount++;
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFPSCheck >= 1000) { // Update FPS every second
            currentFPS = frameCount;
            frameCount = 0;
            lastFPSCheck = currentTime;
        }
        // System.out.println("FPS: " + currentFPS);
        currentView.step(keysDown, keysPressed);

        // clear keysPressed
        for (int i = 0; i < keysPressed.length; i++) {
            keysPressed[i] = false;
        }

    }

    @Override
    public void paint(Graphics g) {
        // clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        currentView.draw(g);

        // draw FPS counter in top right corner
        String fpsText = "FPS: " + currentFPS;
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(fpsText);
        g.drawString(fpsText, getWidth() - textWidth - 10, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int index = e.getKeyCode();
        if (index >= keysDown.length) return;

        // handle ESC key to exit program
        if (index == ESC_KEY) {
            System.exit(0);
        }

        if (!keysDown[index]) {
            keysPressed[index] = true;
            keysDown[index] = true;
        }

        currentView.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int index = e.getKeyCode();
        if (index >= keysDown.length) return;

        keysDown[index] = false;
        keysPressed[index] = false;

        currentView.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        currentView.keyTyped(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        currentView.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentView.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        currentView.mouseReleased(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        currentView.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        currentView.mouseExited(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currentView.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        currentView.mouseMoved(e);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        step();
        repaint();
    }
}