/* main8266.cpp 
 * Kevin Dang
 * Reads Lidar, creates wifi server, sends data to client
*/



#include "Socket8266.h"
#include "Lidar8266.h"

String ssid = "Ario";
String password = "ArioBario@0";

// String ssid = "Virgin AIG_EXT";
// String password = "ginggiang0";

int port = 80;
int PACKET_SIZE = 10; //number of readings per packet
int SPS = 10; //scans per second

unsigned long currMillis;
unsigned long prevMillis; 
long universalMillis = 0;
//time to keep track of events

int SECOND = 1000;

int millisPerScan = (SECOND/SPS); //milliseconds to wait per scan
int millisPerPacket = millisPerScan*PACKET_SIZE; //milliseconds to wait per packet


int receiveMillis = 100;
//time to wait for client to receive data


int lidarTally = 0;
int receiveTally = 0;
int sendTally = 0;

String lidarReadings = "&"; //string to store lidar readings

Socket8266 socket;
Lidar8266 lidar; 
//initialize socket and lidar objects

void setup() {
  

  Serial.begin(115200);
  socket.connectWifi(ssid, password); //connect to wifi
  socket.begin();//begin server

  currMillis = millis();
  prevMillis = millis();
  universalMillis = 0;

}




void loop() {
  if (!socket.isConnected()) { //if client is disconnected
    Serial.println("Client disconnected.");
    socket.begin();

    lidarTally = 0;
    receiveTally = 0;
    sendTally = 0;
    universalMillis = 0;
  }

  digitalWrite(2, HIGH); //turned off LED_BUILTIN when HIGH
  currMillis = millis();

  universalMillis += (currMillis-prevMillis);

  if(universalMillis/millisPerScan > lidarTally){ //if time to read lidar
    lidarReadings+=lidar.read()+"|";
    lidarTally++;
  }


  if(universalMillis/millisPerPacket > sendTally){ //if time to send data
    digitalWrite(2, LOW); //flashes on when sending data
    socket.send(lidarReadings); 
    Serial.println(lidarReadings);
    lidarReadings = "&"; //reset string
    sendTally=universalMillis/millisPerPacket;
  }
 
  prevMillis = currMillis;

}


