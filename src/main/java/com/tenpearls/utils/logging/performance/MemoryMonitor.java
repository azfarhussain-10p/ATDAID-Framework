package com.tenpearls.utils.logging.performance;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Monitors memory usage and adjusts log levels accordingly.
 * This class helps prevent excessive logging from consuming too much memory.
 */
@Component
public class MemoryMonitor {
    
    private static final Logger logger = LogManager.getLogger(MemoryMonitor.class);
    
    @Value("${logging.memory.high.threshold:0.85}")
    private double highMemoryThreshold;
    
    @Value("${logging.memory.medium.threshold:0.70}")
    private double mediumMemoryThreshold;
    
    private final MemoryMXBean memoryMXBean;
    private final Map<String, Level> originalLogLevels;
    private boolean logLevelsAdjusted;
    
    /**
     * Constructor that initializes the memory monitor
     */
    public MemoryMonitor() {
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        originalLogLevels = new ConcurrentHashMap<>();
        logLevelsAdjusted = false;
        logger.info("Memory monitor initialized");
    }
    
    /**
     * Monitor memory usage and adjust log levels if necessary
     * This method is scheduled to run every minute
     */
    @Scheduled(fixedRate = 60000)
    public void monitorMemoryUsage() {
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long used = heapMemoryUsage.getUsed();
        long max = heapMemoryUsage.getMax();
        
        // Calculate memory usage ratio
        double memoryUsageRatio = (double) used / max;
        
        logger.debug("Memory usage: {}%, used: {} MB, max: {} MB", 
                String.format("%.2f", memoryUsageRatio * 100),
                used / (1024 * 1024),
                max / (1024 * 1024));
        
        if (memoryUsageRatio > highMemoryThreshold) {
            // High memory usage - reduce logging
            if (!logLevelsAdjusted) {
                adjustLogLevels(Level.ERROR);
                logLevelsAdjusted = true;
                logger.warn("High memory usage detected ({}%). Reducing logging to ERROR level.", 
                        String.format("%.2f", memoryUsageRatio * 100));
            }
        } else if (memoryUsageRatio > mediumMemoryThreshold) {
            // Medium memory usage - reduce logging but not as much
            if (!logLevelsAdjusted) {
                adjustLogLevels(Level.WARN);
                logLevelsAdjusted = true;
                logger.warn("Medium memory usage detected ({}%). Reducing logging to WARN level.", 
                        String.format("%.2f", memoryUsageRatio * 100));
            }
        } else if (logLevelsAdjusted) {
            // Memory usage is back to normal - restore original log levels
            restoreLogLevels();
            logLevelsAdjusted = false;
            logger.info("Memory usage back to normal ({}%). Restoring original log levels.", 
                    String.format("%.2f", memoryUsageRatio * 100));
        }
    }
    
    /**
     * Adjust log levels to the specified level
     * 
     * @param level The level to adjust to
     */
    private void adjustLogLevels(Level level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        
        // Store original log levels and adjust to the specified level
        for (Map.Entry<String, LoggerConfig> entry : config.getLoggers().entrySet()) {
            String loggerName = entry.getKey();
            LoggerConfig loggerConfig = entry.getValue();
            
            // Store original level if not already stored
            if (!originalLogLevels.containsKey(loggerName)) {
                originalLogLevels.put(loggerName, loggerConfig.getLevel());
            }
            
            // Adjust level if it's lower than the specified level
            if (loggerConfig.getLevel().isLessSpecificThan(level)) {
                loggerConfig.setLevel(level);
            }
        }
        
        // Update configuration
        ctx.updateLoggers();
    }
    
    /**
     * Restore original log levels
     */
    private void restoreLogLevels() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        
        // Restore original log levels
        for (Map.Entry<String, Level> entry : originalLogLevels.entrySet()) {
            String loggerName = entry.getKey();
            Level originalLevel = entry.getValue();
            
            LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
            loggerConfig.setLevel(originalLevel);
        }
        
        // Update configuration
        ctx.updateLoggers();
        
        // Clear stored levels
        originalLogLevels.clear();
    }
    
    /**
     * Get the current memory usage ratio
     * 
     * @return The memory usage ratio (0.0 - 1.0)
     */
    public double getMemoryUsageRatio() {
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long used = heapMemoryUsage.getUsed();
        long max = heapMemoryUsage.getMax();
        
        return (double) used / max;
    }
} 