import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Invalid Usage\nUsage: HTTPServer start --path <path> --port <port> --backlog <backlog> --verbose");
            System.exit(1);
        }
        if (args.length <= 10) {
            if (args[0].equals("start")) {
                int port = 0;
                int backlog = 10;
                String name = null;
                File sourceFolder = null;
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("--name")) {
                        name = args[i+1];
                    }
                    if (args[i].equals("--path")) {
                        sourceFolder = new File(args[i + 1]);
                    }
                    if (args[i].equals("--port")) {
                        port = Integer.parseInt(args[i + 1]);
                    }
                    if (args[i].equals("--backlog")) {
                        backlog = Integer.parseInt(args[i + 1]);
                    }
                    if (args[i].equals("--verbose")) {
                        Logger.setDebugOutput(true);
                    }
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
            }
        }
    }
}