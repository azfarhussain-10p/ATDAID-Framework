package com.tenpearls.utils.logging.async;

import org.apache.logging.log4j.Level;

import java.time.LocalDateTime;

/**
 * Represents a log entry for asynchronous logging.
 * Contains all the information needed to log a message.
 */
public class LogEntry {
    
    private final Level level;
    private final String loggerName;
    private final String message;
    private final Object[] params;
    private final Throwable throwable;
    private final LocalDateTime timestamp;
    
    /**
     * Constructor for a log entry with a message
     * 
     * @param level The log level
     * @param loggerName The logger name
     * @param message The message
     * @param params The parameters for the message (can be null)
     */
    public LogEntry(Level level, String loggerName, String message, Object[] params) {
        this.level = level;
        this.loggerName = loggerName;
        this.message = message;
        this.params = params;
        this.throwable = null;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor for a log entry with an exception
     * 
     * @param level The log level
     * @param loggerName The logger name
     * @param message The message
     * @param throwable The exception
     */
    public LogEntry(Level level, String loggerName, String message, Throwable throwable) {
        this.level = level;
        this.loggerName = loggerName;
        this.message = message;
        this.params = null;
        this.throwable = throwable;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Get the log level
     * 
     * @return The log level
     */
    public Level getLevel() {
        return level;
    }
    
    /**
     * Get the logger name
     * 
     * @return The logger name
     */
    public String getLoggerName() {
        return loggerName;
    }
    
    /**
     * Get the message
     * 
     * @return The message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Get the parameters
     * 
     * @return The parameters
     */
    public Object[] getParams() {
        return params;
    }
    
    /**
     * Get the exception
     * 
     * @return The exception
     */
    public Throwable getThrowable() {
        return throwable;
    }
    
    /**
     * Get the timestamp
     * 
     * @return The timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
} 