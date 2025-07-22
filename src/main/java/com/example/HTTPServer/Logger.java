package com.example.HTTPServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum Logger {
    INFO(" [ INFO ] "),
    WARN(" [ WARN ] "),
    ERROR(" [ ERROR ] "),
    DEBUG(" [ DEBUG ] "),
    CRITICAL(" [ CRITICAL ] "),;

    private final String severity;
    private static boolean debugOutput = false;

    Logger(String severity) {
        this.severity = severity;
    }

    public static void setDebugOutput(boolean debugOutput) {
        Logger.debugOutput = debugOutput;
    }

    public static boolean getDebugOutput() {
        return debugOutput;
    }

    private String DateSeverityFormat() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter) + severity;
    }

    public void Log(String message) {
        if (!debugOutput && this == DEBUG) {
            return;
        } else if (this == DEBUG) {
            debugLogs(message);
            return;
        }
        Log(message, true);
    }

    public void Log(String message, boolean writetoFile) {
        String fullMessage = DateSeverityFormat() + message;
        if (writetoFile) {
            writeLog(fullMessage);
        }
        System.out.println(fullMessage);
    }

    // Write exception message + your own message together to console and log file by default
    public void LogException(Exception e, String message) {
        WriteExceptionMessageLogs(e, message, true);
    }

    public void LogException(Exception e, String message, boolean writetoFile) {
        WriteExceptionMessageLogs(e, message, writetoFile);
    }

    // Write just exception message to console and log file by default
    public void LogException(Exception e) {
        WriteExceptions(e, true);
    }

    public void LogException(Exception e, boolean writetoFile) {
        WriteExceptions(e, writetoFile);
    }


    private void WriteExceptions(Exception e, boolean writetoFile) {
        String fullMessage = DateSeverityFormat()  + e.getMessage();
        if (writetoFile) {
            writeLog(fullMessage);
        }
        System.out.println(fullMessage);
    }

    private void WriteExceptionMessageLogs(Exception e, String message, boolean writetoFile) {
        String fullMessage = DateSeverityFormat()  + message + ". Exception: " + e.getMessage();
        if (writetoFile) {
            writeLog(fullMessage);
        }
        System.out.println(fullMessage);
    }

    public void LogSilently(String message) {
        String fullMessage = DateSeverityFormat() + message;
        writeLog(fullMessage);
    }

    public void LogExceptionSilently(Exception e, String message) {
        WriteExceptionMessageLogs(e, message, true);
    }

    private void debugLogs(String message) {
        String fullMessage = DateSeverityFormat() + message;
        System.out.println(fullMessage);
    }

    private void writeLog(String fullMessage) {
        try {
            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fileName = "log_" + today.format(formatter);
            File logPath = new File("Logs");
            if (!logPath.exists()) {
                boolean createDir = logPath.mkdirs();
                if (!createDir) {
                    throw new IOException("Could not create log directory");
                }
            }
            File logFile = new File(logPath + File.separator + fileName + ".log");
            if (!logFile.exists()) {
                boolean createFile = logFile.createNewFile();
                if (!createFile) {
                    throw new IOException("Could not create log file");
                }
            }
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(fullMessage + "\n");
            }
        } catch (IOException e) {
            LogException(e, "writeLog(String fullMessage)");
        }
    }
}