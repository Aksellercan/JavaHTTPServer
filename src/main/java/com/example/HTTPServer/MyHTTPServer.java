package com.example.HTTPServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;

public class MyHTTPServer {
    private final int port;
    private final int backlog;
    private final File bodyPath = new File("Fallback");
    private final File bodyFile = new File(bodyPath + File.separator + "fallback.html");

    public MyHTTPServer(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
    }

    public void StartServer() {
        StartListening();
    }

    private String ResponseBody(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    private File GetRequestedFile(String fileNameWithExtension) {
        File requestedFilePath = new File(bodyPath + File.separator + fileNameWithExtension);
        if (!requestedFilePath.exists()) {
            throw new NullPointerException("File " + fileNameWithExtension + " not found!");
        }
        Logger.INFO.Log("File " + fileNameWithExtension + " found.");
        return requestedFilePath;
    }

    private void StartListening() {
        ServerSocket socket = null;
        try {
            Logger.INFO.Log("Starting Server on port " + port);
            socket = new ServerSocket(port, backlog);

            while (true) {
                Socket clientSocket = socket.accept();

                Logger.INFO.Log("Waiting for requests... Listening on Port: " + clientSocket.getLocalPort() + " at address: " + clientSocket.getLocalSocketAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String requestType = in.readLine();
                Logger.INFO.Log("Request: " + requestType);

                String body = "";
                StringBuilder requestedFile = new StringBuilder();
                while (requestType != null) {
                    if (requestType.isEmpty()) {
                        break;
                    }
                    if (requestType.contains("GET")) {
                        for (int i = 5; i < requestType.length(); i++) {
                            if (requestType.charAt(i) == ' ') {
                                break;
                            }
                            requestedFile.append(requestType.charAt(i));
                        }
                        if (!requestedFile.isEmpty()) {
                            body = ResponseBody(GetRequestedFile(requestedFile.toString()), StandardCharsets.UTF_8);
                        }
                        Logger.DEBUG.Log("GET file: " + requestedFile.toString());
                    }
                    break;
                }

                String clientInputLine;
                while ((clientInputLine = in.readLine()) != null) {
                    if (clientInputLine.isEmpty()) {
                        break;
                    }
                    Logger.INFO.Log("Request Headers: " + clientInputLine);
                }

                Logger.DEBUG.Log("Sending the body");

                if (requestedFile.toString().contains("css")) SendStylesheet(out, body);
                if (requestedFile.toString().isBlank()) {
                    String htmlBody = ResponseBody(bodyFile, StandardCharsets.UTF_8);
                    SendHTML(out, htmlBody);
                }
                if (requestedFile.toString().contains("js")) SendScript(out, body);
                if (requestedFile.toString().contains("ico")) SendImage(out, body);

                Logger.DEBUG.Log("the end of loop");
            }

        } catch (Exception ex) {
            Logger.ERROR.LogException(ex, "Port " + port + " backlog limit: " + 10);
        } finally {
            try {
                if (socket == null) {
                    throw new NullPointerException("Socket Object is NULL");
                }
                socket.close();
                System.gc();
            } catch (Exception ex) {
                Logger.ERROR.LogException(ex, "Cannot close socket");
            }
        }
    }

    private void SendScript(BufferedWriter out, String body) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 200 OK\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: Custom Server\r\n");
        out.write("Content-Type: text/javascript\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }

    private void SendStylesheet(BufferedWriter out, String body) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 200 OK\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: Custom Server\r\n");
        out.write("Content-Type: text/css\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }

    private void SendHTML(BufferedWriter out, String body) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 200 OK\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: Custom Server\r\n");
        out.write("Content-Type: text/html\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }

    private void SendImage(BufferedWriter out, String body) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 200 OK\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: Custom Server\r\n");
        out.write("Content-Type: image/vnd.microsoft.icon\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }
}
