import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;


public class HTTPServer {
    private final int port;
    private final int backlog;
    private File sourceFolder = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
    private String serverName = "JavaHTTPServer";
    private ServerSocket socket = null;

    public HTTPServer(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public void setSourceFolder(File sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    public void StartServer() {
        StartListening();
    }

    private String ResponseBody(File file, Charset encoding) throws IOException {
        if (file == null) {
            return "";
        }
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    private File GetRequestedFile(String fileNameWithExtension) {
        File requestedFilePath = new File(sourceFolder + File.separator + fileNameWithExtension);
        if (!requestedFilePath.exists()) {
            Logger.WARN.Log("File " + fileNameWithExtension + " not found!");
            return null;
        }
        Logger.INFO.Log("File " + fileNameWithExtension + " found.");
        return requestedFilePath;
    }

    private File GetIndexPage() {
        Logger.INFO.Log("GetIndexPage started. " + sourceFolder.getAbsolutePath());
        File indexFile = new File(sourceFolder + File.separator + "index.html");
        if (!indexFile.exists()) {
            Logger.WARN.Log("File " + indexFile.getName() + " not found!");
            return null;
        }
        return indexFile;
    }

    private void StartListening() {
        try {
            Logger.INFO.Log("Starting Server on " + (port == 0 ? "available port": "port: " + port));
            socket = new ServerSocket(port, backlog);
            Logger.INFO.Log("Server name: " + serverName);
            Logger.DEBUG.Log("Socket local address: " + socket.getLocalSocketAddress() + " InetAddress: " + socket.getInetAddress());
            Logger.DEBUG.Log("Socket local port: " + socket.getLocalPort());
            Logger.DEBUG.Log("Running on directory: " + sourceFolder);
            while (true) {
                Socket clientSocket = socket.accept();
                Logger.INFO.Log("Waiting for requests... Listening on Port: " + clientSocket.getLocalPort() + " at address: " + clientSocket.getLocalSocketAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String requestType = in.readLine();
                Logger.INFO.Log("Request: " + requestType);

                String body = "";
                StringBuilder requestedFile = new StringBuilder();
                if (requestType != null) {
                    if (requestType.isEmpty()) {
                        break;
                    }
                    if (requestType.contains("GET")) {
                        for (int i = 5; i < requestType.length(); i++) {
                            if (requestType.charAt(i) == '/') {
                                requestedFile.setLength(0);
                                continue;
                            }
                            if (requestType.charAt(i) == ' ') {
                                break;
                            }
                            requestedFile.append(requestType.charAt(i));
                        }
                        if (!(requestedFile.isEmpty())) {
                            body = ResponseBody(GetRequestedFile(requestedFile.toString()), StandardCharsets.UTF_8);
                        }
                    }
                }

                String clientInputLine;
                while ((clientInputLine = in.readLine()) != null) {
                    if (clientInputLine.isEmpty()) {
                        break;
                    }
                    Logger.INFO.Log("Request Headers: " + clientInputLine);
                }

                if (requestedFile.toString().contains("css"))
                    PostResponse(out, body, ContentType.StyleSheet.getContentType(), StatusCode.Accepted.getStatusCode());
                if (requestedFile.toString().trim().isEmpty()) {
                    File htmlFile = GetIndexPage();
                    String htmlBody;
                    String statusCode;
                    String contentType;
                    if (htmlFile != null) {
                        contentType = ContentType.HTML.getContentType();
                        statusCode = StatusCode.OK.getStatusCode();
                        htmlBody = ResponseBody(htmlFile, StandardCharsets.UTF_8);
                    } else {
                        contentType = ContentType.TextPlain.getContentType();
                        statusCode = StatusCode.NotFound.getStatusCode();
                        htmlBody = "404 Not Found";
                    }
                    PostResponse(out, htmlBody, contentType, statusCode);
                }
                if (requestedFile.toString().contains("html"))
                    PostResponse(out, body, ContentType.HTML.getContentType(), StatusCode.Accepted.getStatusCode());
                if (requestedFile.toString().contains("js"))
                    PostResponse(out, body, ContentType.JavaScript.getContentType(), StatusCode.Accepted.getStatusCode());
                if (requestedFile.toString().contains("ico"))
                    PostResponse(out, body, ContentType.ImageXIcon.getContentType(), StatusCode.Accepted.getStatusCode());
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
                Logger.INFO.Log("Socket Closed and cleared resources.");
            } catch (Exception ex) {
                Logger.CRITICAL.LogException(ex, "Cannot close socket");
            }
        }
    }

    private void PostResponse(BufferedWriter out, String body, String contentType, String statusCode) throws IOException {
        int bodyLength = body.length();
        Logger.DEBUG.Log("Body Length: " + bodyLength);
        LocalDateTime now = LocalDateTime.now();
        out.write("HTTP/1.0 "+ statusCode +"\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: " + serverName + "\r\n");
        out.write("Content-Type: " + contentType + "\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }
}
