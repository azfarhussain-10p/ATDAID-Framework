package com.tenpearls;

import com.tenpearls.utils.logging.LoggerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple class to test the LoggerUtils class.
 */
public class LoggerUtilsTest {
    private static final Logger logger = LogManager.getLogger(LoggerUtilsTest.class);
    
    public static void main(String[] args) {
        System.out.println("Starting LoggerUtilsTest...");
        System.out.println("Current directory: " + System.getProperty("user.dir"));
        
        // Test different log methods
        LoggerUtils.info(logger, "This is an info message");
        LoggerUtils.debug(logger, "This is a debug message");
        LoggerUtils.error(logger, "This is an error message");
        LoggerUtils.warn(logger, "This is a warning message");
        LoggerUtils.success(logger, "This is a success message");
        LoggerUtils.important(logger, "This is an important message");
        
        // Test with parameters
        LoggerUtils.info(logger, "This is an info message with parameter: {}", "param1");
        LoggerUtils.debug(logger, "This is a debug message with parameters: {} and {}", "param1", "param2");
        
        // Test with exception
        try {
            throw new RuntimeException("Test exception");
        } catch (Exception e) {
            LoggerUtils.error(logger, "This is an error message with exception", e);
        }
        
        // Test section and separator
        LoggerUtils.separator(logger);
        LoggerUtils.section(logger, "Test Section");
        
        // Test test-related methods
        LoggerUtils.testStep(logger, 1, "Initialize test data");
        LoggerUtils.assertion(logger, "Data should be initialized correctly");
        LoggerUtils.data(logger, "TestData", "Sample data value");
        
        // Test process-related methods
        LoggerUtils.startProcess(logger, "Sample Process");
        LoggerUtils.step(logger, 1, "First step of the process");
        LoggerUtils.step(logger, 2, "Second step of the process");
        LoggerUtils.endProcess(logger, "Sample Process");
        
        System.out.println("LoggerUtilsTest completed. Check logs directory for output.");
    }
} 