package com.tenpearls.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Test class to verify logging functionality.
 * This class will write logs at different levels to test the logging configuration.
 */
@Component
public class LoggingTest implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(LoggingTest.class);

    @Override
    public void run(String... args) {
        // Generate test logs at different levels
        logger.trace("This is a TRACE level message");
        logger.debug("This is a DEBUG level message");
        logger.info("This is an INFO level message");
        logger.warn("This is a WARN level message");
        logger.error("This is an ERROR level message");
        
        // Use LoggerUtils for enhanced logging
        LoggerUtils.startContext();
        LoggerUtils.info(logger, "This is an enhanced INFO message");
        LoggerUtils.debug(logger, "This is an enhanced DEBUG message");
        LoggerUtils.error(logger, "This is an enhanced ERROR message");
        LoggerUtils.warn(logger, "This is an enhanced WARN message");
        LoggerUtils.success(logger, "This is a SUCCESS message");
        LoggerUtils.important(logger, "This is an IMPORTANT message");
        
        // Add context data
        LoggerUtils.addToContext("testKey", "testValue");
        logger.info("This message should include context data");
        
        // Log a section
        LoggerUtils.section(logger, "TEST SECTION");
        
        // Log test steps
        LoggerUtils.testStep(logger, 1, "First test step");
        LoggerUtils.testStep(logger, 2, "Second test step");
        
        // Log assertions
        LoggerUtils.assertion(logger, "Value should be equal to expected");
        
        // Log data
        LoggerUtils.data(logger, "User", "John Doe");
        LoggerUtils.data(logger, "Role", "Admin");
        
        // Log separator
        LoggerUtils.separator(logger);
        
        // Clear context
        LoggerUtils.clearContext();
        
        logger.info("Logging test completed");
    }
} 