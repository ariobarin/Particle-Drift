/* SocketClient.java
 * 
 * Kevin Dang
 * 
 * class to connect to the server socket
 */


import java.io.*;
import java.net.*;


public class SocketClient  {

    private Socket client;
    private PrintWriter out;
    private BufferedReader in;


    public SocketClient(String ip, int port) throws IOException {

        startConnection(ip, port); 

    }
    public void startConnection(String ip, int port) throws IOException {
        
        client = new Socket(ip, port); // connect to the server
        out = new PrintWriter(client.getOutputStream(), true); // create an output stream to send messages to the server
        in = new BufferedReader(new InputStreamReader(client.getInputStream())); // create an input stream to receive messages from the server
        System.out.println("Connected to server");

    }

    public void sendMessage(String msg) {

        out.println(msg);

    }

    public String getMessage() throws IOException {
        String msg;
        if(in.ready()){
            msg = in.readLine();
            return msg;
        }
        else{
            return "-+-"; // return a string that will not be a valid message
        }
    }
    
    public void stopConnection() throws IOException {

        in.close();
        out.close();
        client.close();
        // close the input and output streams and the socket
    }
}