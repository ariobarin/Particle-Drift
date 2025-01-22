/* Lidar8266.cpp
 * Kevin Dang
 * Read data from Lidar
 */



#include "Lidar8266.h"



Lidar8266::Lidar8266(int rx, int tx){

  tally = 0;
  startMillis = millis();

  softwareSerial = new SoftwareSerial(rx, tx); //if using software serial

  softwareSerial->begin(115200);
  software = true;

}

Lidar8266::Lidar8266(){
  tally = 0;
  startMillis = millis();
  software = false;//if using hardware serial
}

String Lidar8266::read(){
 
    currMillis = millis(); //get current time
    byte data[9]; //byte array to store data
    tally++;//increment tally to keep track of number of readings
    updateRate(); //update rate with tally and time

    if(software && softwareSerial->available() && softwareSerial->available() >= 9){ //if using software serial and data is available

        for (int i = 0; i < 9; i++) {
        data[i] = softwareSerial->read(); //read data
        }

        clearSerialBuffer(); //clear serial buffer to prevent overflow

        int fullDistance = (data[3] << 8) | data[2]; //combine high and low bytes to get distance
        String distance = String(fullDistance); //convert distance to string
        return (distance); 
    }

    else if (!software && Serial.available() && Serial.available() >= 9){ //if using hardware serial and data is available
        for (int i = 0; i < 9; i++) {
        data[i] = Serial.read();
        }

        clearSerialBuffer();

        int fullDistance = (data[3] << 8) | data[2];
        String distance = String(fullDistance);
        return (distance);
    }//same logic

    tally--; //decrement tally if no data is available
    return "-1"; //return -1 if no data is available

}

void Lidar8266::clearSerialBuffer() { //clear serial buffer
    if(software){//if using software serial
        while (softwareSerial->available() > 0) {
        softwareSerial->read();  
        }//read data to clear buffer
    }
    else{
        while (Serial.available() > 0) { //if using hardware serial
        Serial.read();  
        }
    }
}

void Lidar8266::updateRate(){
    rate = (float)tally * 1000 / (currMillis - startMillis); 
  //calculate rate by dividing number of readings by time
  if (currMillis-startMillis > 1000){
    startMillis = currMillis;
    tally = 0;
  }

}

double Lidar8266::getRate(){
    return rate;
}//get rate of readings