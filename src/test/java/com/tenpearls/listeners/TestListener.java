package com.tenpearls.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.tenpearls.reports.ExtentReportManager;
import com.tenpearls.utils.time.DateTimeUtils;
import com.tenpearls.utils.logging.LoggerUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * TestNG listener for test execution events.
 * Integrates with Log4j2 for logging and ExtentReports for reporting.
 */
public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        // Generate a correlation ID for this test
        String correlationId = LoggerUtils.startContext();
        
        // Add test metadata to the logging context
        Map<String, String> contextData = new HashMap<>();
        contextData.put("testName", result.getName());
        contextData.put("testClass", result.getTestClass().getName());
        contextData.put("testMethod", result.getMethod().getMethodName());
        LoggerUtils.addToContext(contextData);
        
        // Log test start with enhanced formatting
        LoggerUtils.section(logger, "TEST STARTED: " + result.getName());
        LoggerUtils.data(logger, "Test Class", result.getTestClass().getName());
        LoggerUtils.data(logger, "Test Method", result.getMethod().getMethodName());
        LoggerUtils.data(logger, "Correlation ID", correlationId);
        
        if (result.getMethod().getDescription() != null && !result.getMethod().getDescription().isEmpty()) {
            LoggerUtils.data(logger, "Description", result.getMethod().getDescription());
        }
        
        // Create ExtentTest and assign metadata
        ExtentReportManager.startTest(result.getName(), result.getMethod().getDescription())
                .assignCategory(result.getTestClass().getName())
                .assignAuthor("ATDAID Framework");
                
        // Store correlation ID in the report as a tag
        ExtentReportManager.getTest().assignCategory("CorrelationID: " + correlationId);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LoggerUtils.success(logger, "Test passed: " + result.getName());
        LoggerUtils.data(logger, "Duration", (result.getEndMillis() - result.getStartMillis()) + " ms");
        LoggerUtils.separator(logger);
        
        ExtentReportManager.getTest().log(Status.PASS, "Test Passed");
        ExtentReportManager.getTest().log(Status.INFO, "Test Duration: " + (result.getEndMillis() - result.getStartMillis()) + " ms");
        
        // Clear the logging context
        LoggerUtils.clearContext();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LoggerUtils.error(logger, "Test failed: " + result.getName());
        LoggerUtils.data(logger, "Duration", (result.getEndMillis() - result.getStartMillis()) + " ms");
        
        // Log the exception
        if (result.getThrowable() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            result.getThrowable().printStackTrace(printWriter);
            LoggerUtils.error(logger, "Exception: " + stringWriter.toString());
        }

        // Capture screenshot if WebDriver is available
        try {
            // Get the test class name and method name
            String className = result.getTestClass().getRealClass().getSimpleName();
            String methodName = result.getName();
            
            // Create screenshot directory if it doesn't exist
            String screenshotDir = "test-output" + File.separator + "screenshots" + File.separator + 
                    DateTimeUtils.getCurrentDate() + File.separator + className;
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Define screenshot path
            String screenshotPath = screenshotDir + File.separator + methodName + ".png";
            
            // For web tests, you can capture screenshots using WebDriver
            // This is a placeholder for the actual screenshot capture logic
            // WebDriver driver = DriverManager.getDriver();
            // if (driver != null) {
            //     File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            //     FileUtils.copyFile(screenshot, new File(screenshotPath));
            // }
            
            // Add screenshot to report (if available)
            File screenshotFile = new File(screenshotPath);
            if (screenshotFile.exists()) {
                byte[] fileContent = FileUtils.readFileToByteArray(screenshotFile);
                String base64Image = new String(Base64.encodeBase64(fileContent), StandardCharsets.US_ASCII);
                
                // Add screenshot to TestNG report
                Reporter.log("<a href='" + screenshotPath + "'><img src='" + screenshotPath + "' height='100' width='100'/></a>");
                
                // Add screenshot to Extent Report
                ExtentReportManager.getTest().fail("Test Failed",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                ExtentReportManager.getTest().fail("Test Failed",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image).build());
                
                LoggerUtils.info(logger, "Screenshot captured: " + screenshotPath);
            }
        } catch (IOException e) {
            LoggerUtils.error(logger, "Failed to capture or attach screenshot: " + e.getMessage());
        }
        
        // Log the exception in the report
        ExtentReportManager.getTest().fail(result.getThrowable());
        ExtentReportManager.getTest().log(Status.INFO, "Test Duration: " + (result.getEndMillis() - result.getStartMillis()) + " ms");
        
        LoggerUtils.separator(logger);
        
        // Clear the logging context
        LoggerUtils.clearContext();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LoggerUtils.warn(logger, "Test skipped: " + result.getName());
        LoggerUtils.data(logger, "Duration", (result.getEndMillis() - result.getStartMillis()) + " ms");
        
        if (result.getThrowable() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            result.getThrowable().printStackTrace(printWriter);
            LoggerUtils.warn(logger, "Skip reason: " + stringWriter.toString());
            
            ExtentReportManager.getTest().skip(result.getThrowable());
        } else {
            ExtentReportManager.getTest().log(Status.SKIP, "Test Skipped");
        }
        
        ExtentReportManager.getTest().log(Status.INFO, "Test Duration: " + (result.getEndMillis() - result.getStartMillis()) + " ms");
        
        LoggerUtils.separator(logger);
        
        // Clear the logging context
        LoggerUtils.clearContext();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        LoggerUtils.warn(logger, "Test failed but within success percentage: " + result.getName());
        LoggerUtils.data(logger, "Duration", (result.getEndMillis() - result.getStartMillis()) + " ms");
        LoggerUtils.separator(logger);
        
        // Clear the logging context
        LoggerUtils.clearContext();
    }

    @Override
    public void onStart(ITestContext context) {
        LoggerUtils.section(logger, "TEST SUITE STARTED: " + context.getName());
        LoggerUtils.data(logger, "Start Time", DateTimeUtils.formatDate(context.getStartDate(), "yyyy-MM-dd HH:mm:ss"));
        LoggerUtils.data(logger, "Total Tests", context.getAllTestMethods().length);
    }

    @Override
    public void onFinish(ITestContext context) {
        LoggerUtils.section(logger, "TEST SUITE FINISHED: " + context.getName());
        LoggerUtils.data(logger, "End Time", DateTimeUtils.formatDate(context.getEndDate(), "yyyy-MM-dd HH:mm:ss"));
        LoggerUtils.data(logger, "Duration", (context.getEndDate().getTime() - context.getStartDate().getTime()) + " ms");
        LoggerUtils.data(logger, "Passed Tests", context.getPassedTests().size());
        LoggerUtils.data(logger, "Failed Tests", context.getFailedTests().size());
        LoggerUtils.data(logger, "Skipped Tests", context.getSkippedTests().size());
        
        // Flush the Extent Report
        ExtentReportManager.flushReport();
    }
} 