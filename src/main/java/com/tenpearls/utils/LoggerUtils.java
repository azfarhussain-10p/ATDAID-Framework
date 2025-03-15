package com.tenpearls.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for logging with Log4j2
 */
public class LoggerUtils {
    
    /**
     * Get a logger for the specified class
     * @param clazz The class to get the logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
    
    /**
     * Log an info message
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void info(Logger logger, String message) {
        logger.info(message);
    }
    
    /**
     * Log an info message with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void info(Logger logger, String message, Object... params) {
        logger.info(message, params);
    }
    
    /**
     * Log a debug message
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void debug(Logger logger, String message) {
        logger.debug(message);
    }
    
    /**
     * Log a debug message with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void debug(Logger logger, String message, Object... params) {
        logger.debug(message, params);
    }
    
    /**
     * Log an error message
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void error(Logger logger, String message) {
        logger.error(message);
    }
    
    /**
     * Log an error message with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void error(Logger logger, String message, Object... params) {
        logger.error(message, params);
    }
    
    /**
     * Log an error message with an exception
     * @param logger The logger to use
     * @param message The message to log
     * @param throwable The exception to log
     */
    public static void error(Logger logger, String message, Throwable throwable) {
        logger.error(message, throwable);
    }
    
    /**
     * Log a warning message
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void warn(Logger logger, String message) {
        logger.warn(message);
    }
    
    /**
     * Log a warning message with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void warn(Logger logger, String message, Object... params) {
        logger.warn(message, params);
    }
} 