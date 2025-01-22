import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SimulationSelecterView extends View {
    private final Button[] mapButtons;
    private static final BufferedImage mapIcon;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 100;
    private static final int GRID_COLS = 3;
    private static final int GRID_SPACING = 20;

    static {
        // load a generic map icon for all buttons
        mapIcon = Util.loadImage("assets/map_icon.png");
    }

    public SimulationSelecterView(CarGUIPanel panel) {
        super(panel);
        
        mapButtons = new Button[5];
        
        // create buttons in a grid layout
        for (int i = 0; i < 5; i++) {
            final int index = i;  // create final copy
            final int mapNum = i + 1;
            mapButtons[i] = new Button(
                () -> getWidth()/2 - ((GRID_COLS * BUTTON_WIDTH + (GRID_COLS-1) * GRID_SPACING) / 2) + 
                      (index % GRID_COLS) * (BUTTON_WIDTH + GRID_SPACING),
                () -> getHeight()/2 - BUTTON_HEIGHT + 
                      (index / GRID_COLS) * (BUTTON_HEIGHT + GRID_SPACING),
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                mapIcon,
                true,
                () -> {
                    System.out.println("Loading map" + mapNum + ".txt"); // debug print
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
        
        // draw all buttons
        for (Button button : mapButtons) {
            button.draw(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
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
        // update hover states
        for (Button button : mapButtons) {
            button.setHovered(button.contains(e.getX(), e.getY()));
        }
    }

    @Override
    public void step(boolean[] keyDown, boolean[] keyDownLast) {
        // removed the automatic map1 loading
    }
}

