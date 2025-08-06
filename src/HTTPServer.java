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

    private File GetFolders(String requestedFile) {
        StringBuilder folders = new StringBuilder();
        StringBuilder folderNames = new StringBuilder();
        for (int i = 5; i < requestedFile.length(); i++) {
            if (requestedFile.charAt(i) == ' ') {
                break;
            }
            if (requestedFile.charAt(i) == '/') {
                folders.append(folderNames).append(File.separator);
                folderNames.setLength(0);
                continue;
            }
            folderNames.append(requestedFile.charAt(i));
        }
        if (folderNames.toString().isEmpty()) {
            return GetIndexPage();
        }
        return new File(sourceFolder.getAbsolutePath() + File.separator + folders + folderNames);
    }

    private String ResponseBody(File file, Charset encoding) throws IOException {
        if (!file.exists()) {
            return "";
        }
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    private File GetIndexPage() {
        Logger.DEBUG.Log("Index page requested. " + sourceFolder.getAbsolutePath());
        File indexFile = new File(sourceFolder + File.separator + "index.html");
        if (!indexFile.exists()) {
            Logger.WARN.Log("File " + indexFile.getName() + " not found!");
            return null;
        }
        return indexFile;
    }

    private String GetFilename(String requestType) {
        StringBuilder requestedFile = new StringBuilder();
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
        }
        return requestedFile.toString();
    }

    private void StartListening() {
        try {
            Logger.INFO.Log("Starting Server on " + (port == 0 ? "available port" : "port: " + port));
            socket = new ServerSocket(port, backlog);
            Logger.INFO.Log("Server name: " + serverName);
            Logger.INFO.Log("Socket local address: " + socket.getLocalSocketAddress() + " InetAddress: " + socket.getInetAddress());
            Logger.INFO.Log("Socket local port: " + socket.getLocalPort());
            Logger.INFO.Log("Running on directory: " + sourceFolder);
            while (true) {
                Socket clientSocket = socket.accept();
                Logger.INFO.Log("Waiting for requests... Listening on Port: " + clientSocket.getLocalPort() + " at address: " + clientSocket.getLocalSocketAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String requestType = in.readLine();
                Logger.INFO.Log("Request: " + requestType);

                String body = "";
                String requestedFile = null;
                if (requestType != null) {
                    if (requestType.isEmpty()) {
                        break;
                    }
                    requestedFile = GetFilename(requestType);
                    if (!(requestedFile.isEmpty())) {
                        Logger.DEBUG.Log("Requested file: " + requestedFile);
                        body = ResponseBody(GetFolders(requestType), StandardCharsets.UTF_8);
                    }
                }

                String clientInputLine;
                while ((clientInputLine = in.readLine()) != null) {
                    if (clientInputLine.isEmpty()) {
                        break;
                    }
                    Logger.DEBUG.Log("Request Headers: " + clientInputLine);
                }

                if (requestType != null) {
                    if (requestedFile.contains("css"))
                        PostResponse(out, body, ContentType.StyleSheet.getContentType(), StatusCode.Accepted.getStatusCode());
                    if (requestedFile.trim().isEmpty()) {
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
                    if (requestedFile.contains("html"))
                        PostResponse(out, body, ContentType.HTML.getContentType(), StatusCode.Accepted.getStatusCode());
                    if (requestedFile.contains("js"))
                        PostResponse(out, body, ContentType.JavaScript.getContentType(), StatusCode.Accepted.getStatusCode());
                    if (requestedFile.contains("ico"))
                        PostResponse(out, body, ContentType.ImageXIcon.getContentType(), StatusCode.Accepted.getStatusCode());
                }
                System.out.println(":: Ctrl+C to stop the server");
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
        out.write("HTTP/1.0 " + statusCode + "\r\n");
        out.write("Date: " + now + "\r\n");
        out.write("Server: " + serverName + "\r\n");
        out.write("Content-Type: " + contentType + "\r\n");
        out.write("Content-Length: " + bodyLength + "\r\n");
        out.write("\r\n");
        out.write(body);
        out.close();
    }
}
