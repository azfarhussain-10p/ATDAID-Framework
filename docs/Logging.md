# Logging and Reporting in ATDAID Framework

## Overview

The ATDAID Framework uses Apache Log4j 2 for logging and ExtentReports for test reporting. This document provides an overview of the logging and reporting capabilities of the framework.

## Logging with Log4j 2

### Configuration

The Log4j 2 configuration is defined in `src/main/resources/log4j2.properties`. The configuration includes:

- Console appender for displaying logs in the console
- Rolling file appender for writing logs to files
- Logger configuration for the `com.tenpearls` package
- Root logger configuration

### Log Levels

The framework uses the following log levels:

- **ERROR**: For error events that might still allow the application to continue running
- **WARN**: For potentially harmful situations
- **INFO**: For informational messages highlighting the progress of the application
- **DEBUG**: For detailed information on the flow through the system
- **TRACE**: For more detailed information than DEBUG

### Using Logging in Your Code

To use logging in your code, follow these steps:

1. Import the Log4j 2 classes:
   ```java
   import org.apache.logging.log4j.LogManager;
   import org.apache.logging.log4j.Logger;
   ```

2. Create a logger instance:
   ```java
   private static final Logger logger = LogManager.getLogger(YourClass.class);
   ```

3. Use the logger to log messages:
   ```java
   logger.info("This is an info message");
   logger.debug("This is a debug message");
   logger.error("This is an error message", exception);
   ```

## Reporting with ExtentReports

### Overview

The framework uses ExtentReports for generating detailed HTML reports of test executions. The reports include:

- Test status (pass, fail, skip)
- Test duration
- Test logs
- Screenshots (for UI tests)
- Exception details

### Components

The reporting system consists of the following components:

1. **ExtentReportManager**: Manages the ExtentReports instance and provides methods for creating and managing tests.
2. **TestListener**: Implements the TestNG ITestListener interface to capture test events and update the report.
3. **TestLoggerExtension**: JUnit extension that captures test execution events and updates the report.

### Report Location

Reports are generated in the `test-output/reports` directory with a timestamp in the filename. The default format is:

```
test-output/reports/ExtentReport_yyyy-MM-dd_HH-mm-ss.html
```

### Executing and Viewing Reports

#### Automatic Report Generation

Reports are automatically generated when you run tests using Maven or your IDE. The framework is configured to generate reports at the end of test execution through:

1. **TestNG Listener**: For TestNG tests, the TestListener class is registered in the testng.xml file.
2. **JUnit Extension**: For JUnit tests, the TestLoggerExtension is applied to the BaseJUnitTest class.

#### Viewing Reports After Test Execution

To view the latest report after test execution:

```bash
# Windows
start test-output\reports\ExtentReport_<timestamp>.html

# Linux/Mac
open test-output/reports/ExtentReport_<timestamp>.html
```

You can also create a script to automatically open the latest report after test execution:

```bash
# Windows (save as open-latest-report.bat)
@echo off
for /f "tokens=*" %%a in ('dir /b /od test-output\reports\*.html') do set LATEST=%%a
start test-output\reports\%LATEST%

# Linux/Mac (save as open-latest-report.sh)
#!/bin/bash
LATEST=$(ls -t test-output/reports/*.html | head -1)
open $LATEST
```

#### Programmatically Generating Reports

You can also generate reports programmatically by calling the ExtentReportManager directly:

```java
import com.tenpearls.reports.ExtentReportManager;

// Initialize the report
ExtentReportManager.getExtentReports();

// Create a test
ExtentTest test = ExtentReportManager.startTest("Test Name", "Test Description");

// Log test steps
test.log(Status.INFO, "Test step information");

// Log test result
test.pass("Test passed");
// or
test.fail("Test failed");
// or
test.skip("Test skipped");

// Flush the report to disk
ExtentReportManager.flushReport();
```

### Customizing Reports

You can customize the reports by modifying the ExtentReportManager class. Some common customizations include:

1. **Changing the report theme**:
   ```java
   extent.attachReporter(htmlReporter);
   htmlReporter.config().setTheme(Theme.DARK); // or Theme.STANDARD
   ```

2. **Adding system information**:
   ```java
   extent.setSystemInfo("OS", System.getProperty("os.name"));
   extent.setSystemInfo("Java Version", System.getProperty("java.version"));
   extent.setSystemInfo("User", System.getProperty("user.name"));
   ```

3. **Customizing the report title and name**:
   ```java
   htmlReporter.config().setDocumentTitle("ATDAID Test Report");
   htmlReporter.config().setReportName("Test Execution Report");
   ```

### Screenshots

For UI tests, screenshots are captured on test failure and included in the report. Screenshots are stored in the `test-output/screenshots` directory.

To capture screenshots manually and add them to the report:

```java
// Capture screenshot (using Selenium WebDriver)
File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
String screenshotPath = "test-output/screenshots/screenshot_" + System.currentTimeMillis() + ".png";
FileUtils.copyFile(screenshot, new File(screenshotPath));

// Add screenshot to report
ExtentReportManager.getTest().addScreenCaptureFromPath(screenshotPath);
```

## Integration with Test Frameworks

### Integration with TestNG

The framework integrates with TestNG through the TestListener class, which is configured in the testng.xml file:

```xml
<listeners>
    <listener class-name="com.tenpearls.listeners.TestListener"/>
</listeners>
```

The TestListener captures the following events:

- Test start
- Test success
- Test failure
- Test skip
- Test suite start
- Test suite finish

### Integration with JUnit

The framework integrates with JUnit through the TestLoggerExtension class, which is applied to the BaseJUnitTest class:

```java
@ExtendWith(TestLoggerExtension.class)
public abstract class BaseJUnitTest {
    // ...
}
```

The TestLoggerExtension captures the following events:

- Before test execution
- After test execution
- Test success
- Test failure

### Base Test Classes

The framework provides base test classes for both TestNG and JUnit:

1. **BaseTest**: For TestNG tests
   ```java
   public class YourTestClass extends BaseTest {
       // Your test methods
   }
   ```

2. **BaseJUnitTest**: For JUnit tests
   ```java
   public class YourTestClass extends BaseJUnitTest {
       // Your test methods
   }
   ```

These base classes provide:

- Logger initialization
- ExtentReports integration
- Before/after test methods for logging and reporting
- Test result handling

## Utility Classes

The framework includes the following utility classes for logging and reporting:

- **DateTimeUtils**: Provides methods for date and time operations, used for generating timestamps for logs and reports.
- **ExtentReportManager**: Manages the ExtentReports instance and provides methods for creating and managing tests.

## Example: Complete Test with Logging and Reporting

Here's a complete example of a test class that uses the logging and reporting capabilities:

```java
import com.tenpearls.base.BaseTest;
import org.testng.annotations.Test;

public class ProductManagementTest extends BaseTest {

    @Test(groups = {"smoke", "product"})
    public void testCreateProduct_Success() {
        logger.info("Testing createProduct_Success");
        
        try {
            // Test setup
            logger.debug("Setting up test data");
            
            // Test execution
            logger.info("Executing test steps");
            
            // Assertions
            logger.debug("Verifying test results");
            
            // Log success
            logger.info("Test passed successfully");
        } catch (Exception e) {
            logger.error("Test failed with exception", e);
            throw e;
        } finally {
            logger.debug("Test cleanup");
        }
        
        logger.debug("testCreateProduct_Success test completed");
    }
}
```

## Best Practices

1. **Use appropriate log levels**: Use the appropriate log level for your messages to ensure that logs are useful and not too verbose.
2. **Include context in log messages**: Include relevant context in your log messages to make them more useful for debugging.
3. **Log exceptions**: Always log exceptions with their stack traces to help with debugging.
4. **Use parameterized logging**: Use parameterized logging to avoid string concatenation when logging is disabled:
   ```java
   logger.debug("Processing user: {}", user.getName());
   ```
5. **Review logs regularly**: Regularly review logs to identify issues and improve the application.
6. **Add meaningful test descriptions**: Use descriptive test names and add descriptions to make reports more useful:
   ```java
   @Test(description = "Verify that a product can be created successfully")
   public void testCreateProduct_Success() {
       // Test code
   }
   ```
7. **Group related tests**: Use test groups to organize tests and run related tests together:
   ```java
   @Test(groups = {"smoke", "product"})
   public void testCreateProduct_Success() {
       // Test code
   }
   ```
8. **Add screenshots for UI tests**: Capture screenshots for UI tests to help with debugging:
   ```java
   ExtentReportManager.getTest().addScreenCaptureFromPath(screenshotPath);
   ```

## Troubleshooting

### Common Issues

1. **Missing logs**: Ensure that the log4j2.properties file is in the classpath and that the log levels are set correctly.
2. **Missing reports**: Check that the ExtentReportManager is initialized and that the reports are flushed at the end of test execution.
3. **Empty reports**: Ensure that tests are properly registered with ExtentReports and that test results are logged.
4. **Report not showing all tests**: Check that all tests are using the base test classes or that the TestListener/TestLoggerExtension is properly configured.

### Debugging Logging Issues

1. Enable debug logging for Log4j 2 itself:
   ```
   -Dlog4j.debug=true
   ```

2. Check the log4j2.properties file for configuration issues.

3. Verify that the logger is properly initialized in your test classes.

### Debugging Reporting Issues

1. Check that the ExtentReportManager is properly initialized.

2. Verify that the TestListener/TestLoggerExtension is properly configured.

3. Check that the test results are properly logged.

4. Ensure that the reports directory exists and is writable. 