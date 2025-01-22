
/* Socket8266.cpp
 * Kevin Dang
 * send and receive data from client
*/



#include "Socket8266.h"


Socket8266::Socket8266(){} 


void Socket8266::connectWifi(String ssid, String password){

  WiFi.begin(ssid, password);
  pinMode(2, OUTPUT); //LED_BUILTIN output
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }

  Serial.println("Connected to the WiFi network");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP()); //print IP address

  server.begin();
}

void Socket8266::begin(){
  while (!client){
  client = server.accept();
  digitalWrite(2, HIGH);
  delay(200); //flash LED_BUILTIN
  digitalWrite(2, LOW);
  delay(200);
  }
  if (client){
    Serial.println("Client connected.");
  }
  else{
    Serial.println("Client did not begin.");
  }

}

bool Socket8266::send(String data){ //send data to client
  if(client){
    client.println(data);
    return true; 
  }
  else{
    Serial.println("Client not connected.");
    return false;
  }
}

String Socket8266::receive() {//receive data from client
    if (client.available()) {
        char c = client.read(); //only receives one char for speed


        return String(c); 
    
    return "";  
}
}

void Socket8266::end(){
  if (client ){
    client.stop(); //stop the client
    Serial.println("Client disconnected.");
  }
}

bool Socket8266::isConnected(){
  return client; //return if client is connected
}




