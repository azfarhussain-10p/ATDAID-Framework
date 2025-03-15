package com.tenpearls.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.zip.GZIPOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Manages log rotation and archiving to keep log files organized and prevent excessive disk usage.
 * Features:
 * - Automatic daily log rotation
 * - Compression of old log files
 * - Deletion of logs older than a configurable retention period
 * - Size-based rotation for large log files
 */
@Component
public class LogRotationManager {
    private static final Logger logger = LogManager.getLogger(LogRotationManager.class);
    
    private static final String LOGS_DIR = "logs";
    private static final String ARCHIVE_DIR = LOGS_DIR + File.separator + "archive";
    private static final String DAILY_DIR = LOGS_DIR + File.separator + "daily";
    
    // Configuration parameters
    private static final int LOG_RETENTION_DAYS = 30; // Keep logs for 30 days
    private static final long MAX_LOG_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Scheduled task to rotate logs daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    public void performDailyRotation() {
        logger.info("Starting daily log rotation");
        LoggerUtils.info(logger, "Starting daily log rotation");
        
        try {
            // Create today's log directory
            String today = LocalDate.now().format(DATE_FORMATTER);
            String todayDir = DAILY_DIR + File.separator + today;
            createDirectoryIfNotExists(todayDir);
            
            // Archive logs from previous days
            archiveOldLogs();
            
            // Delete logs older than retention period
            deleteExpiredLogs();
            
            logger.info("Daily log rotation completed successfully");
            LoggerUtils.success(logger, "Daily log rotation completed successfully");
        } catch (Exception e) {
            logger.error("Error during daily log rotation", e);
            LoggerUtils.error(logger, "Error during daily log rotation: " + e.getMessage());
        }
    }
    
    /**
     * Checks log files for size-based rotation
     * This is scheduled to run every hour
     */
    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 ms)
    public void checkLogSizes() {
        logger.debug("Checking log file sizes for rotation");
        
        try {
            // Check main application log
            File mainLogFile = new File(LOGS_DIR + File.separator + "application.log");
            if (mainLogFile.exists() && mainLogFile.length() > MAX_LOG_FILE_SIZE) {
                rotateLargeLogFile(mainLogFile);
            }
            
            // Check direct log file
            File directLogFile = new File(LOGS_DIR + File.separator + "direct_log.txt");
            if (directLogFile.exists() && directLogFile.length() > MAX_LOG_FILE_SIZE) {
                rotateLargeLogFile(directLogFile);
            }
            
            // Check today's logs
            String today = LocalDate.now().format(DATE_FORMATTER);
            String todayDir = DAILY_DIR + File.separator + today;
            File todayDirFile = new File(todayDir);
            
            if (todayDirFile.exists() && todayDirFile.isDirectory()) {
                File[] logFiles = todayDirFile.listFiles((dir, name) -> name.endsWith(".log") || name.endsWith(".txt"));
                if (logFiles != null) {
                    for (File logFile : logFiles) {
                        if (logFile.length() > MAX_LOG_FILE_SIZE) {
                            rotateLargeLogFile(logFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error checking log sizes", e);
            LoggerUtils.error(logger, "Error checking log sizes: " + e.getMessage());
        }
    }
    
    /**
     * Rotates a large log file by compressing it and creating a new empty file
     * @param logFile The log file to rotate
     */
    private void rotateLargeLogFile(File logFile) throws IOException {
        logger.info("Rotating large log file: {}", logFile.getAbsolutePath());
        LoggerUtils.info(logger, "Rotating large log file: " + logFile.getAbsolutePath());
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String rotatedFileName = logFile.getName().replaceFirst("\\.(log|txt)$", "_" + timestamp + ".gz");
        File archiveFile = new File(ARCHIVE_DIR + File.separator + rotatedFileName);
        
        // Compress the log file
        compressFile(logFile, archiveFile);
        
        // Clear the original log file
        try (FileOutputStream fos = new FileOutputStream(logFile)) {
            // Empty the file
            fos.write(new byte[0]);
        }
        
        logger.info("Log file rotated successfully: {} -> {}", logFile.getAbsolutePath(), archiveFile.getAbsolutePath());
        LoggerUtils.success(logger, "Log file rotated successfully");
    }
    
    /**
     * Archives logs from previous days
     */
    private void archiveOldLogs() throws IOException {
        logger.debug("Archiving old logs");
        
        LocalDate today = LocalDate.now();
        File dailyDir = new File(DAILY_DIR);
        
        if (dailyDir.exists() && dailyDir.isDirectory()) {
            File[] dateDirs = dailyDir.listFiles(File::isDirectory);
            if (dateDirs != null) {
                for (File dateDir : dateDirs) {
                    try {
                        LocalDate dirDate = LocalDate.parse(dateDir.getName(), DATE_FORMATTER);
                        
                        // If the directory is not today's and not yesterday's, archive it
                        if (ChronoUnit.DAYS.between(dirDate, today) > 1) {
                            archiveDateDirectory(dateDir);
                        }
                    } catch (Exception e) {
                        logger.warn("Could not parse directory name as date: {}", dateDir.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Archives a date directory by compressing its log files
     * @param dateDir The date directory to archive
     */
    private void archiveDateDirectory(File dateDir) throws IOException {
        logger.info("Archiving date directory: {}", dateDir.getAbsolutePath());
        LoggerUtils.info(logger, "Archiving date directory: " + dateDir.getAbsolutePath());
        
        File[] logFiles = dateDir.listFiles((dir, name) -> name.endsWith(".log") || name.endsWith(".txt"));
        if (logFiles != null) {
            for (File logFile : logFiles) {
                String archiveFileName = dateDir.getName() + "_" + logFile.getName().replaceFirst("\\.(log|txt)$", ".gz");
                File archiveFile = new File(ARCHIVE_DIR + File.separator + archiveFileName);
                
                // Compress the log file
                compressFile(logFile, archiveFile);
                
                // Delete the original log file
                logFile.delete();
            }
        }
        
        // If the directory is empty after archiving, delete it
        if (dateDir.list() != null && dateDir.list().length == 0) {
            dateDir.delete();
            logger.info("Deleted empty date directory: {}", dateDir.getAbsolutePath());
        }
    }
    
    /**
     * Deletes logs older than the retention period
     */
    private void deleteExpiredLogs() throws IOException {
        logger.debug("Deleting expired logs");
        
        LocalDate cutoffDate = LocalDate.now().minusDays(LOG_RETENTION_DAYS);
        File archiveDir = new File(ARCHIVE_DIR);
        
        if (archiveDir.exists() && archiveDir.isDirectory()) {
            File[] archiveFiles = archiveDir.listFiles();
            if (archiveFiles != null) {
                for (File archiveFile : archiveFiles) {
                    // Try to extract date from filename (format: YYYY-MM-DD_*.gz)
                    String fileName = archiveFile.getName();
                    try {
                        if (fileName.matches("\\d{4}-\\d{2}-\\d{2}_.*\\.gz")) {
                            String dateStr = fileName.substring(0, 10);
                            LocalDate fileDate = LocalDate.parse(dateStr, DATE_FORMATTER);
                            
                            if (fileDate.isBefore(cutoffDate)) {
                                boolean deleted = archiveFile.delete();
                                if (deleted) {
                                    logger.info("Deleted expired log file: {}", archiveFile.getAbsolutePath());
                                } else {
                                    logger.warn("Failed to delete expired log file: {}", archiveFile.getAbsolutePath());
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Could not parse date from filename: {}", fileName, e);
                    }
                }
            }
        }
    }
    
    /**
     * Compresses a file using GZIP
     * @param sourceFile The source file to compress
     * @param destFile The destination compressed file
     */
    private void compressFile(File sourceFile, File destFile) throws IOException {
        createDirectoryIfNotExists(destFile.getParent());
        
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destFile);
             GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
            
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gzos.write(buffer, 0, len);
            }
        }
        
        logger.debug("Compressed file: {} -> {}", sourceFile.getAbsolutePath(), destFile.getAbsolutePath());
    }
    
    /**
     * Creates a directory if it doesn't exist
     * @param dirPath The directory path to create
     */
    private void createDirectoryIfNotExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.debug("Created directory: {}", dirPath);
        }
    }
} 