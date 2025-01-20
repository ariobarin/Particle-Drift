import java.io.*;
import java.net.*;

//import threads

public class SocketClient  {

    private Socket client;
    private PrintWriter out;
    private BufferedReader in;


    public SocketClient(String ip, int port) throws IOException {

        startConnection(ip, port);

    }
    public void startConnection(String ip, int port) throws IOException {
        
        client = new Socket(ip, port);
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
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
            return "-+-";
        }
    }
    
    public void stopConnection() throws IOException {

        in.close();
        out.close();
        client.close();

    }
}