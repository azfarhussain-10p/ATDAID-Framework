package com.tenpearls.base;

import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.Logger;

/**
 * Base interface for all test classes.
 * Defines common functionality that all test classes should implement.
 */
public interface TestBase {
    
    /**
     * Get the logger for the test class
     * 
     * @return The logger
     */
    Logger getLogger();
    
    /**
     * Get the ExtentTest instance for the test method
     * 
     * @return The ExtentTest instance
     */
    ExtentTest getExtentTest();
    
    /**
     * Set up the test environment before the test suite
     */
    void beforeSuite();
    
    /**
     * Clean up the test environment after the test suite
     */
    void afterSuite();
    
    /**
     * Set up the test environment before each test method
     * 
     * @param testName The name of the test method
     * @param description The description of the test method
     */
    void beforeMethod(String testName, String description);
    
    /**
     * Clean up the test environment after each test method
     * 
     * @param testName The name of the test method
     * @param success Whether the test was successful
     * @param throwable The exception thrown by the test, if any
     */
    void afterMethod(String testName, boolean success, Throwable throwable);
    
    /**
     * Log a message at the INFO level
     * 
     * @param message The message to log
     */
    void logInfo(String message);
    
    /**
     * Log a message at the DEBUG level
     * 
     * @param message The message to log
     */
    void logDebug(String message);
    
    /**
     * Log a message at the WARN level
     * 
     * @param message The message to log
     */
    void logWarn(String message);
    
    /**
     * Log a message at the ERROR level
     * 
     * @param message The message to log
     */
    void logError(String message);
    
    /**
     * Log a message with an exception at the ERROR level
     * 
     * @param message The message to log
     * @param throwable The exception to log
     */
    void logError(String message, Throwable throwable);
    
    /**
     * Add a step to the test report
     * 
     * @param stepName The name of the step
     * @param stepDescription The description of the step
     * @param success Whether the step was successful
     */
    void addTestStep(String stepName, String stepDescription, boolean success);
} 