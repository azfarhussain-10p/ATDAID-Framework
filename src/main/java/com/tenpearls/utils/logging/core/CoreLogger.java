package com.tenpearls.utils.logging.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Core logging functionality that provides a simplified interface for Log4j2.
 * This class handles the basic logging operations and is designed to be used
 * directly or extended by other specialized loggers.
 */
@Component
public class CoreLogger {
    
    private static final Logger logger = LogManager.getLogger(CoreLogger.class);
    
    /**
     * Log a message at the specified level
     * 
     * @param level The log level
     * @param message The message to log
     */
    public void log(Level level, String message) {
        if (isLevelEnabled(level)) {
            logger.log(level, message);
        }
    }
    
    /**
     * Log a message with parameters at the specified level
     * 
     * @param level The log level
     * @param message The message format
     * @param params The parameters for the message
     */
    public void log(Level level, String message, Object... params) {
        if (isLevelEnabled(level)) {
            logger.log(level, message, params);
        }
    }
    
    /**
     * Log a message with an exception at the specified level
     * 
     * @param level The log level
     * @param message The message
     * @param throwable The exception to log
     */
    public void log(Level level, String message, Throwable throwable) {
        if (isLevelEnabled(level)) {
            logger.log(level, message, throwable);
        }
    }
    
    /**
     * Log a message supplier at the specified level
     * 
     * @param level The log level
     * @param messageSupplier The message supplier
     */
    public void log(Level level, Supplier<String> messageSupplier) {
        if (isLevelEnabled(level)) {
            logger.log(level, messageSupplier);
        }
    }
    
    /**
     * Check if the specified level is enabled
     * 
     * @param level The log level to check
     * @return true if the level is enabled, false otherwise
     */
    public boolean isLevelEnabled(Level level) {
        return logger.isEnabled(level);
    }
    
    /**
     * Log a message at the INFO level
     * 
     * @param message The message to log
     */
    public void info(String message) {
        log(Level.INFO, message);
    }
    
    /**
     * Log a message with parameters at the INFO level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void info(String message, Object... params) {
        log(Level.INFO, message, params);
    }
    
    /**
     * Log a message at the DEBUG level
     * 
     * @param message The message to log
     */
    public void debug(String message) {
        log(Level.DEBUG, message);
    }
    
    /**
     * Log a message with parameters at the DEBUG level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void debug(String message, Object... params) {
        log(Level.DEBUG, message, params);
    }
    
    /**
     * Log a message at the WARN level
     * 
     * @param message The message to log
     */
    public void warn(String message) {
        log(Level.WARN, message);
    }
    
    /**
     * Log a message with parameters at the WARN level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void warn(String message, Object... params) {
        log(Level.WARN, message, params);
    }
    
    /**
     * Log a message at the ERROR level
     * 
     * @param message The message to log
     */
    public void error(String message) {
        log(Level.ERROR, message);
    }
    
    /**
     * Log a message with parameters at the ERROR level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void error(String message, Object... params) {
        log(Level.ERROR, message, params);
    }
    
    /**
     * Log a message with an exception at the ERROR level
     * 
     * @param message The message
     * @param throwable The exception to log
     */
    public void error(String message, Throwable throwable) {
        log(Level.ERROR, message, throwable);
    }
    
    /**
     * Get a logger for the specified class
     * 
     * @param clazz The class to get the logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
} 