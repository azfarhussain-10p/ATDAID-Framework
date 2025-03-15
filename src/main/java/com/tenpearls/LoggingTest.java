package com.tenpearls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.status.StatusLogger;

import java.io.File;
import java.net.URL;
import java.util.Map;

/**
 * A simple class to test Log4j2 logging functionality.
 */
public class LoggingTest {
    private static final Logger logger = LogManager.getLogger(LoggingTest.class);

    public static void main(String[] args) {
        System.out.println("Starting LoggingTest...");
        System.out.println("Current directory: " + System.getProperty("user.dir"));
        
        // Print Log4j2 configuration information
        System.out.println("\n--- Log4j2 Configuration Information ---");
        
        // Print system properties related to Log4j2
        System.out.println("log4j.configurationFile: " + System.getProperty("log4j.configurationFile"));
        System.out.println("log4j2.debug: " + System.getProperty("log4j2.debug"));
        System.out.println("log4j2.configurationFile: " + System.getProperty("log4j2.configurationFile"));
        
        // Check if log4j2.properties exists in the classpath
        URL configUrl = LoggingTest.class.getClassLoader().getResource("log4j2.properties");
        System.out.println("log4j2.properties in classpath: " + (configUrl != null ? configUrl.getPath() : "Not found"));
        
        // Check if the logs directory exists
        File logsDir = new File("logs");
        System.out.println("Logs directory exists: " + logsDir.exists());
        System.out.println("Logs directory path: " + logsDir.getAbsolutePath());
        
        // Get the current LoggerContext
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        System.out.println("Configuration name: " + config.getName());
        System.out.println("Configuration class: " + config.getClass().getName());
        
        // Print all appenders
        System.out.println("\nConfigured Appenders:");
        config.getAppenders().forEach((name, appender) -> {
            System.out.println("  - " + name + ": " + appender.getClass().getSimpleName() + " -> " + appender.getLayout());
        });
        
        // Print all loggers
        System.out.println("\nConfigured Loggers:");
        Map<String, org.apache.logging.log4j.core.config.LoggerConfig> loggers = config.getLoggers();
        loggers.forEach((name, loggerConfig) -> {
            System.out.println("  - " + name + " (Level: " + loggerConfig.getLevel() + ")");
            loggerConfig.getAppenderRefs().forEach(ref -> {
                System.out.println("    * Appender Ref: " + ref.getRef());
            });
        });
        
        System.out.println("\n--- End of Log4j2 Configuration Information ---\n");
        
        // Log messages at different levels
        logger.trace("This is a TRACE level message");
        logger.debug("This is a DEBUG level message");
        logger.info("This is an INFO level message");
        logger.warn("This is a WARN level message");
        logger.error("This is an ERROR level message");
        logger.fatal("This is a FATAL level message");
        
        // Log with exception
        try {
            throw new RuntimeException("Test exception");
        } catch (Exception e) {
            logger.error("This is an ERROR with exception", e);
        }
        
        System.out.println("LoggingTest completed. Check logs directory for output.");
    }
} 