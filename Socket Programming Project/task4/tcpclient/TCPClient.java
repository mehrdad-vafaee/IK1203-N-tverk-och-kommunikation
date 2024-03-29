package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private static int BUFFERSIZE = 1024;
    private boolean shutdown;
    private Integer timeout;
    private Integer limit;
    
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        byte[] fromServerBuffer = new byte[BUFFERSIZE];
        ByteArrayOutputStream fromServerBAOS = new ByteArrayOutputStream();
        Socket clientSocket = new Socket(hostname, port);
        int inputLength = 0;

        if (timeout != null) {
            clientSocket.setSoTimeout(timeout);
        }

        clientSocket.getOutputStream().write(toServerBytes);
        
        if (shutdown) {
            clientSocket.shutdownOutput();
        }

        while (true){
            try {
                inputLength = clientSocket.getInputStream().read(fromServerBuffer);
            } catch (SocketTimeoutException e) {
                System.err.println("Timeout!!");
                break;
            }
            if (inputLength == -1) 
                break;
            else if(limit != null && fromServerBAOS.size() + inputLength >= limit){
                fromServerBAOS.write(fromServerBuffer, 0, limit - fromServerBAOS.size());
                break;
            }
            else
                fromServerBAOS.write(fromServerBuffer, 0, inputLength);
                
        }
        clientSocket.close();
        return fromServerBAOS.toByteArray();
    }
}
