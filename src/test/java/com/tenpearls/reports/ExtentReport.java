package com.tenpearls.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ExtentReport {
    private static ExtentReports extentReports;
    private static Map<Long, ExtentTest> extentTestMap = new HashMap<>();

    public static synchronized ExtentReports getExtentReports() {
        if (extentReports == null) {
            extentReports = new ExtentReports();
            String reportDir = "test-output" + File.separator + "reports";
            File directory = new File(reportDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String reportFilePath = reportDir + File.separator + "TestReport_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
            sparkReporter.config().setDocumentTitle("ATDAID Framework Test Report");
            sparkReporter.config().setReportName("Test Automation Report");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
            
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Framework", "ATDAID Framework");
        }
        return extentReports;
    }

    public static synchronized ExtentTest startTest(String testName, String description) {
        ExtentTest test = getExtentReports().createTest(testName, description);
        extentTestMap.put(Thread.currentThread().getId(), test);
        return test;
    }

    public static synchronized ExtentTest getTest() {
        return extentTestMap.get(Thread.currentThread().getId());
    }
} 