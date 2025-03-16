package com.tenpearls.utils.logging.performance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for cleaning up old log files.
 * This class helps prevent disk space issues by removing old log files.
 */
@Component
public class LogCleanupService {
    
    private static final Logger logger = LogManager.getLogger(LogCleanupService.class);
    
    @Value("${logging.cleanup.retention.days:30}")
    private int retentionDays;
    
    @Value("${logging.cleanup.max.size.mb:1000}")
    private long maxSizeMb;
    
    @Value("${logging.dir:logs}")
    private String logsDir;
    
    /**
     * Initialize the log cleanup service
     */
    @PostConstruct
    public void init() {
        logger.info("Log cleanup service initialized with retention days: {}, max size: {} MB",
                retentionDays, maxSizeMb);
    }
    
    /**
     * Clean up old log files
     * This method is scheduled to run daily at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldLogs() {
        logger.info("Starting log cleanup process");
        
        try {
            // Clean up by age
            cleanupByAge();
            
            // Clean up by size
            cleanupBySize();
            
            logger.info("Log cleanup process completed");
        } catch (Exception e) {
            logger.error("Error during log cleanup", e);
        }
    }
    
    /**
     * Clean up log files older than the retention period
     */
    private void cleanupByAge() throws IOException {
        logger.info("Cleaning up log files older than {} days", retentionDays);
        
        LocalDate cutoffDate = LocalDate.now().minus(retentionDays, ChronoUnit.DAYS);
        AtomicInteger deletedCount = new AtomicInteger(0);
        
        Files.walkFileTree(Paths.get(logsDir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                LocalDate fileDate = LocalDate.ofInstant(
                        attrs.creationTime().toInstant(), ZoneId.systemDefault());
                
                if (fileDate.isBefore(cutoffDate) && file.toString().endsWith(".log")) {
                    Files.delete(file);
                    deletedCount.incrementAndGet();
                    logger.debug("Deleted old log file: {}", file);
                }
                
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                logger.warn("Failed to access file: {}", file, exc);
                return FileVisitResult.CONTINUE;
            }
        });
        
        logger.info("Deleted {} old log files", deletedCount.get());
    }
    
    /**
     * Clean up log files if the total size exceeds the maximum
     */
    private void cleanupBySize() throws IOException {
        logger.info("Checking total log size (max: {} MB)", maxSizeMb);
        
        // Calculate total size
        final long maxSizeBytes = maxSizeMb * 1024 * 1024;
        final AtomicInteger deletedCount = new AtomicInteger(0);
        final long[] totalSize = {0};
        
        // First pass: calculate total size
        Files.walkFileTree(Paths.get(logsDir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().endsWith(".log")) {
                    totalSize[0] += attrs.size();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        
        logger.info("Total log size: {} MB", totalSize[0] / (1024 * 1024));
        
        // If total size exceeds max, delete oldest files until under limit
        if (totalSize[0] > maxSizeBytes) {
            logger.warn("Log size exceeds maximum, cleaning up oldest files");
            
            // Get all log files sorted by creation time (oldest first)
            Path[] logFiles = Files.walk(Paths.get(logsDir))
                    .filter(p -> p.toString().endsWith(".log"))
                    .sorted((p1, p2) -> {
                        try {
                            return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .toArray(Path[]::new);
            
            // Delete oldest files until under limit
            for (Path file : logFiles) {
                if (totalSize[0] <= maxSizeBytes) {
                    break;
                }
                
                try {
                    long fileSize = Files.size(file);
                    Files.delete(file);
                    totalSize[0] -= fileSize;
                    deletedCount.incrementAndGet();
                    logger.debug("Deleted log file due to size limit: {}", file);
                } catch (IOException e) {
                    logger.warn("Failed to delete file: {}", file, e);
                }
            }
            
            logger.info("Deleted {} log files due to size limit", deletedCount.get());
        }
    }
    
    /**
     * Clean up log files immediately
     * 
     * @return The number of files deleted
     */
    public int cleanupNow() {
        try {
            AtomicInteger deletedCount = new AtomicInteger(0);
            
            // Clean up by age
            cleanupByAge();
            
            // Clean up by size
            cleanupBySize();
            
            return deletedCount.get();
        } catch (Exception e) {
            logger.error("Error during immediate log cleanup", e);
            return 0;
        }
    }
} 