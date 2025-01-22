import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class View {
    protected CarGUIPanel panel;
    protected int nextView;

    public View(CarGUIPanel panel, int viewIndex) {
        this.panel = panel;
        this.nextView = viewIndex;
    }

    protected int getWidth() { return panel.getWidth(); }
    protected int getHeight() { return panel.getHeight(); }

    public abstract void step(boolean[] keysDown, boolean[] keysPressed);

    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    
    public abstract void draw(Graphics g);  

    public int nextView() {
        return nextView;
    }
}