
 
 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;






public class LidarDisplay extends JFrame{

	GamePanel display = new GamePanel();


    public LidarDisplay() {
		super("Move the Box");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(display);
		pack();
		setVisible(true);
    }
    
    public static void main(String[] arguments) {
		new LidarDisplay();
    }
}

class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener{
	
	private boolean []keys;
	Timer timer;
	Image back;
	private CarSocket LiCar;
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	private JButton connectButton;
	private boolean moving = false;

	private String IP32 = "10.217.210.22";
	private int PORT32 = 8080;
	private String IP8266 = "10.217.210.187";
	private int PORT8266 = 80;
	
	public GamePanel(){
		keys = new boolean[256]; // Initialize the keys array
		addKeyListener(this);
		setFocusable(true);
		timer = new Timer(1000 / 60, this);
		timer.start();
		LiCar = new CarSocket();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		
			
		}

		


@Override
	public void actionPerformed(ActionEvent e){
		repaint(); 

		if(keys[KeyEvent.VK_UP]&&!LiCar.isESP32Connected()&&!LiCar.isESP8266Connected()){
			LiCar.connectCar(IP32, PORT32, IP8266, PORT8266);
		}

	}
	
	@Override
	public void keyReleased(KeyEvent ke){
		int key = ke.getKeyCode();
		keys[key] = false;
		moving = false;
		LiCar.stopMovement();
	}	
	
	@Override
	public void keyPressed(KeyEvent ke){
		int key = ke.getKeyCode();
		keys[key] = true;
		if(moving){
			return;
		}
		if(keys[KeyEvent.VK_W]){
		moving = true;
		LiCar.moveForward();
		}
		else if(keys[KeyEvent.VK_S]){
		moving = true;
		LiCar.moveBackward();
		}
		else if(keys[KeyEvent.VK_A]){
		moving = true;
		LiCar.turnLeft();
		}
		else if(keys[KeyEvent.VK_D]){
		moving = true;
		LiCar.turnRight();
		}

	}
	
	@Override
	public void keyTyped(KeyEvent ke){}
	@Override
	public void	mouseClicked(MouseEvent e){}

	@Override
	public void	mouseEntered(MouseEvent e){}

	@Override
	public void	mouseExited(MouseEvent e){}

	@Override
	public void	mousePressed(MouseEvent e){
		
	}

	@Override
	public void	mouseReleased(MouseEvent e){}

	@Override
	public void paint(Graphics g){

		clear(g);

		drawLidar(g);
		drawESP32(g);
		drawESP8266(g);
		drawESP32Connected(g);
		drawESP8266Connected(g);
		drawStepperAngle(g);
		drawEncoder(g);
		drawLidarData(g);
    }

	private void clear(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
	}

	private void drawLidar(Graphics g){
		g.setColor(Color.RED);
		double angle = (LiCar.getStepper()*Math.PI/180);
		g.drawOval(WIDTH/2-50, HEIGHT/2-50, 100, 100);
		g.drawLine(WIDTH/2, HEIGHT/2, WIDTH/2 + (int)Math.round(50*(Math.cos(angle))), (int)Math.round(HEIGHT/2 + 50*(Math.sin(angle))));
	}
	private void drawESP32(Graphics g){
		g.setColor(Color.BLACK);
		g.drawString("ESP32 Data: "+LiCar.getLatestESP32Data(), 50, 55);
	}

	private void drawESP8266(Graphics g){
		g.setColor(Color.BLACK);
		g.drawString("ESP8266 Data: " +LiCar.getLatestESP8266Data(), 50, 105);
	}
	private void drawESP32Connected(Graphics g){
		if(LiCar.isESP32Connected()){
			g.setColor(Color.GREEN);
		}else{
			g.setColor(Color.RED);
		}
		g.drawRect(10, 35, 30, 30);
	}

	private void drawESP8266Connected(Graphics g){
		if(LiCar.isESP8266Connected()){
			g.setColor(Color.GREEN);
		}else{
			g.setColor(Color.RED);
		}
		g.drawRect(10, 85, 30, 30);
	}

	private void drawStepperAngle(Graphics g){
		g.setColor(Color.BLACK);
		g.drawString("Stepper Angle: " + (LiCar.getStepper()), 10, 150);
	}

	private void drawEncoder(Graphics g){
		g.setColor(Color.BLACK);
		g.drawString("Left Encoder: " + LiCar.getLeftEncoder(), 10, 200);
		g.drawString("Right Encoder: " + LiCar.getRightEncoder(), 10, 230);
	}

	private void drawLidarData(Graphics g) {
		LiCar.getLidar();


	}
}

