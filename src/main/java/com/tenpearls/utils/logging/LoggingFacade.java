package com.tenpearls.utils.logging;

import com.tenpearls.utils.logging.async.AsyncLogger;
import com.tenpearls.utils.logging.context.ContextLogger;
import com.tenpearls.utils.logging.core.CoreLogger;
import com.tenpearls.utils.logging.factory.LoggerFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Facade for all logging operations.
 * Provides a simplified API for logging that delegates to the appropriate logger.
 */
@Component
public class LoggingFacade {
    
    private final CoreLogger coreLogger;
    private final ContextLogger contextLogger;
    private final AsyncLogger asyncLogger;
    private final LoggerFactory loggerFactory;
    
    /**
     * Constructor that injects the required loggers
     * 
     * @param loggerFactory The logger factory
     */
    @Autowired
    public LoggingFacade(LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
        this.coreLogger = loggerFactory.getCoreLogger();
        this.contextLogger = loggerFactory.getContextLogger();
        this.asyncLogger = loggerFactory.getAsyncLogger();
    }
    
    /**
     * Log a message at the INFO level
     * 
     * @param message The message to log
     */
    public void info(String message) {
        coreLogger.info(message);
    }
    
    /**
     * Log a message with parameters at the INFO level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void info(String message, Object... params) {
        coreLogger.info(message, params);
    }
    
    /**
     * Log a message at the DEBUG level
     * 
     * @param message The message to log
     */
    public void debug(String message) {
        coreLogger.debug(message);
    }
    
    /**
     * Log a message with parameters at the DEBUG level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void debug(String message, Object... params) {
        coreLogger.debug(message, params);
    }
    
    /**
     * Log a message at the WARN level
     * 
     * @param message The message to log
     */
    public void warn(String message) {
        coreLogger.warn(message);
    }
    
    /**
     * Log a message with parameters at the WARN level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void warn(String message, Object... params) {
        coreLogger.warn(message, params);
    }
    
    /**
     * Log a message at the ERROR level
     * 
     * @param message The message to log
     */
    public void error(String message) {
        coreLogger.error(message);
    }
    
    /**
     * Log a message with parameters at the ERROR level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void error(String message, Object... params) {
        coreLogger.error(message, params);
    }
    
    /**
     * Log a message with an exception at the ERROR level
     * 
     * @param message The message
     * @param throwable The exception to log
     */
    public void error(String message, Throwable throwable) {
        coreLogger.error(message, throwable);
    }
    
    /**
     * Log a message asynchronously at the INFO level
     * 
     * @param loggerName The logger name
     * @param message The message to log
     */
    public void asyncInfo(String loggerName, String message) {
        asyncLogger.asyncLog(Level.INFO, loggerName, message);
    }
    
    /**
     * Log a message with parameters asynchronously at the INFO level
     * 
     * @param loggerName The logger name
     * @param message The message format
     * @param params The parameters for the message
     */
    public void asyncInfo(String loggerName, String message, Object... params) {
        asyncLogger.asyncLog(Level.INFO, loggerName, message, params);
    }
    
    /**
     * Log a message asynchronously at the DEBUG level
     * 
     * @param loggerName The logger name
     * @param message The message to log
     */
    public void asyncDebug(String loggerName, String message) {
        asyncLogger.asyncLog(Level.DEBUG, loggerName, message);
    }
    
    /**
     * Log a message with parameters asynchronously at the DEBUG level
     * 
     * @param loggerName The logger name
     * @param message The message format
     * @param params The parameters for the message
     */
    public void asyncDebug(String loggerName, String message, Object... params) {
        asyncLogger.asyncLog(Level.DEBUG, loggerName, message, params);
    }
    
    /**
     * Log a message asynchronously at the WARN level
     * 
     * @param loggerName The logger name
     * @param message The message to log
     */
    public void asyncWarn(String loggerName, String message) {
        asyncLogger.asyncLog(Level.WARN, loggerName, message);
    }
    
    /**
     * Log a message with parameters asynchronously at the WARN level
     * 
     * @param loggerName The logger name
     * @param message The message format
     * @param params The parameters for the message
     */
    public void asyncWarn(String loggerName, String message, Object... params) {
        asyncLogger.asyncLog(Level.WARN, loggerName, message, params);
    }
    
    /**
     * Log a message asynchronously at the ERROR level
     * 
     * @param loggerName The logger name
     * @param message The message to log
     */
    public void asyncError(String loggerName, String message) {
        asyncLogger.asyncLog(Level.ERROR, loggerName, message);
    }
    
    /**
     * Log a message with parameters asynchronously at the ERROR level
     * 
     * @param loggerName The logger name
     * @param message The message format
     * @param params The parameters for the message
     */
    public void asyncError(String loggerName, String message, Object... params) {
        asyncLogger.asyncLog(Level.ERROR, loggerName, message, params);
    }
    
    /**
     * Log a message with an exception asynchronously at the ERROR level
     * 
     * @param loggerName The logger name
     * @param message The message
     * @param throwable The exception to log
     */
    public void asyncError(String loggerName, String message, Throwable throwable) {
        asyncLogger.asyncLog(Level.ERROR, loggerName, message, throwable);
    }
    
    /**
     * Start a new logging context with a correlation ID for tracking related log entries
     * 
     * @return The generated correlation ID
     */
    public String startContext() {
        return contextLogger.startContext();
    }
    
    /**
     * Start a new logging context with a specified correlation ID
     * 
     * @param correlationId The correlation ID to use
     */
    public void startContext(String correlationId) {
        contextLogger.startContext(correlationId);
    }
    
    /**
     * End the current logging context
     */
    public void endContext() {
        contextLogger.endContext();
    }
    
    /**
     * Add a key-value pair to the current logging context
     * 
     * @param key The key
     * @param value The value
     */
    public void putContext(String key, String value) {
        contextLogger.putContext(key, value);
    }
    
    /**
     * Add multiple key-value pairs to the current logging context
     * 
     * @param contextMap The map of key-value pairs to add
     */
    public void putAllContext(Map<String, String> contextMap) {
        contextLogger.putAllContext(contextMap);
    }
    
    /**
     * Get a value from the current logging context
     * 
     * @param key The key
     * @return The value, or null if not found
     */
    public String getContext(String key) {
        return contextLogger.getContext(key);
    }
    
    /**
     * Get the current correlation ID
     * 
     * @return The correlation ID, or null if not set
     */
    public String getCorrelationId() {
        return contextLogger.getCorrelationId();
    }
    
    /**
     * Get a Log4j2 logger for the specified class
     * 
     * @param clazz The class to get the logger for
     * @return Logger instance
     */
    public Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Get a Log4j2 logger for the specified name
     * 
     * @param name The name to get the logger for
     * @return Logger instance
     */
    public Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
} 