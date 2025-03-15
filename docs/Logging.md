# Logging and Reporting Guide

This guide provides detailed information about the logging and reporting capabilities of the ATDAID Framework.

## Logging with Log4j2

The ATDAID Framework uses Apache Log4j 2 for comprehensive logging. The logging system is designed to be user-friendly, detailed, and organized to facilitate debugging and troubleshooting.

### Log Directory Structure

Logs are organized in a structured directory hierarchy:

```
logs/
├── application.log       # Main application log file
├── error.log            # Error-specific log file
├── debug.log            # Detailed debug log file
├── archive/             # Archived log files (older than current day)
│   ├── application-2023-01-01-1.log.gz
│   ├── error-2023-01-01-1.log.gz
│   └── ...
└── daily/               # Daily log directories
    ├── 2023-01-01/      # Logs for specific date
    │   ├── application.log
    │   ├── error.log
    │   ├── debug.log
    │   └── test.log
    └── ...
```

Log files are automatically:
- Organized by date in the `daily` directory
- Rotated when they reach 10MB in size
- Compressed and archived in the `archive` directory
- Deleted after 10 days to manage disk space

### Log Levels

The framework uses the following log levels:

| Level | Description |
|-------|-------------|
| FATAL | Critical errors causing the application to abort |
| ERROR | Error events that might still allow the application to continue running |
| WARN  | Potentially harmful situations |
| INFO  | Informational messages highlighting application progress |
| DEBUG | Detailed information for debugging |
| TRACE | Most detailed information |

### Using Logging in Code

The framework provides a `LoggerUtils` class that simplifies logging and adds visual enhancements:

```java
import com.tenpearls.utils.logging.LoggerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyClass {
    private static final Logger logger = LogManager.getLogger(MyClass.class);
    
    public void doSomething() {
        // Start a correlation ID for tracking related log entries
        String correlationId = LoggerUtils.startContext();
        
        // Add custom context data
        LoggerUtils.addToContext("userId", "12345");
        
        // Log with different levels and emojis for better readability
        LoggerUtils.info(logger, "Starting operation");
        LoggerUtils.debug(logger, "Processing data: {}", data);
        
        try {
            // Log method start with timing
            long startTime = System.currentTimeMillis();
            LoggerUtils.methodStart(logger, "processData");
            
            // Process data...
            
            // Log method end with execution time
            LoggerUtils.methodEnd(logger, "processData", startTime);
            
            // Log success
            LoggerUtils.success(logger, "Operation completed successfully");
        } catch (Exception e) {
            // Log error with exception
            LoggerUtils.error(logger, "Operation failed", e);
        } finally {
            // Clear the logging context
            LoggerUtils.clearContext();
        }
    }
    
    public void testMethod() {
        // Section headers for better organization
        LoggerUtils.section(logger, "TESTING IMPORTANT FEATURE");
        
        // Test steps
        LoggerUtils.testStep(logger, 1, "Initialize test data");
        // ... test code ...
        
        // Assertions
        LoggerUtils.assertion(logger, "Value should match expected result");
        
        // Data logging
        LoggerUtils.data(logger, "Response Code", 200);
        LoggerUtils.data(logger, "Response Body", responseBody);
        
        // Separators for visual grouping
        LoggerUtils.separator(logger);
    }
}
```

### Log4j2 Configuration

The logging configuration is defined in `src/main/resources/log4j2.properties`. Key features include:

- Colorized console output
- Daily log files with date-based organization
- Separate log files for different log levels (error.log, debug.log)
- Automatic log rotation and compression
- Log file deletion after 10 days
- Correlation IDs for tracking related log entries

## Enhanced Logging with Direct File Fallback

The ATDAID Framework implements a robust logging mechanism that combines Log4j2 with a direct file logging fallback system. This ensures that logs are captured even if there are issues with the Log4j2 configuration.

### How It Works

1. **Primary Logging**: The framework uses Log4j2 as the primary logging mechanism, with all its features like log levels, appenders, and patterns.

2. **Fallback Mechanism**: In addition to Log4j2, the `LoggerUtils` class implements a direct file logging mechanism that writes log messages directly to files using Java's file I/O operations.

3. **Automatic Directory Creation**: The `LoggerUtils` class automatically creates the necessary log directories at startup, ensuring that logs can be written even if the directories don't exist.

4. **Dual Logging**: When you use the `LoggerUtils` methods, log messages are sent to both Log4j2 and the direct file logging system, ensuring that logs are captured even if one system fails.

### Benefits

- **Reliability**: Logs are captured even if there are issues with the Log4j2 configuration.
- **Simplicity**: The direct file logging system is simple and doesn't depend on external libraries.
- **Consistency**: Log messages have the same format in both systems, making it easier to analyze logs.
- **Automatic Directory Management**: Log directories are automatically created and organized by date.

### Example

```java
import com.tenpearls.utils.logging.LoggerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyClass {
    private static final Logger logger = LogManager.getLogger(MyClass.class);
    
    public void doSomething() {
        // This log message will be sent to both Log4j2 and the direct file logging system
        LoggerUtils.info(logger, "Starting operation");
        
        try {
            // Process data...
            LoggerUtils.success(logger, "Operation completed successfully");
        } catch (Exception e) {
            // The exception will be logged by both systems
            LoggerUtils.error(logger, "Operation failed", e);
        }
    }
}
```

### Direct File Logging Configuration

The direct file logging system is configured in the `LoggerUtils` class:

```java
private static final String LOGS_DIR = "logs";
private static final String DAILY_DIR = LOGS_DIR + File.separator + "daily";
private static final String TODAY_DIR = DAILY_DIR + File.separator + 
        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
private static final String FALLBACK_LOG_FILE = TODAY_DIR + File.separator + "application.log";
```

You can modify these constants to change the log file locations if needed.

## Reporting with ExtentReports

The framework uses ExtentReports for comprehensive test reporting. Reports are automatically generated when tests are run.

### Report Components

The reporting system consists of:

1. **ExtentReportManager**: Manages the creation and configuration of reports
2. **TestListener**: Captures test events and updates the report
3. **TestLoggerExtension**: JUnit extension for capturing test events

### Automatic Report Generation

Reports are automatically generated when tests are run using TestNG or JUnit. The reports include:

- Test status (pass, fail, skip)
- Test duration
- Test logs
- Exception details for failed tests
- Screenshots for UI tests (if available)
- Test metadata (categories, authors)

### Viewing Reports

After test execution, reports are available in the `test-output/reports` directory. The report filename includes a timestamp:

```
test-output/reports/TestReport_yyyy-MM-dd_HH-mm-ss.html
```

To view the latest report:

```bash
# Windows
start test-output\reports\TestReport_<timestamp>.html

# Linux/Mac
open test-output/reports/TestReport_<timestamp>.html
```

### Programmatically Generating Reports

You can also generate reports programmatically:

```java
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
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
test.fail("Test failed with exception: " + e.getMessage());
// or
test.skip("Test skipped");

// Flush the report to disk (important!)
ExtentReportManager.flushReport();
```

### Customizing Reports

You can customize the appearance and content of reports by modifying the `ExtentReportManager` class:

```java
// Change theme
htmlReporter.config().setTheme(Theme.DARK); // or Theme.STANDARD

// Add system information
extent.setSystemInfo("OS", System.getProperty("os.name"));
extent.setSystemInfo("Java Version", System.getProperty("java.version"));
extent.setSystemInfo("Environment", "QA");

// Customize report title and name
htmlReporter.config().setDocumentTitle("ATDAID Test Report");
htmlReporter.config().setReportName("Test Execution Report");
```

## Integration with Test Frameworks

### TestNG Integration

For TestNG tests, the `TestListener` class is registered in the `testng.xml` file:

```xml
<listeners>
    <listener class-name="com.tenpearls.listeners.TestListener"/>
</listeners>
```

### JUnit Integration

For JUnit tests, the `TestLoggerExtension` is applied to test classes:

```java
@ExtendWith(TestLoggerExtension.class)
public class MyJUnitTest {
    // Test methods
}
```

Alternatively, extend the `BaseJUnitTest` class which already includes the extension:

```java
public class MyJUnitTest extends BaseJUnitTest {
    // Test methods
}
```

## Best Practices

### Logging Best Practices

1. **Use appropriate log levels**: Use ERROR for exceptions, WARN for potential issues, INFO for general information, and DEBUG for detailed debugging information.

2. **Include context**: Add relevant context to log messages to make them more useful for debugging.

3. **Use correlation IDs**: Start a logging context with a correlation ID to track related log entries.

4. **Log method entry and exit**: Use `methodStart` and `methodEnd` to log method execution with timing information.

5. **Organize logs with sections**: Use `section` and `separator` to organize logs for better readability.

6. **Log test steps and assertions**: Use `testStep` and `assertion` to document test execution.

7. **Clear the logging context**: Always clear the logging context when done to prevent context data leakage.

### Reporting Best Practices

1. **Use descriptive test names**: Make your test names clear and descriptive.

2. **Add test descriptions**: Include detailed descriptions for tests to make reports more informative.

3. **Categorize tests**: Use categories to organize your tests in the report.

4. **Add screenshots for UI tests**: Include screenshots for UI tests to make it easier to diagnose failures.

5. **Log important test steps**: Add logs for important steps to make reports more informative.

## Troubleshooting

### Common Logging Issues

1. **Missing logs**: Ensure the log level is appropriate for your messages. DEBUG messages won't appear if the logger is set to INFO.

2. **Log directory not created**: The `LogDirectoryInitializer` should create log directories at startup. If not, check for file system permissions.

3. **Correlation ID not appearing**: Ensure you're using `LoggerUtils.startContext()` and that the pattern layout includes `%X{correlationId}`.

4. **Log4j2 configuration issues**: If you're experiencing issues with Log4j2 configuration, check the direct file logging fallback system. Logs should still be captured in the `logs/daily/YYYY-MM-DD/application.log` file even if Log4j2 is not working correctly.

5. **Direct file logging not working**: If the direct file logging fallback system is not working, check for file system permissions and ensure that the `LoggerUtils` class is being used for logging.

6. **No logs in either system**: If no logs are being captured by either Log4j2 or the direct file logging system, check for file system permissions, disk space, and ensure that the application has write access to the logs directory.

### Verifying Logging Configuration

To verify that the logging configuration is working correctly, you can run the `LoggerUtilsTest` class:

```bash
mvn exec:java
```

This will generate log messages using both Log4j2 and the direct file logging system. You should see log messages in the console and in the `logs/daily/YYYY-MM-DD/application.log` file.

You can also run the `DirectFileLogger` class to test the direct file logging system independently:

```bash
java -cp target/classes com.tenpearls.DirectFileLogger
```

This will create a log file in the `logs/daily/YYYY-MM-DD/direct_log.txt` file.

### Common Reporting Issues

1. **Reports not generated**: Ensure `ExtentReportManager.flushReport()` is called at the end of test execution.

2. **Screenshots not appearing**: Check that the screenshot path is correct and that the file exists.

3. **Test steps not logged**: Ensure you're using `ExtentReportManager.getTest()` to get the current test instance.

## Conclusion

The logging and reporting capabilities of the ATDAID Framework provide comprehensive tools for monitoring, debugging, and documenting test execution. By following the best practices outlined in this guide, you can ensure that your logs and reports are informative, organized, and useful for troubleshooting. 