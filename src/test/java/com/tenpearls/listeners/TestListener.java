package com.tenpearls.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.tenpearls.reports.ExtentReportManager;
import com.tenpearls.utils.DateTimeUtils;

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

/**
 * TestNG listener for test execution events.
 * Integrates with Log4j2 for logging and ExtentReports for reporting.
 */
public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Starting test: {}", result.getName());
        ExtentReportManager.startTest(result.getName(), result.getMethod().getDescription())
                .assignCategory(result.getTestClass().getName())
                .assignAuthor("ATDAID Framework");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test passed: {}", result.getName());
        ExtentReportManager.getTest().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test failed: {}", result.getName());
        
        // Log the exception
        if (result.getThrowable() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            result.getThrowable().printStackTrace(printWriter);
            logger.error("Exception: {}", stringWriter.toString());
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
            }
        } catch (IOException e) {
            logger.error("Failed to capture or attach screenshot: {}", e.getMessage());
        }
        
        // Log the exception in the report
        ExtentReportManager.getTest().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info("Test skipped: {}", result.getName());
        ExtentReportManager.getTest().log(Status.SKIP, "Test Skipped");
        if (result.getThrowable() != null) {
            ExtentReportManager.getTest().skip(result.getThrowable());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.info("Test failed but within success percentage: {}", result.getName());
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("Starting test suite: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Finishing test suite: {}", context.getName());
        logger.info("Passed tests: {}", context.getPassedTests().size());
        logger.info("Failed tests: {}", context.getFailedTests().size());
        logger.info("Skipped tests: {}", context.getSkippedTests().size());
        
        // Flush the Extent Report
        ExtentReportManager.flushReport();
    }
} 