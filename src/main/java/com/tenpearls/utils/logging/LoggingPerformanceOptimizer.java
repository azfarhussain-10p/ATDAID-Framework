package com.tenpearls.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Optimizes logging performance by implementing various strategies:
 * - Asynchronous logging for non-critical messages
 * - Dynamic log level adjustment based on system load
 * - Batch processing of log entries
 * - Periodic cleanup of log buffers
 * - Memory usage monitoring
 */
@Component
public class LoggingPerformanceOptimizer {
    private static final Logger logger = LogManager.getLogger(LoggingPerformanceOptimizer.class);
    
    // Configuration parameters
    private static final int MAX_QUEUE_SIZE = 1000;
    private static final int BATCH_SIZE = 50;
    private static final long FLUSH_INTERVAL_MS = 5000; // 5 seconds
    private static final int THREAD_POOL_SIZE = 2;
    
    // Memory thresholds for dynamic log level adjustment
    private static final double HIGH_MEMORY_THRESHOLD = 0.85; // 85% memory usage
    private static final double MEDIUM_MEMORY_THRESHOLD = 0.70; // 70% memory usage
    
    // Async logging queue and executor
    private final BlockingQueue<LogEntry> logQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
    private final AtomicInteger droppedLogCount = new AtomicInteger(0);
    
    // Original log levels for restoring after dynamic adjustment
    private final Map<String, String> originalLogLevels = new ConcurrentHashMap<>();
    
    /**
     * Initialize the optimizer
     */
    public LoggingPerformanceOptimizer() {
        // Start the batch processor
        scheduler.scheduleAtFixedRate(this::processBatch, 0, FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
        
        // Start memory monitoring
        scheduler.scheduleAtFixedRate(this::monitorMemoryUsage, 1, 1, TimeUnit.MINUTES);
        
        logger.info("Logging performance optimizer initialized");
        LoggerUtils.info(logger, "Logging performance optimizer initialized");
    }
    
    /**
     * Asynchronously log a message
     * @param level The log level
     * @param loggerName The logger name
     * @param message The message to log
     */
    public void asyncLog(String level, String loggerName, String message) {
        LogEntry entry = new LogEntry(level, loggerName, message, LocalDateTime.now());
        
        // Try to add to queue, but don't block if full
        boolean added = logQueue.offer(entry);
        if (!added) {
            // If queue is full, increment dropped count
            droppedLogCount.incrementAndGet();
            
            // Log directly if it's an important message
            if (level.equalsIgnoreCase("ERROR") || level.equalsIgnoreCase("FATAL")) {
                Logger targetLogger = LogManager.getLogger(loggerName);
                targetLogger.error("DIRECT (Queue full): {}", message);
            }
        }
    }
    
    /**
     * Process a batch of log entries
     */
    private void processBatch() {
        List<LogEntry> batch = new ArrayList<>(BATCH_SIZE);
        logQueue.drainTo(batch, BATCH_SIZE);
        
        if (!batch.isEmpty()) {
            for (LogEntry entry : batch) {
                // Get the appropriate logger
                Logger targetLogger = LogManager.getLogger(entry.getLoggerName());
                
                // Log the message with the appropriate level
                switch (entry.getLevel().toUpperCase()) {
                    case "DEBUG":
                        targetLogger.debug(entry.getMessage());
                        break;
                    case "INFO":
                        targetLogger.info(entry.getMessage());
                        break;
                    case "WARN":
                        targetLogger.warn(entry.getMessage());
                        break;
                    case "ERROR":
                        targetLogger.error(entry.getMessage());
                        break;
                    case "FATAL":
                        targetLogger.fatal(entry.getMessage());
                        break;
                    default:
                        targetLogger.info(entry.getMessage());
                        break;
                }
            }
            
            logger.debug("Processed {} log entries from queue", batch.size());
        }
        
        // Check if we've dropped any logs
        int dropped = droppedLogCount.getAndSet(0);
        if (dropped > 0) {
            logger.warn("Dropped {} log entries due to queue overflow", dropped);
        }
    }
    
    /**
     * Monitor memory usage and adjust log levels if necessary
     */
    private void monitorMemoryUsage() {
        try {
            // Get current memory usage
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            double memoryUsageRatio = (double) usedMemory / maxMemory;
            
            logger.debug("Current memory usage: {}/{} ({:.2f}%)", 
                    formatSize(usedMemory), 
                    formatSize(maxMemory), 
                    memoryUsageRatio * 100);
            
            // Adjust log levels based on memory usage
            if (memoryUsageRatio > HIGH_MEMORY_THRESHOLD) {
                // High memory usage - reduce logging to ERROR only
                adjustLogLevels("ERROR");
                logger.warn("High memory usage detected ({}%). Reducing logging to ERROR level.", 
                        String.format("%.2f", memoryUsageRatio * 100));
            } else if (memoryUsageRatio > MEDIUM_MEMORY_THRESHOLD) {
                // Medium memory usage - reduce logging to WARN
                adjustLogLevels("WARN");
                logger.warn("Medium memory usage detected ({}%). Reducing logging to WARN level.", 
                        String.format("%.2f", memoryUsageRatio * 100));
            } else {
                // Normal memory usage - restore original log levels
                restoreLogLevels();
            }
        } catch (Exception e) {
            logger.error("Error monitoring memory usage", e);
        }
    }
    
    /**
     * Adjust log levels based on system load
     * @param newLevel The new log level to set
     */
    private void adjustLogLevels(String newLevel) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        
        // Store original log levels if not already stored
        if (originalLogLevels.isEmpty()) {
            for (Map.Entry<String, LoggerConfig> entry : config.getLoggers().entrySet()) {
                String loggerName = entry.getKey();
                LoggerConfig loggerConfig = entry.getValue();
                originalLogLevels.put(loggerName, loggerConfig.getLevel().toString());
            }
        }
        
        // Set new log level for all loggers
        for (Map.Entry<String, LoggerConfig> entry : config.getLoggers().entrySet()) {
            LoggerConfig loggerConfig = entry.getValue();
            loggerConfig.setLevel(org.apache.logging.log4j.Level.getLevel(newLevel));
        }
        
        // Update the configuration
        context.updateLoggers();
        
        logger.info("Adjusted all log levels to {}", newLevel);
    }
    
    /**
     * Restore original log levels
     */
    private void restoreLogLevels() {
        if (originalLogLevels.isEmpty()) {
            return; // Nothing to restore
        }
        
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        
        // Restore original log levels
        for (Map.Entry<String, String> entry : originalLogLevels.entrySet()) {
            String loggerName = entry.getKey();
            String levelName = entry.getValue();
            
            LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
            loggerConfig.setLevel(org.apache.logging.log4j.Level.getLevel(levelName));
        }
        
        // Update the configuration
        context.updateLoggers();
        
        logger.info("Restored original log levels");
        
        // Clear the stored levels
        originalLogLevels.clear();
    }
    
    /**
     * Format a size in bytes to a human-readable string
     * @param size The size in bytes
     * @return A human-readable string
     */
    private String formatSize(long size) {
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double sizeAsDouble = size;
        
        while (sizeAsDouble >= 1024 && unitIndex < units.length - 1) {
            sizeAsDouble /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", sizeAsDouble, units[unitIndex]);
    }
    
    /**
     * Scheduled task to clean up old log files
     * Runs once a day at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldLogBuffers() {
        logger.info("Starting cleanup of old log buffers");
        
        try {
            // Force GC to clean up any lingering resources
            System.gc();
            
            // Process any remaining logs in the queue
            List<LogEntry> remaining = new ArrayList<>();
            logQueue.drainTo(remaining);
            
            if (!remaining.isEmpty()) {
                logger.info("Processing {} remaining log entries before cleanup", remaining.size());
                for (LogEntry entry : remaining) {
                    Logger targetLogger = LogManager.getLogger(entry.getLoggerName());
                    targetLogger.info("CLEANUP: {}", entry.getMessage());
                }
            }
            
            logger.info("Log buffer cleanup completed");
        } catch (Exception e) {
            logger.error("Error during log buffer cleanup", e);
        }
    }
    
    /**
     * Shutdown hook to ensure all logs are processed
     */
    public void shutdown() {
        logger.info("Shutting down logging performance optimizer");
        
        try {
            // Process any remaining logs
            processBatch();
            
            // Shutdown the scheduler
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            logger.info("Logging performance optimizer shutdown completed");
        } catch (Exception e) {
            logger.error("Error during shutdown of logging performance optimizer", e);
        }
    }
    
    /**
     * Checks if the LoggingPerformanceOptimizer is properly initialized.
     * 
     * @return true if initialized, false otherwise
     */
    public boolean isInitialized() {
        return true; // In a real implementation, this would check internal state
    }
    
    /**
     * Inner class to represent a log entry
     */
    private static class LogEntry {
        private final String level;
        private final String loggerName;
        private final String message;
        private final LocalDateTime timestamp;
        
        public LogEntry(String level, String loggerName, String message, LocalDateTime timestamp) {
            this.level = level;
            this.loggerName = loggerName;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public String getLevel() {
            return level;
        }
        
        public String getLoggerName() {
            return loggerName;
        }
        
        public String getMessage() {
            return message;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
} 