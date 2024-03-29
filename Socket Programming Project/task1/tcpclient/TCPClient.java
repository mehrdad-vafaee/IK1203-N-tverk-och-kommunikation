package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 1024;
    
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        byte[] fromServerBuffer = new byte[BUFFERSIZE];
        ByteArrayOutputStream fromServerBAOS = new ByteArrayOutputStream();
        Socket clientSocket = new Socket(hostname, port);
        clientSocket.getOutputStream().write(toServerBytes);
        // Some type of loop so we're continuosly reading from the server.
        boolean loop = true;
        while (loop){
            int inputLength = clientSocket.getInputStream().read(fromServerBuffer);
            if (inputLength == -1) 
                loop = false;
            else 
                fromServerBAOS.write(fromServerBuffer, 0, inputLength);
        }
        clientSocket.close();
        return fromServerBAOS.toByteArray();
    }
}
