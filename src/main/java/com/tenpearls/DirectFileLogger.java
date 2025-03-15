package com.tenpearls;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple class that directly writes to log files without using Log4j2.
 */
public class DirectFileLogger {
    private static final String LOGS_DIR = "logs";
    private static final String DAILY_DIR = LOGS_DIR + File.separator + "daily";
    
    public static void main(String[] args) {
        System.out.println("Starting DirectFileLogger...");
        System.out.println("Current directory: " + System.getProperty("user.dir"));
        
        try {
            // Create main logs directory
            createDirectory(LOGS_DIR);
            
            // Create daily directory
            createDirectory(DAILY_DIR);
            
            // Create today's log directory
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String todayDir = DAILY_DIR + File.separator + today;
            createDirectory(todayDir);
            
            // Write to a log file in the daily directory
            String logFile = todayDir + File.separator + "direct_log.txt";
            writeLog(logFile, "This is a direct log message in today's directory");
            
            // Check if the log file exists
            File file = new File(logFile);
            System.out.println("Log file exists: " + file.exists());
            System.out.println("Log file can write: " + file.canWrite());
            System.out.println("Log file can read: " + file.canRead());
            System.out.println("Log file length: " + file.length());
            System.out.println("Log file path: " + file.getAbsolutePath());
            
            System.out.println("DirectFileLogger completed successfully");
        } catch (Exception e) {
            System.err.println("Error in DirectFileLogger: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a directory if it doesn't exist
     * 
     * @param dirPath The directory path to create
     * @return true if the directory was created, false if it already exists
     */
    private static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println(dirPath + " directory created: " + created);
            return created;
        } else {
            System.out.println(dirPath + " directory already exists");
            return false;
        }
    }
    
    /**
     * Writes a log message to a file
     * 
     * @param filePath The file path to write to
     * @param message The message to write
     * @throws IOException If an I/O error occurs
     */
    private static void writeLog(String filePath, String message) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            printWriter.println(timestamp + " - DirectFileLogger - " + message);
            System.out.println("Wrote to log file: " + filePath);
        }
    }
} 