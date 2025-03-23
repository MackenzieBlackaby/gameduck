package com.blackaby.Frontend;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DebugLogger {
    private static final String LOG_FILE = "debugoutput.txt"; // Change the filename if needed
    private static final String SERIAL_FILE = "serialoutput.txt"; // Change the filename if needed

    private static ArrayList<Integer> logBuffer = new ArrayList<Integer>();

    public static void log(String message) {
        System.out.print(message);
        // logFile(message, LOG_FILE);
    }

    public static void logFile(String message, String file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logn(String message) {
        DebugLogger.log(message + "\n");
    }

    public static void serialOutput(int byteToPrint) {
        logFile(Character.toString((char) (byteToPrint & 0xFF)), SERIAL_FILE);
    }
}
