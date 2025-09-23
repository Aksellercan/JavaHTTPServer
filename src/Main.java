import java.io.File;
import Logger.Logger;
import Logger.ConsoleColours;

public class Main {
    public static void main(String[] args) {
        Logger.setEnableStackTraces(true);
        if (args.length < 1) {
            System.out.println("Usage: HTTPServer start --name <server-name> --dir <directory> --port <port> --backlog <backlog> --verbose");
            System.exit(0);
        }
        if (args.length <= 10) {
            if (args[0].equals("start")) {
                int port = 0;
                int backlog = 10;
                String name = null;
                File sourceFolder = null;
                try {
                    for (int i = 1; i < args.length; i++) {
                        switch (args[i]) {
                            case "--name":
                                name = args[i + 1];
                                break;
                            case "--dir":
                                sourceFolder = new File(args[i + 1]);
                                break;
                            case "--port":
                                port = Integer.parseInt(args[i + 1]);
                                break;
                            case "--backlog":
                                backlog = Integer.parseInt(args[i + 1]);
                                break;
                            case "--verbose":
                                Logger.setDebugOutput(true);
                                break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(ConsoleColours.RED + "Invalid Usage\nUsage: HTTPServer start --name <server-name> --dir <directory> --port <port> --backlog <backlog> --verbose" + ConsoleColours.RESET);
                    System.exit(1);
                }
                HTTPServer server = new HTTPServer(port, backlog);
                if (sourceFolder != null) {
                    if (!sourceFolder.exists()) {
                        Logger.ERROR.Log("Source folder " + sourceFolder.getAbsolutePath() + " does not exist");
                        System.exit(1);
                    }
                    server.setSourceFolder(sourceFolder);
                }
                if (name != null) {
                    server.setServerName(name);
                }
                server.StartServer();
            } else {
                System.out.println("Usage: HTTPServer start --name <server-name> --dir <directory> --port <port> --backlog <backlog> --verbose");
                System.exit(0);
            }
        }
    }
}