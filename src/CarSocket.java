import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

class CarSocket {

    private Scanner kb;
    private Timer timer;

    public static final int STEPS = 200;
    public static final int RESOLUTION = 40;
    public static final int DATAPERPACKET = 10;
    public static final int SPS = 10;

    public static final int RECEIVE_MS = 5;
    public static final int SEND_MS = 2000;

    private boolean ESP32Connected = false;
    private boolean ESP8266Connected = false;
    private boolean CarConnected = false;

    private volatile boolean running = false;

    private SocketClient ESP32;
    private SocketClient ESP8266;

    private double stepperAngle;
    private double nextStepperAngle;

    private int millisPassed;
    private int prevMillisPassed;

    private boolean[] moveArray = new boolean[4];

    public static final int FORWARD = 0, BACKWARD = 1, LEFT = 2, RIGHT = 3;

    private static final int WHEELRPM = 178;
    private static final int ENCODERTICKS = 20;

    private double leftEncoder = 0;
    private double rightEncoder = 0;

    private List<MyVector> lidarQueue = new ArrayList<>();

    public static final int LIDAR_MIN = 1, LIDAR_MAX = 800;

    String latestESP32Data = "";
    String latestESP8266Data = "";

    public CarSocket() {
        kb = new Scanner(System.in);
        timer = new Timer();
        stepperAngle = 0;
        millisPassed = 0;

    }

    public void connectCar(String ip32, int port32, String ip8266, int port8266) {

        disconnect();

        try {
            ESP32 = new SocketClient(ip32, port32);
            ESP32Connected = true;

            ESP8266 = new SocketClient(ip8266, port8266);
            ESP8266Connected = true;

            System.out.println("connected");

            CarConnected = true;
            running = true;

            startTimerThread();
            startESP32DataThread();
            startESP8266DataThread();
            startStepperControlThread();
            startEncoderUpdateThread();
        } catch (IOException e) {
            System.out.println("Connection failed");
        }
    }

    private void startTimerThread() {
        new Thread(() -> {
            while (running) {

                millisPassed += 1;

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

    }

    private void startESP32DataThread() {
        new Thread(() -> {
            while (running) {

                processESP32();

            }
        }).start();
    }

    private void startESP8266DataThread() {
        new Thread(() -> {
            while (running) {

                processESP8266();

            }
        }).start();
    }

    private void startStepperControlThread() {
        new Thread(() -> {
            long lastTime = System.nanoTime();
            double stepInterval = 1_000_000_000.0 / SPS;

            while (running) {
                long currentTime = System.nanoTime();
                if (currentTime - lastTime >= stepInterval) {
                    stepperAngle += (360.0 / RESOLUTION);
                    stepperAngle %= 360;
                    lastTime += stepInterval;
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void startEncoderUpdateThread() {
        new Thread(() -> {
            while (running) {

                estimateEncoder(millisPassed - prevMillisPassed);
                prevMillisPassed = millisPassed;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void disconnect() {

        running = false;

        try {
            if (ESP32Connected) {
                ESP32.stopConnection();
                ESP32Connected = false;
            }
            if (ESP8266Connected) {
                ESP8266.stopConnection();
                ESP8266Connected = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        CarConnected = false;
    }

    private void processESP32() {
        try {
            String data = ESP32.getMessage();

            if (data.startsWith("%")) {
                latestESP32Data = data;
                stepperAngle = (Double.parseDouble(data.substring(1)));
            }
        } catch (IOException e) {

        }
    }

    private void processESP8266() {
        try {
            String data = ESP8266.getMessage();

            if (data.startsWith("&")) {
                System.out.println(data);

                latestESP8266Data = data;

                data = data.substring(1, data.length() - 1);
                String[] dataArr = data.split("\\|");
                for (int i = 0; i < dataArr.length; i++) {
                    int value = Integer.parseInt(dataArr[i]);
                    if (value <= LIDAR_MIN || value >= LIDAR_MAX) {
                        continue;
                    }
                    lidarQueue.add(new MyVector(Math.toRadians(nextStepperAngle)+i*(360/STEPS), Double.parseDouble(dataArr[i])));
                }
                nextStepperAngle = stepperAngle;
            }
        } catch (IOException e) {
            // Handle exception if needed
        }
    }

    public boolean isESP32Connected() {
        return ESP32Connected;
    }

    public boolean isESP8266Connected() {
        return ESP8266Connected;
    }

    public boolean isLidarEmpty() {
        return lidarQueue.isEmpty();
    }

    public void LidarClear() {
        lidarQueue.clear();
    }

    public List<MyVector> getLidar() {
        List<MyVector> lidar = new ArrayList<>(lidarQueue);
        lidarQueue.clear();
        return lidar;
    }

    public void moveForward() {
        if (moving()) {
            return;
        }
        ESP32.sendMessage("w");
        LidarClear();
        moveArray[FORWARD] = true;
    }

    public void moveBackward() {
        if (moving()) {
            return;
        }
        ESP32.sendMessage("s");
        LidarClear();
        moveArray[BACKWARD] = true;
    }

    public void turnLeft() {
        if (moving()) {
            return;
        }
        ESP32.sendMessage("a");
        LidarClear();
        moveArray[LEFT] = true;
    }

    public void turnRight() {
        if (moving()) {
            return;
        }
        ESP32.sendMessage("d");
        LidarClear();
        moveArray[RIGHT] = true;
    }

    public void stopMovement() {
        ESP32.sendMessage("x");
        LidarClear();
        for (int i = 0; i < 4; i++) {
            moveArray[i] = false;
        }
    }

    public boolean moving() {
        for (int i = 0; i < 4; i++) {
            if (moveArray[i]) {
                return true;
            }
        }
        return false;
    }

    private void estimateEncoder(int millis) {
        if (moveArray[FORWARD]) {
            leftEncoder += (millis / 1000.0 * WHEELRPM * ENCODERTICKS) / 60.0;
            rightEncoder += (millis / 1000.0 * WHEELRPM * ENCODERTICKS) / 60.0;
        } else if (moveArray[BACKWARD]) {
            leftEncoder -= (millis / 1000.0 * WHEELRPM * ENCODERTICKS) / 60.0;
            rightEncoder -= (millis / 1000.0 * WHEELRPM * ENCODERTICKS) / 60.0;
        } else if (moveArray[LEFT]) {
            leftEncoder -= (millis / 1000.0 * WHEELRPM * ENCODERTICKS) / 60.0;
            rightEncoder += (millis / 1000.0 * WHEELRPM * ENCODERTICKS) / 60.0;
        } else if (moveArray[RIGHT]) {
            leftEncoder += (millis / 1000.0 * WHEELRPM * ENCODERTICKS) / 60.0;
            rightEncoder -= (millis / 1000.0 * WHEELRPM * ENCODERTICKS) / 60.0;
        }

    }

    public int getLeftEncoder() {
        return (int) Math.round(leftEncoder);
    }

    public int getRightEncoder() {
        return (int) Math.round(rightEncoder);
    }

    public boolean connected() {
        return CarConnected;
    }

    public double getStepper() {
        return stepperAngle;
    }

    public String getLatestESP32Data() {
        return latestESP32Data;
    }

    public String getLatestESP8266Data() {
        return latestESP8266Data;
    }

    public static void main(String[] args) {

    }
}
