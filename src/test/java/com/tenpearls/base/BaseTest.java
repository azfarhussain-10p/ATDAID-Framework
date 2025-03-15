package com.tenpearls.base;

import com.aventstack.extentreports.ExtentTest;
import com.tenpearls.reports.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

/**
 * Base class for all test classes.
 * Provides integration with Log4j2 and ExtentReports.
 */
public abstract class BaseTest {
    
    protected Logger logger;
    protected ExtentTest extentTest;
    
    /**
     * Constructor that initializes the logger for the specific test class.
     */
    public BaseTest() {
        logger = LogManager.getLogger(this.getClass());
    }
    
    /**
     * Method that runs before the test suite.
     * Initializes the ExtentReports instance.
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("Starting test suite execution");
        // Initialize ExtentReports
        ExtentReportManager.getExtentReports();
    }
    
    /**
     * Method that runs after the test suite.
     * Flushes the ExtentReports instance.
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        logger.info("Finishing test suite execution");
        // Flush ExtentReports
        ExtentReportManager.flushReport();
    }
    
    /**
     * Method that runs before each test method.
     * Initializes the ExtentTest instance for the specific test method.
     * 
     * @param method The test method
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        String testName = method.getName();
        logger.info("Starting test: {}", testName);
        
        // Get test description if available
        String description = "";
        if (method.isAnnotationPresent(org.testng.annotations.Test.class)) {
            description = method.getAnnotation(org.testng.annotations.Test.class).description();
        }
        
        // Create ExtentTest instance
        extentTest = ExtentReportManager.startTest(testName, description);
    }
    
    /**
     * Method that runs after each test method.
     * Updates the ExtentTest instance with the test result.
     * 
     * @param result The test result
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        String testName = result.getName();
        
        if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("Test passed: {}", testName);
            extentTest.pass("Test passed");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test failed: {}", testName);
            if (result.getThrowable() != null) {
                logger.error("Exception: ", result.getThrowable());
                extentTest.fail(result.getThrowable());
            }
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.info("Test skipped: {}", testName);
            extentTest.skip("Test skipped");
        }
    }
} 