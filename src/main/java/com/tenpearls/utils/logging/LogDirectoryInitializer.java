package com.tenpearls.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Initializes log directories at application startup.
 * Creates the necessary directory structure for logs with date-based organization.
 */
@Component
public class LogDirectoryInitializer implements ApplicationListener<ApplicationStartedEvent> {
    private static final Logger logger = LogManager.getLogger(LogDirectoryInitializer.class);
    
    private static final String LOGS_DIR = "logs";
    private static final String ARCHIVE_DIR = LOGS_DIR + File.separator + "archive";
    private static final String DAILY_DIR = LOGS_DIR + File.separator + "daily";
    
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            // Create main logs directory
            createDirectory(LOGS_DIR);
            
            // Create archive directory for rotated logs
            createDirectory(ARCHIVE_DIR);
            
            // Create daily directory for date-based logs
            createDirectory(DAILY_DIR);
            
            // Create today's log directory
            String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String todayDir = DAILY_DIR + File.separator + today;
            createDirectory(todayDir);
            
            logger.info("Log directories initialized successfully");
            LoggerUtils.success(logger, "Log directories initialized successfully");
            LoggerUtils.data(logger, "Main Logs Directory", new File(LOGS_DIR).getAbsolutePath());
            LoggerUtils.data(logger, "Archive Directory", new File(ARCHIVE_DIR).getAbsolutePath());
            LoggerUtils.data(logger, "Daily Logs Directory", new File(DAILY_DIR).getAbsolutePath());
            LoggerUtils.data(logger, "Today's Logs Directory", new File(todayDir).getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to initialize log directories", e);
        }
    }
    
    /**
     * Creates a directory if it doesn't exist
     * 
     * @param dirPath The directory path to create
     * @return true if the directory was created, false if it already exists
     */
    private boolean createDirectory(String dirPath) {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                return true;
            } catch (Exception e) {
                logger.error("Failed to create directory: " + dirPath, e);
                return false;
            }
        }
        return false;
    }
} 