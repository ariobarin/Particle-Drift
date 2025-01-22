/* RealView.java
 *

Ario Barin Ostovary & Kevin Dang
 
this class implements the car simulation using real life lidar data
*/ 



import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

public class RealView extends View {

    private CarSocket carSocket; // Socket to communicate with the car

    private String IP32 = "10.217.210.22";
    private int PORT32 = 8080;
    private String IP8266 = "10.217.210.187";
    private int PORT8266 = 80;
    //ip and port for the ESP32 and ESP8266

    private int connectKey = KeyEvent.VK_UP;

    private final int moveForwardKey = KeyEvent.VK_W;
    private final int moveBackwardKey = KeyEvent.VK_S;
    private final int turnLeftKey = KeyEvent.VK_A;
    private final int turnRightKey = KeyEvent.VK_D;
    //keys for movement
    

    private final Color LIGHT_RED = new Color(255, 200, 200);
    private final Color MATRIX_GREEN = new Color(0, 255, 0);
    private final Color DARK_GRAY = new Color(50, 50, 50);
    private final Color LIGHT_GREEN = new Color(0, 255, 0, 128);
    //colors for the GUI
    private boolean[] keysPressed = new boolean[4];
    private final int FORWARD = 0;
    private final int BACKWARD = 1;
    private final int LEFT = 2;
    private final int RIGHT = 3;
    //movement directions

    private boolean moving = false;
    //movement status

    
    private final int CONNECTION_PANEL_HEIGHT = 100;
    private final int CONNECTION_PANEL_WIDTH = 200;
    private final int CONNECTION_PANEL_X = 10;
    private final int CONNECTION_PANEL_Y = 10;

    private final int MOVEMENT_PANEL_WIDTH = 250;
    private final int MOVEMENT_PANEL_HEIGHT = 100;
    private final int MOVEMENT_PANEL_X = 10;
    private final int MOVEMENT_PANEL_Y = getHeight() - MOVEMENT_PANEL_HEIGHT - 10;

    ;
    private final int DATA_PANEL_HEIGHT = 100;
    private final int DATA_PANEL_WIDTH = 200;
    private final int DATA_PANEL_X = 220;
    private final int DATA_PANEL_Y = 10;
    //panel dimensions and positions

    private final ParticleFilter particleFilter;

<<<<<<< Updated upstream
    public RealView(CarGUIPanel panel, int viewIndex) {
        super(panel, viewIndex);
        carSocket = new CarSocket(); //initialize the car socket
         particleFilter = new ParticleFilter(); //initialize slam
=======
    private static final BufferedImage exitIcon;
    private final Button exitButton;

    static {
        exitIcon = Util.loadImage("assets/exit_icon.png");
    }

    public RealView(CarGUIPanel panel) {
        super(panel);

        carSocket = new CarSocket();
        particleFilter = new ParticleFilter();

        // create exit button in top-left corner
        exitButton = new Button(
            () -> 10,  // x supplier
            () -> 10,  // y supplier
            50, 50,    // width, height
            exitIcon, true,               // icon and initial state
            () -> panel.startMenu()  // onClick handler
        );
>>>>>>> Stashed changes
    }

    @Override
    public void step(boolean[] keysDown, boolean[] keysPressed) {
        if (keysDown[connectKey] && !carSocket.isESP32Connected() && !carSocket.isESP8266Connected()) {
            System.out.println("Connecting to car"); //connect to the car
            // carSocket.connectCar(IP32, PORT32, IP8266, PORT8266);
        }

        List<MyVector> lidar = carSocket.getLidar(); //get lidar data
        particleFilter.update(0, 0, lidar); //update slam with positions and lidar data
    }

    @Override
    public void draw(Graphics g) {
        particleFilter.draw(g);
        drawGUI(g); 
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("key pressed"); 
        int key = e.getKeyCode();
        System.out.println("key pressed: " + key);
        if (key == moveForwardKey) { 
            // carSocket.moveForward();
            keysPressed[FORWARD] = true;//keep track of the keys pressed for keystrokes gui
            System.out.println("move forward");
        }  if (key == moveBackwardKey) {
            // carSocket.moveBackward();
            keysPressed[BACKWARD] = true;
            System.out.println("move backward");
        } if (key == turnLeftKey) {
            // carSocket.turnLeft();
            keysPressed[LEFT] = true;
            System.out.println("turn left");
        } if (key == turnRightKey) {
            // carSocket.turnRight();  
            keysPressed[RIGHT] = true;
            System.out.println("turn right");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        moving = false;
        if (key == moveForwardKey) {
            keysPressed[FORWARD] = false;
        } else if (key == moveBackwardKey) {
            keysPressed[BACKWARD] = false;
        } else if (key == turnLeftKey) {
            keysPressed[LEFT] = false;
        } else if (key == turnRightKey) {
            keysPressed[RIGHT] = false;
        } //stop car
        // carSocket.stopMovement();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private void drawGUI(Graphics g) {
        // Draw Connection Status Panel
        drawPanel(g, "Connection Status", CONNECTION_PANEL_X, CONNECTION_PANEL_Y, CONNECTION_PANEL_WIDTH, CONNECTION_PANEL_HEIGHT);
        drawConnectionStatus(g); //draw the connection status

        // Draw Motor Data Panel
        drawPanel(g, "Motor Data", MOVEMENT_PANEL_X, MOVEMENT_PANEL_Y, MOVEMENT_PANEL_WIDTH, MOVEMENT_PANEL_HEIGHT);
        drawMotorData(g); //draw the motor data and keystrokes

        drawKeystrokes(g);

    
    }

    private void drawPanel(Graphics g, String title, int x, int y, int width, int height) { //custom function to draw a panel
        g.setColor(Color.BLACK); 
        g.fillRect(x, y, width, height);
        g.setColor(MATRIX_GREEN);
        // g.drawString(title, x + 10, y + 20);
    }

    private void drawConnectionStatus(Graphics g) {
        int statusYOffset = 30; // gap between the two statuses

        g.setColor(carSocket.isESP32Connected() ? Color.GREEN : Color.RED); //draw the connection status
        g.fillOval(CONNECTION_PANEL_X + 10, CONNECTION_PANEL_Y + 20, 20, 20); // ESP32 status
        g.setColor(MATRIX_GREEN); 
        g.drawString("ESP32 Connected", CONNECTION_PANEL_X + 40, CONNECTION_PANEL_Y + 35);

        g.setColor(carSocket.isESP8266Connected() ? Color.GREEN : Color.RED);
        g.fillOval(CONNECTION_PANEL_X + 10, CONNECTION_PANEL_Y + 20 + statusYOffset, 20, 20); // ESP8266 status
        g.setColor(MATRIX_GREEN);
        g.drawString("ESP8266 Connected", CONNECTION_PANEL_X + 40, CONNECTION_PANEL_Y + 35 + statusYOffset);
    }

    private void drawMotorData(Graphics g) {
        g.setColor(MATRIX_GREEN);
        g.drawString("Motor Status: " + (moving ? "Moving" : "Stopped"), MOVEMENT_PANEL_X + 100, MOVEMENT_PANEL_Y + 30); //draw the motor status
    }
    private void drawKeystrokes(Graphics g) {
        g.setColor(MATRIX_GREEN);
        int boxWidth = 30;   // width of each box
        int boxHeight = 30;  // height of each box
        int xOffset = 25;  // offset from the left
        int yOffset = MOVEMENT_PANEL_Y + (MOVEMENT_PANEL_HEIGHT - 2 * boxHeight - 10) / 2 - 2;  //centered vertically in panel
        int gap = 5;  // Gap between the boxes
        Color pressedColor = LIGHT_GREEN;
        Color defaultColor = DARK_GRAY;
    
        g.setColor(keysPressed[FORWARD] ? pressedColor : defaultColor); //change the color of the box if the key is pressed
        g.fillRect(xOffset + boxWidth, yOffset, boxWidth, boxHeight); //draw box
        g.setColor(Color.WHITE);
        g.drawString("W", xOffset + boxWidth + (boxWidth - g.getFontMetrics().stringWidth("W")) / 2, yOffset + (boxHeight + g.getFontMetrics().getHeight()) / 2);
        //draw letter
        g.setColor(keysPressed[BACKWARD] ? pressedColor : defaultColor);
        g.fillRect(xOffset + boxWidth, yOffset + boxHeight + gap, boxWidth, boxHeight); 
        g.setColor(Color.WHITE);
        g.drawString("S", xOffset + boxWidth + (boxWidth - g.getFontMetrics().stringWidth("S")) / 2, yOffset + boxHeight + gap + (boxHeight + g.getFontMetrics().getHeight()) / 2);
        //same process for the rest of the keys
        g.setColor(keysPressed[LEFT] ? pressedColor : defaultColor);
        g.fillRect(xOffset - gap, yOffset + boxHeight, boxWidth, boxHeight);  
        g.setColor(Color.WHITE);
        g.drawString("A", xOffset  - gap + (boxWidth - g.getFontMetrics().stringWidth("A")) / 2, yOffset + boxHeight + (boxHeight + g.getFontMetrics().getHeight()) / 2);
    
        g.setColor(keysPressed[RIGHT] ? pressedColor : defaultColor);

        g.fillRect(xOffset + 2 * boxWidth + gap, yOffset + boxHeight, boxWidth, boxHeight); 
        g.setColor(Color.WHITE);
        g.drawString("D", xOffset + 2 * boxWidth + gap + (boxWidth - g.getFontMetrics().stringWidth("D")) / 2, yOffset + boxHeight + (boxHeight + g.getFontMetrics().getHeight()) / 2);
    }
    
    
    
}
