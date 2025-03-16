package com.tenpearls.utils.logging.factory;

import com.tenpearls.utils.logging.async.AsyncLogger;
import com.tenpearls.utils.logging.context.ContextLogger;
import com.tenpearls.utils.logging.core.CoreLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory for creating loggers.
 * Provides a unified interface for creating different types of loggers.
 */
@Component
public class LoggerFactory {
    
    private final CoreLogger coreLogger;
    private final ContextLogger contextLogger;
    private final AsyncLogger asyncLogger;
    
    /**
     * Constructor that injects the required loggers
     * 
     * @param coreLogger The core logger
     * @param contextLogger The context logger
     * @param asyncLogger The async logger
     */
    @Autowired
    public LoggerFactory(CoreLogger coreLogger, ContextLogger contextLogger, AsyncLogger asyncLogger) {
        this.coreLogger = coreLogger;
        this.contextLogger = contextLogger;
        this.asyncLogger = asyncLogger;
    }
    
    /**
     * Get a core logger
     * 
     * @return The core logger
     */
    public CoreLogger getCoreLogger() {
        return coreLogger;
    }
    
    /**
     * Get a context logger
     * 
     * @return The context logger
     */
    public ContextLogger getContextLogger() {
        return contextLogger;
    }
    
    /**
     * Get an async logger
     * 
     * @return The async logger
     */
    public AsyncLogger getAsyncLogger() {
        return asyncLogger;
    }
    
    /**
     * Get a Log4j2 logger for the specified class
     * 
     * @param clazz The class to get the logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
    
    /**
     * Get a Log4j2 logger for the specified name
     * 
     * @param name The name to get the logger for
     * @return Logger instance
     */
    public static Logger getLogger(String name) {
        return LogManager.getLogger(name);
    }
} 