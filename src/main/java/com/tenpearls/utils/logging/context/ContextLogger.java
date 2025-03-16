package com.tenpearls.utils.logging.context;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Context-aware logging functionality that provides the ability to track related log entries
 * using correlation IDs and other context information.
 */
@Component
public class ContextLogger {
    
    private static final Logger logger = LogManager.getLogger(ContextLogger.class);
    
    /**
     * Start a new logging context with a correlation ID for tracking related log entries
     * 
     * @return The generated correlation ID
     */
    public String startContext() {
        String correlationId = UUID.randomUUID().toString();
        ThreadContext.put("correlationId", correlationId);
        return correlationId;
    }
    
    /**
     * Start a new logging context with a specified correlation ID
     * 
     * @param correlationId The correlation ID to use
     */
    public void startContext(String correlationId) {
        ThreadContext.put("correlationId", correlationId);
    }
    
    /**
     * End the current logging context
     */
    public void endContext() {
        ThreadContext.clearAll();
    }
    
    /**
     * Add a key-value pair to the current logging context
     * 
     * @param key The key
     * @param value The value
     */
    public void putContext(String key, String value) {
        ThreadContext.put(key, value);
    }
    
    /**
     * Add multiple key-value pairs to the current logging context
     * 
     * @param contextMap The map of key-value pairs to add
     */
    public void putAllContext(Map<String, String> contextMap) {
        contextMap.forEach(ThreadContext::put);
    }
    
    /**
     * Get a value from the current logging context
     * 
     * @param key The key
     * @return The value, or null if not found
     */
    public String getContext(String key) {
        return ThreadContext.get(key);
    }
    
    /**
     * Remove a key-value pair from the current logging context
     * 
     * @param key The key to remove
     */
    public void removeContext(String key) {
        ThreadContext.remove(key);
    }
    
    /**
     * Get the current correlation ID
     * 
     * @return The correlation ID, or null if not set
     */
    public String getCorrelationId() {
        return ThreadContext.get("correlationId");
    }
    
    /**
     * Log a message with the current context at the specified level
     * 
     * @param level The log level
     * @param message The message to log
     */
    public void logWithContext(Level level, String message) {
        logger.log(level, message);
    }
    
    /**
     * Log a message with parameters and the current context at the specified level
     * 
     * @param level The log level
     * @param message The message format
     * @param params The parameters for the message
     */
    public void logWithContext(Level level, String message, Object... params) {
        logger.log(level, message, params);
    }
    
    /**
     * Log a message with an exception and the current context at the specified level
     * 
     * @param level The log level
     * @param message The message
     * @param throwable The exception to log
     */
    public void logWithContext(Level level, String message, Throwable throwable) {
        logger.log(level, message, throwable);
    }
    
    /**
     * Log a message with the current context at the INFO level
     * 
     * @param message The message to log
     */
    public void infoWithContext(String message) {
        logWithContext(Level.INFO, message);
    }
    
    /**
     * Log a message with parameters and the current context at the INFO level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void infoWithContext(String message, Object... params) {
        logWithContext(Level.INFO, message, params);
    }
    
    /**
     * Log a message with the current context at the DEBUG level
     * 
     * @param message The message to log
     */
    public void debugWithContext(String message) {
        logWithContext(Level.DEBUG, message);
    }
    
    /**
     * Log a message with parameters and the current context at the DEBUG level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void debugWithContext(String message, Object... params) {
        logWithContext(Level.DEBUG, message, params);
    }
    
    /**
     * Log a message with the current context at the WARN level
     * 
     * @param message The message to log
     */
    public void warnWithContext(String message) {
        logWithContext(Level.WARN, message);
    }
    
    /**
     * Log a message with parameters and the current context at the WARN level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void warnWithContext(String message, Object... params) {
        logWithContext(Level.WARN, message, params);
    }
    
    /**
     * Log a message with the current context at the ERROR level
     * 
     * @param message The message to log
     */
    public void errorWithContext(String message) {
        logWithContext(Level.ERROR, message);
    }
    
    /**
     * Log a message with parameters and the current context at the ERROR level
     * 
     * @param message The message format
     * @param params The parameters for the message
     */
    public void errorWithContext(String message, Object... params) {
        logWithContext(Level.ERROR, message, params);
    }
    
    /**
     * Log a message with an exception and the current context at the ERROR level
     * 
     * @param message The message
     * @param throwable The exception to log
     */
    public void errorWithContext(String message, Throwable throwable) {
        logWithContext(Level.ERROR, message, throwable);
    }
} 