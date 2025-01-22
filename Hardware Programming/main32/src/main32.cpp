
/* main32.cpp
 * Kevin Dang
 * Controls the movement of the car, sends and receives data from the server
 */

#include <Arduino.h>
#include "Socket8266.h"
#include "Motor.h"
#include "Stepper32.h"
//custom libraries
const char* ssid = "";      
const char* password = "";      
const int port = 1024;              


//wifi info 

String motorMovement = "$";

unsigned long universalMillis;
unsigned long ESPPrevmillis;
unsigned long ESPCurrmillis;
//keep track of time
const int LForward = 18;
const int LBackward = 19;

const int RForward = 4;
const int RBackward = 16;

const int REncoderA = 34;
const int REncoderB = 35;

const int LEncoderA = 32;
const int LEncoderB = 33;

const int EncoderTicks = 20;

const int dirPin = 25;
const int stepPin = 26;
const int stepsperRev = 200;

//pins for motors, encoders, and stepper motor

const int lidarResolution = 100; //scan per rotation
const int sps = 50; //scans per second

const int SECOND = 1000; //milliseconds in a second

int lidarTally = 0; //keep track of lidar packets sent
int receiveTally = 0; //keep track of receive packets

int encoderSent = 0; //keep track of encoder packets sent
int encoderPacketInterval = 1000; //interval to send encoder packets

int stepperSent = 0; //keep track of stepper packets sent
int stepperPacketInterval = 250; //interval to send stepper packets

int receiveMillis = 20; //interval to receive data

int motorGetMillis = 500; //interval to get motor data
int motorGetTally = 0;

boolean driving = false;

Socket8266 socket;

Motor leftMotor = Motor(LForward, LBackward, LEncoderA, LEncoderB, EncoderTicks);
Motor rightMotor = Motor(RForward, RBackward, REncoderA, REncoderB, EncoderTicks);

Stepper32 stepper = Stepper32(dirPin, stepPin, stepsperRev);
//initialize motors and stepper

void setup() {

  Serial.begin(115200);

  socket.connectWifi(ssid, password);
  // socket.begin();

  ESPCurrmillis = millis();
  ESPPrevmillis = millis();
  

  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, LOW);
  //initialize LED_BUILTIN
  universalMillis = 0;
  stepper.setDelayPerStep(2000);
  //initialize variables

}



void rotateLeft(){ 
  leftMotor.run(Motor::CLOCKWISE);
  rightMotor.run(Motor::CLOCKWISE);
} 
void rotateRight(){
  leftMotor.run(Motor::COUNTERCLOCKWISE);
  rightMotor.run(Motor::COUNTERCLOCKWISE);
}

void moveForward(){
  leftMotor.run(Motor::COUNTERCLOCKWISE);
  rightMotor.run(Motor::CLOCKWISE);
}
void moveBackward(){
  leftMotor.run(Motor::CLOCKWISE);
  rightMotor.run(Motor::COUNTERCLOCKWISE);
}

void stop(){
  leftMotor.stop();
  rightMotor.stop();
} //stop the motors

void processInput(String input) {

  if (input.isEmpty()){
    return;
  }

  if (input == "*") {
    universalMillis = input.substring(1).toInt();
  }

  if (input == "w"){ //if input is w, move forward
    driving = true;
    moveForward();
    Serial.println("Moving Forward");
  }
  else if(input== "s"){ //if input is s, move backward
    driving = true;
    moveBackward();
    Serial.println("Moving Backward");

  }
  else if(input == "d"){ //if input is d, rotate right
    driving = true;
    rotateRight();
    Serial.println("Rotating Right");

  }
  else if(input== "a"){ //if input is a, rotate left
    driving = true;
    rotateLeft();
    Serial.println("Rotating Left");
  }
  else if (input == "x"){ //if input is x, stop
    driving = false;
    stop();
  }
}


void loop() {
  
  if (!socket.isConnected()){
    digitalWrite(LED_BUILTIN, LOW);
    stop(); //stop the motors

    socket.begin();// start the server

    universalMillis = 0;
    lidarTally = 0;
    encoderSent = 0;
    stepperSent = 0;
    receiveTally = 0;
    motorGetTally = 0;
  }



  digitalWrite(LED_BUILTIN, HIGH);

  // // leftMotor.updateEncoder();
  // // rightMotor.updateEncoder();

  ESPCurrmillis = millis();
  universalMillis += (ESPCurrmillis-ESPPrevmillis);
  //update time

  if (universalMillis/receiveMillis > receiveTally) { //if time to receive data ( every receive millis )
            String data = socket.receive(); 
            if (!data.isEmpty()) { //if data is not empty
                Serial.println(data + " | " + String(universalMillis));
                processInput(data); //process the data 
            
        }
        receiveTally++;
    }



  if ((universalMillis / (SECOND/sps) > lidarTally) && !driving) { //if time to send lidar data
    stepper.turnSteps(stepsperRev / lidarResolution); //turn the stepper motor
    lidarTally=universalMillis/(SECOND/sps); //update lidarTally
  }
  

  if ((universalMillis / stepperPacketInterval) > stepperSent){ //if time to send stepper data
    socket.send("%"+String(stepper.getAngle())); //send the angle of the stepper motor
    stepperSent=universalMillis/stepperPacketInterval; //update stepperSent
    digitalWrite(LED_BUILTIN, LOW); 
  }

  //
  ESPPrevmillis = ESPCurrmillis;
  //update time
}


