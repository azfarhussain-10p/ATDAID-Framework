package com.tenpearls.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.UUID;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Enhanced utility class for logging with Log4j2.
 * Provides a simplified and user-friendly interface for common logging operations.
 * Features:
 * - Simplified logging methods with consistent formatting
 * - Context-aware logging with correlation IDs
 * - Direct file logging fallback for reliability
 * - Integration with log rotation, analysis, monitoring, and performance optimization
 */
@Component
public class LoggerUtils {
    
    private static final Logger logger = LogManager.getLogger(LoggerUtils.class);
    
    private static final String LOGS_DIR = "logs";
    private static final String DAILY_DIR = LOGS_DIR + File.separator + "daily";
    private static final String TODAY_DIR = DAILY_DIR + File.separator + 
            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    private static final String FALLBACK_LOG_FILE = TODAY_DIR + File.separator + "application.log";
    
    // Performance optimization
    private static LoggingPerformanceOptimizer performanceOptimizer;
    
    // Flag to enable/disable asynchronous logging
    private static boolean asyncLoggingEnabled = true;
    
    private static ExecutorService asyncLogExecutor;
    private static final int DEFAULT_THREAD_POOL_SIZE = 2;
    
    private static LoggerUtils instance;
    
    static {
        // Ensure log directories exist
        createDirectory(LOGS_DIR);
        createDirectory(DAILY_DIR);
        createDirectory(TODAY_DIR);
    }
    
    @Autowired(required = false)
    public void setPerformanceOptimizer(LoggingPerformanceOptimizer optimizer) {
        LoggerUtils.performanceOptimizer = optimizer;
        logger.info("Performance optimizer injected into LoggerUtils");
    }
    
    /**
     * Initialize the LoggerUtils
     */
    @PostConstruct
    public void init() {
        logger.info("LoggerUtils initialized");
    }
    
    /**
     * Cleanup resources on shutdown
     */
    @PreDestroy
    public void cleanup() {
        logger.info("LoggerUtils shutting down");
    }
    
    /**
     * Get a logger for the specified class
     * @param clazz The class to get the logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
    
    /**
     * Start a new logging context with a correlation ID for tracking related log entries
     * @return The generated correlation ID
     */
    public static String startContext() {
        String correlationId = UUID.randomUUID().toString();
        ThreadContext.put("correlationId", correlationId);
        return correlationId;
    }
    
    /**
     * Start a new logging context with a specified correlation ID
     * @param correlationId The correlation ID to use
     */
    public static void startContext(String correlationId) {
        ThreadContext.put("correlationId", correlationId);
    }
    
    /**
     * Add custom context data to the logging context
     * @param key The context key
     * @param value The context value
     */
    public static void addToContext(String key, String value) {
        ThreadContext.put(key, value);
    }
    
    /**
     * Add multiple custom context data entries to the logging context
     * @param contextMap Map of context keys and values
     */
    public static void addToContext(Map<String, String> contextMap) {
        for (Map.Entry<String, String> entry : contextMap.entrySet()) {
            ThreadContext.put(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Clear the current logging context
     */
    public static void clearContext() {
        ThreadContext.clearAll();
    }
    
    /**
     * Log the start of a method execution
     * @param logger The logger to use
     * @param methodName The name of the method
     */
    public static void methodStart(Logger logger, String methodName) {
        logger.info("⏱️ START: Method [{}] execution started", methodName);
    }
    
    /**
     * Log the end of a method execution with execution time
     * @param logger The logger to use
     * @param methodName The name of the method
     * @param startTimeMillis The start time in milliseconds
     */
    public static void methodEnd(Logger logger, String methodName, long startTimeMillis) {
        long executionTime = System.currentTimeMillis() - startTimeMillis;
        logger.info("⏱️ END: Method [{}] execution completed in {} ms", methodName, executionTime);
    }
    
    /**
     * Log an info message
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void info(Logger logger, String message) {
        logger.info("ℹ️ INFO: " + message);
        directLog("INFO", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("INFO", logger.getName(), "ℹ️ INFO: " + message);
        }
    }
    
    /**
     * Log an info message with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void info(Logger logger, String message, Object... params) {
        logger.info("ℹ️ INFO: " + message, params);
        directLog("INFO", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("INFO", logger.getName(), "ℹ️ INFO: " + message);
        }
    }
    
    /**
     * Log a debug message
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void debug(Logger logger, String message) {
        logger.debug("🔍 DEBUG: " + message);
        directLog("DEBUG", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("DEBUG", logger.getName(), "🔍 DEBUG: " + message);
        }
    }
    
    /**
     * Log a debug message with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void debug(Logger logger, String message, Object... params) {
        logger.debug("🔍 DEBUG: " + message, params);
        directLog("DEBUG", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("DEBUG", logger.getName(), "🔍 DEBUG: " + message);
        }
    }
    
    /**
     * Log an error message
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void error(Logger logger, String message) {
        logger.error("❌ ERROR: " + message);
        directLog("ERROR", message);
        
        // Always log errors synchronously for reliability
        // No async logging for errors
    }
    
    /**
     * Log an error message with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void error(Logger logger, String message, Object... params) {
        logger.error("❌ ERROR: " + message, params);
        directLog("ERROR", message);
        
        // Always log errors synchronously for reliability
        // No async logging for errors
    }
    
    /**
     * Log an error message with an exception
     * @param logger The logger to use
     * @param message The message to log
     * @param throwable The exception to log
     */
    public static void error(Logger logger, String message, Throwable throwable) {
        logger.error("❌ ERROR: " + message, throwable);
        directLog("ERROR", message + " - Exception: " + throwable.getMessage());
        
        // Always log errors synchronously for reliability
        // No async logging for errors
    }
    
    /**
     * Log a warning message
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void warn(Logger logger, String message) {
        logger.warn("⚠️ WARNING: " + message);
        directLog("WARNING", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("WARN", logger.getName(), "⚠️ WARNING: " + message);
        }
    }
    
    /**
     * Log a warning message with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void warn(Logger logger, String message, Object... params) {
        logger.warn("⚠️ WARNING: " + message, params);
        directLog("WARNING", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("WARN", logger.getName(), "⚠️ WARNING: " + message);
        }
    }
    
    /**
     * Log a success message
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void success(Logger logger, String message) {
        logger.info("✅ SUCCESS: " + message);
        directLog("SUCCESS", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("INFO", logger.getName(), "✅ SUCCESS: " + message);
        }
    }
    
    /**
     * Log a success message with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void success(Logger logger, String message, Object... params) {
        logger.info("✅ SUCCESS: " + message, params);
        directLog("SUCCESS", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("INFO", logger.getName(), "✅ SUCCESS: " + message);
        }
    }
    
    /**
     * Log a message about an important event
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void important(Logger logger, String message) {
        logger.info("🔔 IMPORTANT: " + message);
        directLog("IMPORTANT", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("INFO", logger.getName(), "🔔 IMPORTANT: " + message);
        }
    }
    
    /**
     * Log a message about an important event with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void important(Logger logger, String message, Object... params) {
        logger.info("🔔 IMPORTANT: " + message, params);
        directLog("IMPORTANT", message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("INFO", logger.getName(), "🔔 IMPORTANT: " + message);
        }
    }
    
    /**
     * Log a message at the specified level
     * @param logger The logger to use
     * @param level The log level
     * @param message The message to log
     */
    public static void log(Logger logger, Level level, String message) {
        logger.log(level, message);
        directLog(level.name(), message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null && 
            !level.equals(Level.ERROR) && !level.equals(Level.FATAL)) {
            performanceOptimizer.asyncLog(level.name(), logger.getName(), message);
        }
    }
    
    /**
     * Log a message at the specified level with parameters
     * @param logger The logger to use
     * @param level The log level
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void log(Logger logger, Level level, String message, Object... params) {
        logger.log(level, message, params);
        directLog(level.name(), message);
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null && 
            !level.equals(Level.ERROR) && !level.equals(Level.FATAL)) {
            performanceOptimizer.asyncLog(level.name(), logger.getName(), message);
        }
    }
    
    /**
     * Check if logging is enabled for the specified level
     * @param logger The logger to check
     * @param level The log level to check
     * @return true if logging is enabled for the specified level
     */
    public static boolean isEnabled(Logger logger, Level level) {
        return logger.isEnabled(level);
    }
    
    /**
     * Log a separator line for better log readability
     * @param logger The logger to use
     */
    public static void separator(Logger logger) {
        logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        directLog("SEPARATOR", "");
    }
    
    /**
     * Log a section header for better log organization
     * @param logger The logger to use
     * @param sectionName The name of the section
     */
    public static void section(Logger logger, String sectionName) {
        logger.info("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        logger.info("┃ {} ", sectionName);
        logger.info("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        directLog("SECTION", sectionName);
    }
    
    /**
     * Log a test step for better test log organization
     * @param logger The logger to use
     * @param stepNumber The step number
     * @param stepDescription The description of the step
     */
    public static void testStep(Logger logger, int stepNumber, String stepDescription) {
        logger.info("🔶 STEP {}: {}", stepNumber, stepDescription);
        directLog("TEST STEP", stepDescription);
    }
    
    /**
     * Log a test assertion for better test log organization
     * @param logger The logger to use
     * @param assertionDescription The description of the assertion
     */
    public static void assertion(Logger logger, String assertionDescription) {
        logger.info("✓ ASSERT: {}", assertionDescription);
        directLog("TEST ASSERTION", assertionDescription);
    }
    
    /**
     * Log a data entry for better visibility of test data
     * @param logger The logger to use
     * @param key The data key
     * @param value The data value
     */
    public static void data(Logger logger, String key, Object value) {
        logger.info("📋 DATA: {} = {}", key, value);
        directLog("DATA", key + ": " + value);
    }
    
    /**
     * Log a step in a process
     * @param logger The logger to use
     * @param step The step number
     * @param description The description of the step
     */
    public static void step(Logger logger, int step, String description) {
        logger.info("🔄 STEP " + step + ": " + description);
        directLog("PROCESS STEP", description);
    }
    
    /**
     * Log the start of a process
     * @param logger The logger to use
     * @param processName The name of the process
     */
    public static void startProcess(Logger logger, String processName) {
        logger.info("▶️ START PROCESS: " + processName);
        directLog("PROCESS START", processName);
    }
    
    /**
     * Log the end of a process
     * @param logger The logger to use
     * @param processName The name of the process
     */
    public static void endProcess(Logger logger, String processName) {
        logger.info("⏹️ END PROCESS: " + processName);
        directLog("PROCESS END", processName);
    }
    
    /**
     * Log a critical error that requires immediate attention
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void critical(Logger logger, String message) {
        logger.fatal("🚨 CRITICAL: " + message);
        directLog("CRITICAL", message);
        
        // Always log critical errors synchronously for reliability
        // No async logging for critical errors
    }
    
    /**
     * Log a critical error with parameters
     * @param logger The logger to use
     * @param message The message to log
     * @param params The parameters to include in the message
     */
    public static void critical(Logger logger, String message, Object... params) {
        logger.fatal("🚨 CRITICAL: " + message, params);
        directLog("CRITICAL", message);
        
        // Always log critical errors synchronously for reliability
        // No async logging for critical errors
    }
    
    /**
     * Log a performance metric
     * @param logger The logger to use
     * @param operation The operation being measured
     * @param durationMs The duration in milliseconds
     */
    public static void performance(Logger logger, String operation, long durationMs) {
        logger.info("⚡ PERFORMANCE: {} completed in {} ms", operation, durationMs);
        directLog("PERFORMANCE", operation + " completed in " + durationMs + " ms");
        
        // Use async logging if enabled and optimizer is available
        if (asyncLoggingEnabled && performanceOptimizer != null) {
            performanceOptimizer.asyncLog("INFO", logger.getName(), 
                    "⚡ PERFORMANCE: " + operation + " completed in " + durationMs + " ms");
        }
    }
    
    /**
     * Enable or disable asynchronous logging
     * @param enabled Whether to enable asynchronous logging
     */
    public static void setAsyncLoggingEnabled(boolean enabled) {
        asyncLoggingEnabled = enabled;
        logger.info("Asynchronous logging {}", enabled ? "enabled" : "disabled");
        if (enabled && asyncLogExecutor == null) {
            asyncLogExecutor = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        } else if (!enabled && asyncLogExecutor != null) {
            asyncLogExecutor.shutdown();
            asyncLogExecutor = null;
        }
    }
    
    /**
     * Check if asynchronous logging is enabled
     * @return true if asynchronous logging is enabled
     */
    public static boolean isAsyncLoggingEnabled() {
        return asyncLoggingEnabled;
    }
    
    /**
     * Log a message directly to a file as a fallback mechanism
     * @param level The log level
     * @param message The message to log
     */
    public static void directLog(String level, String message) {
        try {
            writeLog(FALLBACK_LOG_FILE, level, message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Creates a directory if it doesn't exist
     * @param dirPath The directory path to create
     * @return true if the directory was created, false if it already exists
     */
    private static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            try {
                boolean created = dir.mkdirs();
                System.out.println(dirPath + " directory created: " + created);
                return created;
            } catch (Exception e) {
                System.err.println("Failed to create directory: " + dirPath + " - " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    /**
     * Writes a log message to a file
     * @param filePath The file path to write to
     * @param level The log level
     * @param message The message to write
     * @throws IOException If an I/O error occurs
     */
    private static void writeLog(String filePath, String level, String message) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String loggerName = Thread.currentThread().getStackTrace()[3].getClassName();
            printWriter.println(timestamp + " [" + Thread.currentThread().getName() + "] " + 
                    level + " [" + loggerName + "] - " + message);
        }
    }
    
    /**
     * Gets the singleton instance of LoggerUtils.
     * 
     * @return the singleton instance
     */
    public static LoggerUtils getInstance() {
        if (instance == null) {
            instance = new LoggerUtils();
        }
        return instance;
    }
    
    /**
     * Logs a debug message.
     * 
     * @param message the message to log
     * @param contextData optional context data as key-value pairs
     */
    public void debug(String message, Object... contextData) {
        // Implementation would use Log4j or other logging framework
        System.out.println(formatLogMessage("DEBUG", message, contextData));
    }
    
    /**
     * Logs a debug message asynchronously.
     * 
     * @param message the message to log
     * @param contextData optional context data as key-value pairs
     */
    public void debugAsync(String message, Object... contextData) {
        // Implementation would use async logging
        System.out.println(formatLogMessage("DEBUG", message, contextData));
    }
    
    /**
     * Logs an info message.
     * 
     * @param message the message to log
     * @param contextData optional context data as key-value pairs
     */
    public void info(String message, Object... contextData) {
        // Implementation would use Log4j or other logging framework
        System.out.println(formatLogMessage("INFO", message, contextData));
    }
    
    /**
     * Logs a warning message.
     * 
     * @param message the message to log
     * @param contextData optional context data as key-value pairs
     */
    public void warning(String message, Object... contextData) {
        // Implementation would use Log4j or other logging framework
        System.out.println(formatLogMessage("WARNING", message, contextData));
    }
    
    /**
     * Logs a test step.
     * 
     * @param message the message to log
     * @param contextData optional context data as key-value pairs
     */
    public void testStep(String message, Object... contextData) {
        // Implementation would use Log4j or other logging framework
        System.out.println(formatLogMessage("TEST STEP", message, contextData));
    }
    
    /**
     * Logs a test assertion.
     * 
     * @param message the message to log
     * @param contextData optional context data as key-value pairs
     */
    public void testAssertion(String message, Object... contextData) {
        // Implementation would use Log4j or other logging framework
        System.out.println(formatLogMessage("TEST ASSERTION", message, contextData));
    }
    
    /**
     * Logs a performance message.
     * 
     * @param message the message to log
     * @param contextData optional context data as key-value pairs
     */
    public void performance(String message, Object... contextData) {
        // Implementation would use Log4j or other logging framework
        System.out.println(formatLogMessage("PERFORMANCE", message, contextData));
    }
    
    /**
     * Logs a test message.
     * 
     * @param message the message to log
     * @param contextData optional context data as key-value pairs
     */
    public void test(String message, Object... contextData) {
        // Implementation would use Log4j or other logging framework
        System.out.println(formatLogMessage("TEST", message, contextData));
    }
    
    /**
     * Formats a log message with timestamp, level, and context data.
     * 
     * @param level the log level
     * @param message the message to log
     * @param contextData optional context data as key-value pairs
     * @return the formatted log message
     */
    private String formatLogMessage(String level, String message, Object... contextData) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().toString())
          .append(" [main] ")
          .append(level)
          .append(" [com.tenpearls.utils.logging.LoggerUtils] - ")
          .append(message);
        
        if (contextData != null && contextData.length > 0) {
            sb.append(" {");
            for (int i = 0; i < contextData.length; i += 2) {
                if (i > 0) {
                    sb.append(", ");
                }
                if (i + 1 < contextData.length) {
                    sb.append(contextData[i]).append("=").append(contextData[i + 1]);
                } else {
                    sb.append(contextData[i]).append("=?");
                }
            }
            sb.append("}");
        }
        
        return sb.toString();
    }
} 