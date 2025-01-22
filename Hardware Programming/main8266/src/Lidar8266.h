/* Lidar8266.h
 * Kevin Dang
 * Header file for Lidar8266.cpp
 */





#ifndef Lidar8266_h
#define Lidar8266_h


#include <Arduino.h>
#include <SoftwareSerial.h>


class Lidar8266 {
  public:

    Lidar8266(int rx, int tx); //constructor for software serial
    Lidar8266(); //constructor for hardware serial

    String read(); 
    double getRate();

  private:

    void clearSerialBuffer();
    void updateRate();

    boolean software;

    int tally;
    unsigned long startMillis; //start time for rate calculation
    unsigned long currMillis;
    double rate;

    SoftwareSerial* softwareSerial; //points to software serial object

};


#endif