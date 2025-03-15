package com.tenpearls.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.tenpearls.utils.DateTimeUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages ExtentReports for test reporting.
 * Provides methods to create, manage, and flush reports.
 */
public class ExtentReportManager {
    private static ExtentReports extentReports;
    private static Map<Long, ExtentTest> testMap = new HashMap<>();
    
    /**
     * Initializes and returns the ExtentReports instance.
     * Creates a new instance if one doesn't exist.
     */
    public static synchronized ExtentReports getExtentReports() {
        if (extentReports == null) {
            extentReports = createExtentReports();
        }
        return extentReports;
    }
    
    /**
     * Creates a new ExtentReports instance with configured settings.
     */
    private static ExtentReports createExtentReports() {
        ExtentReports reports = new ExtentReports();
        
        // Create a timestamp for the report name
        String timestamp = DateTimeUtils.getCurrentDateTime();
        
        // Create the report directory if it doesn't exist
        String reportDir = "test-output" + File.separator + "reports";
        File directory = new File(reportDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Configure the HTML reporter
        String reportPath = reportDir + File.separator + "TestReport_" + timestamp + ".html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("ATDAID Framework Test Report");
        sparkReporter.config().setReportName("Test Execution Report");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setEncoding("utf-8");
        
        // Attach the reporter to the ExtentReports instance
        reports.attachReporter(sparkReporter);
        
        // Set system info
        reports.setSystemInfo("OS", System.getProperty("os.name"));
        reports.setSystemInfo("Java Version", System.getProperty("java.version"));
        reports.setSystemInfo("User", System.getProperty("user.name"));
        
        return reports;
    }
    
    /**
     * Starts a new test and adds it to the test map.
     * 
     * @param testName The name of the test
     * @param description The test description
     * @return The created ExtentTest instance
     */
    public static synchronized ExtentTest startTest(String testName, String description) {
        ExtentTest test = getExtentReports().createTest(testName, description);
        testMap.put(Thread.currentThread().getId(), test);
        return test;
    }
    
    /**
     * Gets the current test for the executing thread.
     * 
     * @return The ExtentTest instance for the current thread
     */
    public static synchronized ExtentTest getTest() {
        return testMap.get(Thread.currentThread().getId());
    }
    
    /**
     * Flushes the report, writing all test results to the report file.
     */
    public static synchronized void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
} 