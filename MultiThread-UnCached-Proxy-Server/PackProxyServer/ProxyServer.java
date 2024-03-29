package PackProxyServer; // Package declaration should come first
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Date;
import PackProxyServer.filehandling.LogFileHandler;

public class ProxyServer {
    private static final int PORT = 8080;
    public static void main(String[] args) {
        // Setting Up The Server
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {


            LogFileHandler.log("Proxy Server Has Started\n");
            System.out.println("Proxy Server Has Started\n");
            LogFileHandler.log("Proxy server listening on port " + PORT + "...");
            System.out.println("Proxy server listening on port " + PORT + "...");


            while (true) {
                Socket clientSocket = serverSocket.accept();

                LogFileHandler.log("New client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                System.out.println("New client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                // Handle client request in a separate thread
                new Thread(() -> handleClientRequest(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
     //Handle the clint Request
    private static void handleClientRequest(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {
            StringBuilder requestBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                requestBuilder.append(inputLine).append("\n");
                if (inputLine.isEmpty()) {
                    break; // End of request headers
                }
            }

            String request = requestBuilder.toString();

            // Parse request to extract destination host and port 
            String[] requestLines = request.split("\\n");
            String[] requestLineParts = requestLines[0].split(" ");
            String method = requestLineParts[0];
            String urlString = requestLineParts[1];
            URL url = new URL(urlString);
            String host = url.getHost();
            int port = url.getPort() != -1 ? url.getPort() : 80;
            LogFileHandler.log("Proxy : Intercepting Host: Port-Num:" + port + " Host Request:" + host + "\n");
            System.out.println("Proxy : Intercepting Host: Port-Num:"+port+" Host Request:"+host+"\n");

            // Forward the request to the destination server
            try (Socket destSocket = new Socket(host, port);
                 OutputStream destOut = destSocket.getOutputStream();
                 BufferedReader destIn = new BufferedReader(new InputStreamReader(destSocket.getInputStream()))
            ) {
                destOut.write(request.getBytes());
                destOut.flush();
                LogFileHandler.log("Proxy : Host-Request has been Forwarded to Destination");
                System.out.println("Proxy : Host-Request has been Fowarded to Desination");
                // Forward the response from the destination server back to the client
                String responseLine;
                while ((responseLine = destIn.readLine()) != null) {
                    out.write(responseLine.getBytes());
                    out.write("\n".getBytes());

                    LogFileHandler.log("Response : " + responseLine);
                    System.out.println("Response : "+responseLine);
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
