import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

public class RealView extends View {

    private CarSocket carSocket;

    private String IP32 = "10.217.210.22";
    private int PORT32 = 8080;
    private String IP8266 = "10.217.210.187";
    private int PORT8266 = 80;

    private int connectKey = KeyEvent.VK_UP;

    private final int moveForwardKey = KeyEvent.VK_W;
    private final int moveBackwardKey = KeyEvent.VK_S;
    private final int turnLeftKey = KeyEvent.VK_A;
    private final int turnRightKey = KeyEvent.VK_D;

    private boolean moving = false;

    private final ParticleFilter particleFilter;

    public RealView(CarGUIPanel panel) {
        super(panel);

        carSocket = new CarSocket();
        particleFilter = new ParticleFilter();
    }

    @Override
    public void step(boolean[] keysDown, boolean[] keysPressed) {
        if (keysDown[connectKey] && !carSocket.isESP32Connected() && !carSocket.isESP8266Connected()) {
            System.out.println("Connecting to car");
            carSocket.connectCar(IP32, PORT32, IP8266, PORT8266);
        }


        List<MyVector> lidar = carSocket.getLidar();
        particleFilter.update(0, 0, lidar);

    }

    @Override
    public void draw(Graphics g) {
        particleFilter.draw(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("key pressed");
        int key = e.getKeyCode();
        System.out.println("key pressed: " + key);
        if (key == moveForwardKey) {
            carSocket.moveForward();
            System.out.println("move forward");
        }
        else if (key == moveBackwardKey) {
            carSocket.moveBackward();
            System.out.println("move backward");
        }
        else if (key == turnLeftKey) {
            carSocket.turnLeft();
            System.out.println("turn left");
        }
        else if (key == turnRightKey) {
            carSocket.turnRight();
            System.out.println("turn right");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        moving = false;
        carSocket.stopMovement();
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

}
