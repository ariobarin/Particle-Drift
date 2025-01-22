/*
 * CarSocket.java
 * Kevin Dang
 * Connects to the ESP32 and ESP8266 to control the car and receive lidar data
 */
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

class CarSocket {

    private Scanner kb;
    private Timer timer;

    public static final int STEPS = 200; // steps per revolution
    public static final int RESOLUTION = 40; // scans per revolution
    public static final int DATAPERPACKET = 10; // data per packet
    public static final int SPS = 10; // scans per second

    public static final int RECEIVE_MS = 5;
    public static final int SEND_MS = 2000;
    // send and receive milliseconds
    private boolean ESP32Connected = false;
    private boolean ESP8266Connected = false;
    private boolean CarConnected = false;
    // connection status
    private volatile boolean running = false;

    private SocketClient ESP32;
    private SocketClient ESP8266;
    // socket clients for ESP32 and ESP8266
    private double stepperAngle;
    private double nextStepperAngle;
    // estimated stepper angle
    private int millisPassed;
    private int prevMillisPassed;
    // keep track of time
    private boolean[] moveArray = new boolean[4];
    // movement array to know which direction the car is moving
    public static final int FORWARD = 0, BACKWARD = 1, LEFT = 2, RIGHT = 3;
    // movement directions
    private static final int WHEELRPM = 178;
    private static final int ENCODERTICKS = 20;
    // wheel rpm and encoder ticks
    private double leftEncoder = 0;
    private double rightEncoder = 0;
    // encoder values
    private List<MyVector> lidarQueue = new ArrayList<>();
    // list of lidar data
    public static final int LIDAR_MIN = 1, LIDAR_MAX = 800;
    // lidar min and max values
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
        // disconnect from the car if already connected
        try {
            ESP32 = new SocketClient(ip32, port32);
            ESP32Connected = true;// connect to the ESP32

            ESP8266 = new SocketClient(ip8266, port8266);
            ESP8266Connected = true;    // connect to the ESP8266

            System.out.println("connected");

            CarConnected = true;
            running = true;

            startTimerThread();
            startESP32DataThread();
            startESP8266DataThread();
            startStepperControlThread();
            startEncoderUpdateThread();
            // start the threads
        } catch (IOException e) {
            System.out.println("Connection failed");
        }
    }

    private void startTimerThread() { // keep track of milliseconds passed
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

    private void startESP32DataThread() { // process data from ESP32
        new Thread(() -> {
            while (running) {

                processESP32();

            }
        }).start();
    }

    private void startESP8266DataThread() { // process data from ESP8266
        new Thread(() -> {
            while (running) {

                processESP8266();

            }
        }).start();
    }

    private void startStepperControlThread() { // estimate stepper angle
        new Thread(() -> {
            long lastTime = System.nanoTime();
            double stepInterval = 1_000_000_000.0 / SPS;

            while (running) {
                long currentTime = System.nanoTime(); //get current time in nanoseconds
                if (currentTime - lastTime >= stepInterval) { //if time passed is greater than step interval
                    stepperAngle += (360.0 / RESOLUTION); //increment stepper angle
                    stepperAngle %= 360; //keep angle between 0 and 360
                    lastTime += stepInterval; //update last time
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

                estimateEncoder(millisPassed - prevMillisPassed); //estimate encoder values
                prevMillisPassed = millisPassed; //based on milliseconds passed
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
            if (ESP32Connected) { //disconnect from ESP32
                ESP32.stopConnection();
                ESP32Connected = false;
            }
            if (ESP8266Connected) { //disconnect from ESP8266
                ESP8266.stopConnection();
                ESP8266Connected = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        CarConnected = false; //disconnect from car
    }

    private void processESP32() {
        try {
            String data = ESP32.getMessage(); //get message from ESP32

            if (data.startsWith("%")) { //if message starts with % it's a stepper angle
                latestESP32Data = data;
                stepperAngle = (Double.parseDouble(data.substring(1)));
            }
        } catch (IOException e) {

        }
    }

    private void processESP8266() {
        try {
            String data = ESP8266.getMessage(); //get message from ESP8266

            if (data.startsWith("&")) { //if message starts with & it's lidar data
                System.out.println(data);

                latestESP8266Data = data;

                data = data.substring(1, data.length() - 1); //remove & and | from data
                String[] dataArr = data.split("\\|"); //split data by |
                for (int i = 0; i < dataArr.length; i++) { //add data to lidar queue
                    int value = Integer.parseInt(dataArr[i]);
                    if (value <= LIDAR_MIN || value >= LIDAR_MAX) {
                        continue;
                    }
                    lidarQueue.add(new MyVector(Math.toRadians(nextStepperAngle)+i*(360/STEPS)/RESOLUTION, Double.parseDouble(dataArr[i])));
                } //add vectors to lidar queue, estimate angle based on stepper angle
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
  //getter methods  

    public List<MyVector> getLidar() {
        List<MyVector> lidar = new ArrayList<>(lidarQueue);
        lidarQueue.clear();
        return lidar;
    } //get lidar data

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
    //methods to control car movement
    public boolean moving() {
        for (int i = 0; i < 4; i++) {
            if (moveArray[i]) {
                return true; //check if car is moving
            }
        }
        return false;
    }

    private void estimateEncoder(int millis) { //estimate encoder values based on time passed
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

    public int getLeftEncoder() { //get left encoder value
        return (int) Math.round(leftEncoder); 
    }

    public int getRightEncoder() { //get right encoder value
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
    } //getter methods

    public static void main(String[] args) {

    }
}
