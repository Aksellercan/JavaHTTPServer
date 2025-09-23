package Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger Tool
 * Logs in varying severity levels such as INFO, WARN, ERROR, DEBUG and CRITICAL
 */

public enum Logger {
    INFO(" [ INFO ] "),
    WARN(" [ WARN ] "),
    ERROR(" [ ERROR ] "),
    DEBUG(" [ DEBUG ] "),
    CRITICAL(" [ CRITICAL ] "),;

    private final String severity;
    /**
     * Default set to false
     */
    private static boolean debugOutput = false;
    /**
     * Default set to false
     */
    private static boolean verboseLogFile = false;
    /**
     * Default set to false for compatibility
     */
    private static boolean colouredOutput = false;
    /**
     * Default set to false
     */
    private static boolean enableStackTraces = false;

    /**
     * Constructor for logger takes severity level as input
     * @param severity  Sets the severity
     */
    Logger(String severity) {
        this.severity = severity;
    }

    /**
     * Sets whether Logger should output DEBUG severity levels
     * @param debugOutput Takes a boolean to set
     */
    public static void setDebugOutput(boolean debugOutput) {
        Logger.debugOutput = debugOutput;
    }

    /**
     * Sets if debug severities should be included in log file or not
     * @param verboseLogFile    Takes a boolean to set
     */
    public static void setVerboseLogFile(boolean verboseLogFile) {
        Logger.verboseLogFile = verboseLogFile;
    }

    /**
     * Enable or disable ANSI colours on console
     * @param colouredOutput    Takes a boolean to set
     */
    public static void setColouredOutput(boolean colouredOutput) {
        Logger.colouredOutput = colouredOutput;
    }

    /**
     * Enable or disable detailed stack traces in log
     * @param enableStackTraces    Takes a boolean to set
     */
    public static void setEnableStackTraces(boolean enableStackTraces) {
        Logger.enableStackTraces = enableStackTraces;
    }

    /**
     * Returns debugOutput value
     * @return debugOutput value
     */
    public static boolean getDebugOutput() {
        return debugOutput;
    }

    /**
     * Returns colouredOutput value
     * @return  colouredOutput value
     */
    public static boolean getColouredOutput() {
        return colouredOutput;
    }

    /**
     * Returns verboseLogFile value
     * @return  verboseLogFile value
     */
    public static boolean getVerboseLogFile() {
        return verboseLogFile;
    }

    /**
     * Returns enableStackTraces value
     * @return  enableStackTraces value
     */
    public static boolean getEnableStackTraces() {
        return enableStackTraces;
    }

    /**
     * Formats the message with date and the severity level
     * @return Formatted message string
     */
    private String DateSeverityFormat() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter) + severity;
    }

    /**
     * Logs message and writes it to logfile
     * @param message   Message to be logged
     */
    public void Log(String message) {
        WriteLog(message, true);
    }

    /**
     * Logs message and writes it to logfile if "writeToFile" variable it set to true.
     * If debugOutput is set to false it won't display it or write it to file
     * @param message   Message to be logged
     * @param writeToFile Whether to write to logfile or not
     */
    public void Log(String message, boolean writeToFile) {
        WriteLog(message, writeToFile);
    }

    /**
     * Decides what to do with logs depending on settings.
     *  If only "debugOutput" is set to false it won't display it and by default will not write to file.
     *  If "debugOutput" is set to false but "verboseLogFile" is set to true, It will only write it to log file.
     *  If only "verboseLogFile" is set to false but "debugOutput" is set to true, then it will only display it on console.
     *  Parameter "writeToFile" still has priority on deciding whether to write to file or not, however.
     * @param message   Message to be logged
     * @param writeToFile   Whether to write to log file or not
     */
    private void WriteLog(String message, boolean writeToFile) {
        String fullMessage = DateSeverityFormat() + message;
        if (!debugOutput && this == DEBUG) {
            if (verboseLogFile && writeToFile) SaveLog(fullMessage);
            return;
        }
        else if (this == DEBUG) {
            if (verboseLogFile && writeToFile) SaveLog(fullMessage);
            ColourOutput(fullMessage);
            return;
        }
        if (writeToFile) {
            SaveLog(fullMessage);
        }
        ColourOutput(fullMessage);
    }

    /**
     * Colour console output depending on severity level
     * @param fullMessage   Message to be logged
     */
    private void ColourOutput(String fullMessage) {
        if (colouredOutput) {
            switch (this) {
                case WARN:
                    System.out.println(ConsoleColours.YELLOW + fullMessage + ConsoleColours.RESET);
                    break;
                case DEBUG:
                    System.out.println(ConsoleColours.BLUE + fullMessage + ConsoleColours.RESET);
                    break;
                case ERROR:
                case CRITICAL:
                    System.out.println(ConsoleColours.RED + fullMessage + ConsoleColours.RESET);
                    break;
                case INFO:
                    System.out.println(ConsoleColours.GREEN + fullMessage + ConsoleColours.RESET);
                    break;
                default:
                    System.out.println(fullMessage);
            }
        } else {
            System.out.println(fullMessage);
        }
    }

    /**
     * Used to log exceptions with messages and writes them to logfile
     * @param e Exception to be logged
     * @param message   Message to be logged
     */
    public void LogException(Exception e, String message) {
        WriteExceptionMessageLogs(e, message, true);
    }

    /**
     * Used to log exceptions with messages and writes them to logfile if "writeToFile" is set to true
     * @param e Exception to be logged
     * @param message   Message to be logged
     * @param writeToFile   Whether to write to logfile or not
     */
    public void LogException(Exception e, String message, boolean writeToFile) {
        WriteExceptionMessageLogs(e, message, writeToFile);
    }

    /**
     * Used to log exceptions and writes them to logfile
     * @param e Exception to be logged
     */
    public void LogException(Exception e) {
        WriteExceptions(e, true);
    }

    /**
     * Used to log exceptions and writes them to logfile if "writeToFile" is set to true
     * @param e Exception to be logged
     * @param writeToFile   Whether to write to logfile or not
     */
    public void LogException(Exception e, boolean writeToFile) {
        WriteExceptions(e, writeToFile);
    }

    /**
     * Formats Exceptions with date and severity then writes them to file if "writeToFile" is set to true
     * @param e Exception to be formatted
     * @param writeToFile   Whether to write to logfile or not
     */
    private void WriteExceptions(Exception e, boolean writeToFile) {
        String fullMessage;
        if (enableStackTraces) {
            fullMessage = DateSeverityFormat()  + e.getMessage() + "\n" + GetStackTraceAsString(e);
        } else {
            fullMessage = DateSeverityFormat()  + e.getMessage();
        }
        if (writeToFile) {
            SaveLog(fullMessage);
        }
        ColourOutput(fullMessage);
    }

    /**
     * Formats Exceptions with message, date and severity then writes them to file if "writeToFile" is set to true
     * @param e Exception to be formatted
     * @param message   Message to be formatted
     * @param writeToFile   Whether to write to logfile or not
     */
    private void WriteExceptionMessageLogs(Exception e, String message, boolean writeToFile) {
        String fullMessage;
        if (enableStackTraces) {
            fullMessage = DateSeverityFormat() + message + ". Exception: " + e.getMessage() + "\n" + GetStackTraceAsString(e);
        } else {
            fullMessage = DateSeverityFormat() + message + ". Exception: " + e.getMessage();
        }
        if (writeToFile) {
            SaveLog(fullMessage);
        }
        ColourOutput(fullMessage);
    }

    /**
     * Gets detailed stack trace and returns it as string with tab indentation
     * @param e Exception stack trace to be returned
     * @return  Stacktrace with tab indentation
     */
    private String GetStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < e.getStackTrace().length; i++) {
            if (i+1 == e.getStackTrace().length) {
                sb.append("\t").append(e.getStackTrace()[i]);
                continue;
            }
            sb.append("\t").append(e.getStackTrace()[i]).append("\n");
        }
        return sb.toString();
    }

    /**
     * Logs silently by not printing them to the console instead writes them to logfile
     * @param message   Message to be logged
     */
    public void LogSilently(String message) {
        String fullMessage = DateSeverityFormat() + message;
        SaveLog(fullMessage);
    }

    /**
     * Logs exceptions with message silently by not printing them to the console instead writes them to logfile
     * @param e Exception to be logged
     * @param message   Message to be logged
     */
    public void LogExceptionSilently(Exception e, String message) {
        WriteExceptionMessageLogs(e, message, true);
    }

    /**
     * Writes the logs to logfile in directory "Logs/*" with filename as the date log was created with ".log" extension. Checks if the directory exists if not creates it.
     * Same for logfile as well, checks if the file exists if it does, it appends to it. Otherwise, creates it.
     * @param fullMessage   Full formatted message that will be written to logfile
     */
    private void SaveLog(String fullMessage) {
        try {
            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String fileName = "log_" + today.format(formatter);
            File logFile = getLogFile(fileName);
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(fullMessage + "\n");
            }
        } catch (IOException e) {
            LogException(e, "SaveLog(String fullMessage)");
        }
    }

    /**
     * Returns the logfile
     * @param fileName  logfile name
     * @return  return the found logfile
     * @throws IOException  If the file doesn't exist
     */
    private File getLogFile(String fileName) throws IOException {
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
        return logFile;
    }
}