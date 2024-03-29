import java.net.*;
import java.io.*;
import tcpclient.TCPClient;



public class ConcHTTPAsk {
    static int BUFFERSIZE = 1024;
    static String CRLF = "\n\r";
    static String HTTP200 = "HTTP/1.1 200 OK" + CRLF;
    static String HTTP400 = "HTTP/1.1 400 BAD REQUEST" + CRLF;
    static String HTTP404 = "HTTP/1.1 404 NOT FOUND" + CRLF;

    public static class MyRunnable implements Runnable {

        Socket connectionSocket;

        public MyRunnable(Socket connectionSocket){
            this.connectionSocket = connectionSocket;
        }

        public void run(){
            try {
                byte[] fromClientBuffer = new byte[BUFFERSIZE];
                ByteArrayOutputStream fromClientBAOS = new ByteArrayOutputStream();
        
                while (true) {
                    int fromClientLength = connectionSocket.getInputStream().read(fromClientBuffer);
                    fromClientBAOS.write(fromClientBuffer, 0, fromClientLength);
                    if (fromClientBAOS.toString().contains("\n")) {
                        break;
                    }
                }
                
                String response = parser(fromClientBAOS);
                
                byte[] toClientBuffer = response.getBytes();
                connectionSocket.getOutputStream().write(toClientBuffer);
        
                connectionSocket.close();
            } catch (IOException e) {
                System.out.println("Socket error!");
            }

        }
    }

    private static String parser(ByteArrayOutputStream fromClientBAOS) {
        byte[] fromServerBuffer = new byte[BUFFERSIZE];
        boolean shutdown = false;
        Integer timeout = null;
        Integer limit = null;
        String hostname = null;
        Integer serverPort = null;
        String response = HTTP200;
        byte[] toServerBytes = new byte[0];

        String fromClientString = fromClientBAOS.toString();
        String[] fromClientStringSplit = fromClientString.split("\n"); 
        String requestLine = fromClientStringSplit[0];
        String[] requestLineSplit = requestLine.split(" ");
        String URI = requestLineSplit[1];

        if (!requestLineSplit[2].trim().equals("HTTP/1.1") || !requestLineSplit[0].trim().equals("GET")) {
            response = HTTP400;
        }
        else if (!URI.startsWith("/ask")) {
            response = HTTP404;
        }
        else{
            try {
                URI = URI.replaceAll("/ask\\?", "");
                String[] URISplit = URI.split("&");
                for (String string : URISplit) {
                    if (string.startsWith("hostname=")) {
                        string = string.substring("hostname=".length());
                        hostname = string;
                    }
                    else if (string.startsWith("port=")) {
                        string = string.substring("port=".length());
                        serverPort = Integer.parseInt(string);
                    }
                    else if (string.startsWith("limit=")) {
                        string = string.substring("limit=".length());
                        limit = Integer.parseInt(string);
                    }
                    else if (string.startsWith("shutdown=")) {
                        string = string.substring("shutdown=".length());
                        shutdown = Boolean.parseBoolean(string);
                    }
                    else if (string.startsWith("timeout=")) {
                        string = string.substring("timeout=".length());
                        timeout = Integer.parseInt(string);
                    }
                    else if (string.startsWith("string=")) {
                        string = string.substring("string=".length());
                        toServerBytes = string.getBytes();
                    }
                    else
                        response = HTTP404;
                }
            } catch (NumberFormatException e) {
                response = HTTP404;
            }
            
            TCPClient client = new TCPClient(shutdown, timeout, limit);
            try {
                fromServerBuffer = client.askServer(hostname, serverPort, toServerBytes);
                String fromServerString = new String(fromServerBuffer);
                fromServerString = fromServerString.replaceAll("%20", " ");
                response = response + CRLF + fromServerString;
            } catch (Exception e) {
                response = HTTP404;
            }
        }
        return response;
    }

    public static void main( String[] args) throws Exception {
        try {
            Integer welcomePort = Integer.parseInt(args[0]);
            ServerSocket welcomeSocket = new ServerSocket(welcomePort); 
            System.err.println("Server is running on port: " + welcomePort);
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                Runnable r = new MyRunnable(connectionSocket);
                new Thread(r).start();
            }
        } catch (Exception e) {
            System.err.println("Bad argument!");
        }
    }
}

