package com.tenpearls.base;

import com.aventstack.extentreports.Status;
import com.tenpearls.reports.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * JUnit extension for logging test execution and updating ExtentReports.
 */
public class TestLoggerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    
    private static final Logger logger = LogManager.getLogger(TestLoggerExtension.class);
    
    /**
     * Method that runs before test execution.
     * Logs the test start.
     * 
     * @param context The extension context
     */
    @Override
    public void beforeTestExecution(ExtensionContext context) {
        String testName = getTestName(context);
        logger.info("Executing test: {}", testName);
    }
    
    /**
     * Method that runs after test execution.
     * Logs the test result and updates ExtentReports.
     * 
     * @param context The extension context
     */
    @Override
    public void afterTestExecution(ExtensionContext context) {
        String testName = getTestName(context);
        
        // Check if the test failed
        if (context.getExecutionException().isPresent()) {
            Throwable exception = context.getExecutionException().get();
            logger.error("Test failed: {}", testName);
            logger.error("Exception: ", exception);
            
            // Update ExtentReports
            ExtentReportManager.getTest().log(Status.FAIL, "Test failed");
            
            // Log the exception details
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            exception.printStackTrace(printWriter);
            ExtentReportManager.getTest().fail(exception);
        } else {
            logger.info("Test passed: {}", testName);
            
            // Update ExtentReports
            ExtentReportManager.getTest().log(Status.PASS, "Test passed");
        }
    }
    
    /**
     * Gets the test name from the extension context.
     * 
     * @param context The extension context
     * @return The test name
     */
    private String getTestName(ExtensionContext context) {
        return context.getRequiredTestMethod().getName();
    }
} 