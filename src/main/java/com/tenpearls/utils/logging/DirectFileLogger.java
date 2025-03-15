package com.tenpearls.utils.logging;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple file logger that writes directly to a file without using Log4j2.
 * This is used to verify if there are any permission issues with writing to files.
 */
@Component
@Order(1) // Run before other CommandLineRunners
public class DirectFileLogger implements CommandLineRunner {

    @Override
    public void run(String... args) {
        try {
            // Create logs directory if it doesn't exist
            String logsDir = System.getProperty("user.dir") + File.separator + "logs";
            Path logsDirPath = Paths.get(logsDir);
            if (!Files.exists(logsDirPath)) {
                Files.createDirectories(logsDirPath);
            }

            // Create a direct log file
            String directLogFile = logsDir + File.separator + "direct_log.txt";
            try (FileWriter writer = new FileWriter(directLogFile, true)) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                writer.write(timestamp + " - DirectFileLogger - This is a direct log message\n");
                writer.write(timestamp + " - DirectFileLogger - Current working directory: " + System.getProperty("user.dir") + "\n");
                writer.write(timestamp + " - DirectFileLogger - Logs directory: " + logsDir + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file: " + directLogFile + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file exists: " + new File(directLogFile).exists() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file can write: " + new File(directLogFile).canWrite() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file can read: " + new File(directLogFile).canRead() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file is directory: " + new File(directLogFile).isDirectory() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file is file: " + new File(directLogFile).isFile() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file is absolute: " + new File(directLogFile).isAbsolute() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file absolute path: " + new File(directLogFile).getAbsolutePath() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file canonical path: " + new File(directLogFile).getCanonicalPath() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file parent: " + new File(directLogFile).getParent() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file parent exists: " + new File(new File(directLogFile).getParent()).exists() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file parent can write: " + new File(new File(directLogFile).getParent()).canWrite() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file parent can read: " + new File(new File(directLogFile).getParent()).canRead() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file parent is directory: " + new File(new File(directLogFile).getParent()).isDirectory() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file parent is file: " + new File(new File(directLogFile).getParent()).isFile() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file parent is absolute: " + new File(new File(directLogFile).getParent()).isAbsolute() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file parent absolute path: " + new File(new File(directLogFile).getParent()).getAbsolutePath() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Log file parent canonical path: " + new File(new File(directLogFile).getParent()).getCanonicalPath() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Direct file logging completed\n");
            }

            // Create daily directory
            String dailyDir = logsDir + File.separator + "daily";
            Path dailyDirPath = Paths.get(dailyDir);
            if (!Files.exists(dailyDirPath)) {
                Files.createDirectories(dailyDirPath);
            }

            // Create today's directory
            String today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String todayDir = dailyDir + File.separator + today;
            Path todayDirPath = Paths.get(todayDir);
            if (!Files.exists(todayDirPath)) {
                Files.createDirectories(todayDirPath);
            }

            // Create a direct log file in today's directory
            String todayLogFile = todayDir + File.separator + "direct_log.txt";
            try (FileWriter writer = new FileWriter(todayLogFile, true)) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                writer.write(timestamp + " - DirectFileLogger - This is a direct log message in today's directory\n");
                writer.write(timestamp + " - DirectFileLogger - Today's directory: " + todayDir + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file: " + todayLogFile + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file exists: " + new File(todayLogFile).exists() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file can write: " + new File(todayLogFile).canWrite() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file can read: " + new File(todayLogFile).canRead() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file is directory: " + new File(todayLogFile).isDirectory() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file is file: " + new File(todayLogFile).isFile() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file is absolute: " + new File(todayLogFile).isAbsolute() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file absolute path: " + new File(todayLogFile).getAbsolutePath() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file canonical path: " + new File(todayLogFile).getCanonicalPath() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file parent: " + new File(todayLogFile).getParent() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file parent exists: " + new File(new File(todayLogFile).getParent()).exists() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file parent can write: " + new File(new File(todayLogFile).getParent()).canWrite() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file parent can read: " + new File(new File(todayLogFile).getParent()).canRead() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file parent is directory: " + new File(new File(todayLogFile).getParent()).isDirectory() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file parent is file: " + new File(new File(todayLogFile).getParent()).isFile() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file parent is absolute: " + new File(new File(todayLogFile).getParent()).isAbsolute() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file parent absolute path: " + new File(new File(todayLogFile).getParent()).getAbsolutePath() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Today's log file parent canonical path: " + new File(new File(todayLogFile).getParent()).getCanonicalPath() + "\n");
                writer.write(timestamp + " - DirectFileLogger - Direct file logging in today's directory completed\n");
            }

            System.out.println("DirectFileLogger - Direct file logging completed");
            System.out.println("DirectFileLogger - Log file: " + directLogFile);
            System.out.println("DirectFileLogger - Today's log file: " + todayLogFile);
        } catch (IOException e) {
            System.err.println("DirectFileLogger - Error writing to log file: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 