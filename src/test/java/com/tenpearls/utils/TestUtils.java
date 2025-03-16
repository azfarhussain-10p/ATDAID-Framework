package com.tenpearls.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class for tests.
 * Provides common utility methods for tests.
 */
public class TestUtils {
    
    private static final String SCREENSHOTS_DIR = "test-output/screenshots";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    static {
        // Ensure screenshots directory exists
        createDirectory(SCREENSHOTS_DIR);
    }
    
    /**
     * Create a directory if it doesn't exist
     * 
     * @param dirPath The directory path
     */
    public static void createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Generate a unique ID
     * 
     * @return A unique ID
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Get the current timestamp as a formatted string
     * 
     * @return The current timestamp
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Take a screenshot and save it to the screenshots directory
     * 
     * @param testName The name of the test
     * @return The path to the screenshot file
     * @throws IOException If an I/O error occurs
     */
    public static String takeScreenshot(String testName) throws IOException {
        // This is a placeholder for actual screenshot functionality
        // In a real implementation, this would use Selenium WebDriver or similar
        
        String fileName = SCREENSHOTS_DIR + File.separator + 
                testName + "_" + getCurrentTimestamp() + ".png";
        
        // Create an empty file as a placeholder
        Files.createFile(Paths.get(fileName));
        
        return fileName;
    }
    
    /**
     * Log the test result
     * 
     * @param logger The logger
     * @param extentTest The ExtentTest instance
     * @param result The test result
     */
    public static void logTestResult(Logger logger, ExtentTest extentTest, ITestResult result) {
        String testName = result.getName();
        
        if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("Test passed: {}", testName);
            extentTest.log(Status.PASS, "Test passed");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test failed: {}", testName);
            if (result.getThrowable() != null) {
                logger.error("Exception: ", result.getThrowable());
                extentTest.log(Status.FAIL, result.getThrowable());
                
                // Take screenshot on failure
                try {
                    String screenshotPath = takeScreenshot(testName);
                    extentTest.addScreenCaptureFromPath(screenshotPath);
                } catch (IOException e) {
                    logger.error("Failed to add screenshot to report", e);
                }
            }
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.info("Test skipped: {}", testName);
            extentTest.log(Status.SKIP, "Test skipped");
        }
    }
    
    /**
     * Add a test step to the report
     * 
     * @param logger The logger
     * @param extentTest The ExtentTest instance
     * @param stepName The name of the step
     * @param stepDescription The description of the step
     * @param success Whether the step was successful
     */
    public static void addTestStep(Logger logger, ExtentTest extentTest, 
            String stepName, String stepDescription, boolean success) {
        if (success) {
            logger.info("Step passed: {}", stepName);
            extentTest.log(Status.PASS, stepName + ": " + stepDescription);
        } else {
            logger.error("Step failed: {}", stepName);
            extentTest.log(Status.FAIL, stepName + ": " + stepDescription);
            
            // Take screenshot on step failure
            try {
                String screenshotPath = takeScreenshot(stepName);
                extentTest.addScreenCaptureFromPath(screenshotPath);
            } catch (IOException e) {
                logger.error("Failed to add screenshot to report", e);
            }
        }
    }
    
    /**
     * Clean up test resources
     * 
     * @param testName The name of the test
     */
    public static void cleanupTestResources(String testName) {
        // This is a placeholder for actual cleanup functionality
        // In a real implementation, this would clean up any resources created by the test
        
        // For example, delete temporary files
        try {
            Files.walk(Paths.get(SCREENSHOTS_DIR))
                    .filter(path -> path.toString().contains(testName))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            // Ignore
                        }
                    });
        } catch (IOException e) {
            // Ignore
        }
    }
} 