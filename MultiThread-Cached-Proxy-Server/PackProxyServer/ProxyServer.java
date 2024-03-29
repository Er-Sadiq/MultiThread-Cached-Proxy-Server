package PackProxyServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import PackProxyServer.filehandling.LogFileHandler;

public class ProxyServer {
    private static final int PORT = 8080;
    private static final int CACHE_CAPACITY = 100;
    private static final HashedCache cache = new HashedCache(CACHE_CAPACITY);
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LogFileHandler.log("Proxy Server Has Started\n");
            System.out.println("Proxy Server Has Started\n");
            LogFileHandler.log("Proxy server listening on port " + PORT + "...");
            System.out.println("Proxy server listening on port " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                LogFileHandler.log("New client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                System.out.println("New client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                threadPool.execute(() -> handleClientRequest(clientSocket));
            }
        } catch (IOException e) {
            handleException("Error starting server", e);
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {
            String request = getRequest(in);
            String cacheKey = extractUrl(request);

            String cachedResponse = cache.get(cacheKey);
            if (cachedResponse != null) {
                out.write(cachedResponse.getBytes());
                out.flush();
                LogFileHandler.log("Response served from cache");
                System.out.println("Response served from cache");
            } else {
                forwardRequestAndCacheResponse(request, out, cacheKey);
            }
        } catch (IOException e) {
            handleException("Error handling client request", e);
        } finally {
            closeSocket(clientSocket);
        }
    }

    private static String getRequest(BufferedReader in) throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            requestBuilder.append(inputLine).append("\n");
            if (inputLine.isEmpty()) {
                break; // End of request headers
            }
        }
        return requestBuilder.toString();
    }

    private static String extractUrl(String request) {
        String[] requestLines = request.split("\\n");
        String[] requestLineParts = requestLines[0].split(" ");
        return requestLineParts[1];
    }

    private static void forwardRequestAndCacheResponse(String request, OutputStream out, String cacheKey) {
        try {
            URL url = new URL(extractUrl(request));
            String host = url.getHost();
            int port = url.getPort() != -1 ? url.getPort() : url.getDefaultPort();

            try (Socket destSocket = new Socket(host, port);
                 OutputStream destOut = destSocket.getOutputStream();
                 InputStream destIn = destSocket.getInputStream()) {

                destOut.write(request.getBytes());
                destOut.flush();
                LogFileHandler.log("Proxy : Host-Request has been Forwarded to Destination");
                System.out.println("Proxy : Host-Request has been Forwarded to Destination");

                ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = destIn.read(buffer)) != -1) {
                    responseBuffer.write(buffer, 0, bytesRead);
                }
                byte[] responseBytes = responseBuffer.toByteArray();
                out.write(responseBytes);

                String response = new String(responseBytes);

                cache.put(cacheKey, response);
                LogFileHandler.log("Response has been put to cache");
                System.out.println("Response has been put to cache");
            }
        } catch (IOException e) {
            handleException("Error forwarding request or caching response", e);
        }
    }

    private static void closeSocket(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            handleException("Error closing client socket", e);
        }
    }

    private static void handleException(String message, Exception e) {
        LogFileHandler.log(message + ": " + e.getMessage());
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}
