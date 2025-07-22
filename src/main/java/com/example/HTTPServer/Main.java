package com.example.HTTPServer;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Invalid Usage\nUsage: HTTPServer start --path <path> --port <port> --backlog <backlog> --verbose");
            System.exit(1);
        }
        if (args.length <= 8) {
            if (args[0].equals("start")) {
                File sourceFolder = null;
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("--path")) {
                        sourceFolder = new File(args[i + 1]);
                        break;
                    }
                }
                int port = 0;
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("--port")) {
                        port = Integer.parseInt(args[i + 1]);
                        break;
                    }
                }
                if (port == 0) {
                    port = 8080;
                }
                int backlog = 0;
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("--backlog")) {
                        backlog = Integer.parseInt(args[i + 1]);
                        break;
                    }
                }
                if (backlog == 0) {
                    backlog = 10;
                }
                for (String arg : args) {
                    if (arg.equals("--verbose")) {
                        Logger.setDebugOutput(true);
                        break;
                    }
                }
                HTTPServer server = new HTTPServer(port, backlog);
                if (sourceFolder != null) {
                    server.setSourceFolder(sourceFolder);
                }
                server.StartServer();
            }
        }
    }
}