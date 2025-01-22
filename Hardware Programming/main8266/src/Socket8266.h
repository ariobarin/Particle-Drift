/*Socket8266.h 

Kevin Dang

Header file for Socket8266.cpp
*/



#ifndef Socket8266_h
#define Socket8266_h

#include <Arduino.h>
#include <ESP8266WiFi.h>

class Socket8266 {
  public:

    Socket8266();

    void connectWifi(String ssid, String password); //connect to wifi
    void begin(); //begin server
    void end(); //end server

    bool isConnected(); //check if client is connected
    bool send(String data); //send data to client
    String receive(); //receive data from client
//public methods
  private:

    WiFiServer server = WiFiServer(80);
    WiFiClient client;
    //private variables
};

#endif
