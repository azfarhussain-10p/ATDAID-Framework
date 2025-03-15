package com.tenpearls;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple test class to verify file writing capabilities.
 */
public class SimpleLogTest {

    public static void main(String[] args) {
        try {
            // Get current directory
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current directory: " + currentDir);

            // Create logs directory
            String logsDir = currentDir + File.separator + "logs";
            Path logsDirPath = Paths.get(logsDir);
            if (!Files.exists(logsDirPath)) {
                Files.createDirectories(logsDirPath);
                System.out.println("Created logs directory: " + logsDir);
            } else {
                System.out.println("Logs directory already exists: " + logsDir);
            }

            // Create a simple log file
            String logFile = logsDir + File.separator + "simple_log.txt";
            try (FileWriter writer = new FileWriter(logFile, true)) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                writer.write(timestamp + " - SimpleLogTest - This is a simple log message\n");
                System.out.println("Wrote to log file: " + logFile);
            }

            // Create daily directory
            String dailyDir = logsDir + File.separator + "daily";
            Path dailyDirPath = Paths.get(dailyDir);
            if (!Files.exists(dailyDirPath)) {
                Files.createDirectories(dailyDirPath);
                System.out.println("Created daily directory: " + dailyDir);
            } else {
                System.out.println("Daily directory already exists: " + dailyDir);
            }

            // Create today's directory
            String today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String todayDir = dailyDir + File.separator + today;
            Path todayDirPath = Paths.get(todayDir);
            if (!Files.exists(todayDirPath)) {
                Files.createDirectories(todayDirPath);
                System.out.println("Created today's directory: " + todayDir);
            } else {
                System.out.println("Today's directory already exists: " + todayDir);
            }

            // Create a log file in today's directory
            String todayLogFile = todayDir + File.separator + "simple_log.txt";
            try (FileWriter writer = new FileWriter(todayLogFile, true)) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                writer.write(timestamp + " - SimpleLogTest - This is a simple log message in today's directory\n");
                System.out.println("Wrote to today's log file: " + todayLogFile);
            }

            // Print file information
            File logFileObj = new File(logFile);
            System.out.println("Log file exists: " + logFileObj.exists());
            System.out.println("Log file can write: " + logFileObj.canWrite());
            System.out.println("Log file can read: " + logFileObj.canRead());
            System.out.println("Log file length: " + logFileObj.length());

            File todayLogFileObj = new File(todayLogFile);
            System.out.println("Today's log file exists: " + todayLogFileObj.exists());
            System.out.println("Today's log file can write: " + todayLogFileObj.canWrite());
            System.out.println("Today's log file can read: " + todayLogFileObj.canRead());
            System.out.println("Today's log file length: " + todayLogFileObj.length());

            System.out.println("Simple log test completed successfully");
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 