/* 
 * CarGUIPanel.java
 * Ario Barin Ostovary & Kevin Dang
 * 
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CarGUIPanel extends JPanel implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
    public static final int BASE_WIDTH = 1200, BASE_HEIGHT = 800;
    private static final int FPS = 100;

    // Array of keys currently pressed
    private final boolean[] keysDown; // keys currently down
    private final boolean[] keysPressed; // keys pressed (on press)

    private final Timer timer;
    
    private int currentFPS = 0;
    private int frameCount = 0;
    private long lastFPSCheck = 0;
    
    private static final int MENU = 0, SIM = 1, REAL = 2, SETTINGS = 3;
    private int current;
    private View currentView;

    public CarGUIPanel() {
        keysDown = new boolean[KeyEvent.KEY_LAST + 1];
        keysPressed = new boolean[KeyEvent.KEY_LAST + 1];
        timer = new Timer(1000 / FPS, this);
        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        setFocusable(true);
        requestFocus();

        // startSimulation();
        startReal();
        
        // Add listeners
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        timer.start();
    }

    public void startSimulation() {
        current = SIM;
        currentView = new SimulationView(new World(), this);
    }

    public void startReal() {
        current = REAL;
        currentView = new RealView(this);
    }

    public void startSettings() {
        current = SETTINGS;
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
    public void keyPressed(KeyEvent e) {
        int index = e.getKeyCode();
        if (index >= keysDown.length) {
            return;
        }
        if (!keysDown[index]) {
            keysPressed[index] = true;
            keysDown[index] = true;
        }
        currentView.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // if the key is released, set the corresponding index in the keysPressed array
        // to false
        int index = e.getKeyCode();
        if (index >= keysDown.length) {
            return;
        }
        keysDown[index] = false;
        keysPressed[index] = false;
        currentView.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (current == MENU) {
            startSimulation();
            return;
        }
        
        currentView.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        currentView.mouseReleased(e);
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
        currentView.mouseMoved(e);
    }

    @Override
    public void paint(Graphics g) {
        currentView.draw(g);

        // Draw FPS counter
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("FPS: " + currentFPS, 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        step();
        repaint();
    }
}