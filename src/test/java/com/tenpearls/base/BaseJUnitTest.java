package com.tenpearls.base;

import com.aventstack.extentreports.ExtentTest;
import com.tenpearls.reports.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Base class for all JUnit test classes.
 * Provides integration with Log4j2 and ExtentReports.
 */
@ExtendWith(TestLoggerExtension.class)
public abstract class BaseJUnitTest {
    
    protected Logger logger;
    protected static ExtentTest extentTest;
    
    /**
     * Constructor that initializes the logger for the specific test class.
     */
    public BaseJUnitTest() {
        logger = LogManager.getLogger(this.getClass());
    }
    
    /**
     * Method that runs before all tests in the class.
     * Initializes the ExtentReports instance.
     */
    @BeforeAll
    public static void beforeAll() {
        // Initialize ExtentReports
        ExtentReportManager.getExtentReports();
    }
    
    /**
     * Method that runs after all tests in the class.
     * Flushes the ExtentReports instance.
     */
    @AfterAll
    public static void afterAll() {
        // Flush ExtentReports
        ExtentReportManager.flushReport();
    }
    
    /**
     * Method that runs before each test method.
     * Initializes the ExtentTest instance for the specific test method.
     * 
     * @param testInfo Information about the test
     */
    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        logger.info("Starting test: {}", testName);
        
        // Get test description if available
        String description = testInfo.getDisplayName();
        
        // Create ExtentTest instance
        extentTest = ExtentReportManager.startTest(testName, description);
    }
    
    /**
     * Method that runs after each test method.
     * Updates the ExtentTest instance with the test result.
     */
    @AfterEach
    public void afterEach(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        logger.info("Finished test: {}", testName);
    }
} 