# ATDAID Framework Logging System

The ATDAID Framework provides a comprehensive logging system built on Log4j2 with enhanced features for test automation and debugging. This document outlines the logging architecture, configuration options, and best practices.

## Table of Contents

1. [Overview](#overview)
2. [Directory Structure](#directory-structure)
3. [Configuration](#configuration)
4. [LoggerUtils](#loggerutils)
5. [Advanced Features](#advanced-features)
   - [Log Rotation](#log-rotation)
   - [Log Analysis](#log-analysis)
   - [Log Monitoring](#log-monitoring)
   - [Performance Optimization](#performance-optimization)
6. [Best Practices](#best-practices)
7. [Troubleshooting](#troubleshooting)

## Overview

The logging system is designed to provide:

- Comprehensive logging for test automation
- Structured organization of logs by date and type
- Direct file logging fallback for reliability
- Context-aware logging with correlation IDs
- Advanced features like log rotation, analysis, and monitoring
- Performance optimization for high-volume logging

## Directory Structure

Logs are organized in the following directory structure:

```
logs/
├── atdaid.log             # Main log file
├── direct_log.txt         # Fallback log file (used when Log4j fails)
├── daily/                 # Daily logs directory
│   ├── 2025-03-16/        # Date-specific directory
│   │   ├── test.log       # Test-specific logs
│   │   └── api.log        # API-specific logs
│   └── ...
├── archive/               # Archived logs
└── analysis/              # Log analysis reports
```

## Configuration

### Log4j2 Configuration

The primary logging configuration is in `src/main/resources/log4j2.properties`. Key settings include:

- Log levels for different packages
- Appender configurations (Console, File)
- Pattern layouts for log formatting
- Rolling file policies

Example configuration:

```properties
status = debug
name = ATDAIDLoggingConfig

property.logsDir = ${sys:user.dir}/logs
property.patternLayout = %d{yyyy-MM-dd'T'HH:mm:ss.SSS} [%t] [%level] %c{1.} - %msg%n

# Console Appender
appender.console.type = Console
appender.console.name = ConsoleAppender
appender.console.layout.type = PatternLayout
appender.console.layout.pattern = ${patternLayout}

# File Appender
appender.file.type = RollingFile
appender.file.name = FileAppender
appender.file.fileName = ${logsDir}/atdaid.log
appender.file.filePattern = ${logsDir}/archive/atdaid-%d{yyyy-MM-dd}-%i.log.gz
appender.file.layout.type = PatternLayout
appender.file.layout.pattern = ${patternLayout}
appender.file.policies.type = Policies
appender.file.policies.time.type = TimeBasedTriggeringPolicy
appender.file.policies.time.interval = 1
appender.file.policies.time.modulate = true
appender.file.policies.size.type = SizeBasedTriggeringPolicy
appender.file.policies.size.size = 10MB
appender.file.strategy.type = DefaultRolloverStrategy
appender.file.strategy.max = 30

# Root Logger
rootLogger.level = info
rootLogger.appenderRef.console.ref = ConsoleAppender
rootLogger.appenderRef.file.ref = FileAppender
```

### Application Properties

Additional logging configuration is available in `application.properties`:

```properties
# Logging Configuration
logging.level.root=INFO
logging.level.com.tenpearls=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate=ERROR

# Log Monitoring Configuration
logging.monitor.enabled=true
logging.monitor.email.enabled=false
logging.monitor.email.to=admin@example.com
logging.monitor.email.from=system@example.com
logging.monitor.email.subject=[ALERT] ATDAID Framework Log Alert
logging.monitor.check.interval=3600000

# Log Rotation Configuration
logging.rotation.enabled=true
logging.rotation.max-size=10MB
logging.rotation.max-history=30

# Log Performance Configuration
logging.performance.async-enabled=true
```

## LoggerUtils

The `LoggerUtils` class provides a simplified interface for logging with additional features:

```java
// Basic logging
LoggerUtils.info(logger, "User logged in successfully");
LoggerUtils.debug(logger, "Processing request with parameters: {}", params);
LoggerUtils.error(logger, "Failed to connect to database", exception);

// Context-aware logging
LoggerUtils.startContext("test-123");
LoggerUtils.addToContext("user", "admin");
LoggerUtils.info(logger, "Operation performed in context");
LoggerUtils.clearContext();

// Process step logging
LoggerUtils.step(logger, 1, "Logging in to application");
LoggerUtils.step(logger, 2, "Navigating to dashboard");

// Test step logging
LoggerUtils.testStep(logger, 1, "Verifying login functionality");
LoggerUtils.assertion(logger, "Dashboard title is correct");

// Performance logging
LoggerUtils.performance(logger, "Database query", 150);

// Success and important messages
LoggerUtils.success(logger, "Operation completed successfully");
LoggerUtils.important(logger, "Critical configuration change detected");

// Critical errors
LoggerUtils.critical(logger, "System failure detected");
```

### Key Features of LoggerUtils

- **Visual Indicators**: Each log level has distinct visual indicators for better readability
- **Context Management**: Easily manage and track correlation IDs and context data
- **Direct File Logging**: Fallback mechanism for reliability when Log4j2 fails
- **Performance Metrics**: Built-in support for logging performance data
- **Test Organization**: Special methods for test steps and assertions
- **Asynchronous Logging**: Optional async logging for performance-critical sections

## Advanced Features

### Log Rotation

The `LogRotationManager` handles automatic rotation of log files based on size and age:

- **Daily Rotation**: Logs are automatically rotated at midnight
- **Size-Based Rotation**: Large log files are rotated when they exceed a configurable size
- **Compression**: Rotated logs are compressed to save disk space
- **Retention Management**: Old logs are automatically deleted after a configurable retention period
- **Archiving**: Logs from previous days are moved to the archive directory

Configuration in `application.properties`:

```properties
logging.rotation.enabled=true
logging.rotation.max-size=10MB
logging.rotation.max-history=30
```

#### Key Methods

```java
// Scheduled methods (run automatically)
public void performDailyRotation() // Runs at midnight
public void checkLogSizes() // Runs hourly

// Helper methods
private void rotateLargeLogFile(File logFile)
private void archiveOldLogs()
private void deleteExpiredLogs()
```

### Log Analysis

The `LogAnalyzer` provides insights into log patterns and issues:

- **Error Analysis**: Identifies common error patterns and frequencies
- **Warning Analysis**: Tracks warning messages and their frequencies
- **Performance Analysis**: Identifies slow methods and operations
- **Exception Analysis**: Catalogs unique exceptions encountered
- **Recommendations**: Generates actionable recommendations based on analysis
- **Summary Reports**: Creates daily analysis reports in the analysis directory

#### Key Methods

```java
// Scheduled methods (run automatically)
public void performDailyAnalysis() // Runs at 1:00 AM

// Analysis methods
private void analyzeLogFile(File logFile)
private void generateAnalysisReport(String dateStr)
private void generateRecommendations(PrintWriter writer)

// Utility methods
public Map<String, Object> getAnalysisSummary()
```

#### Sample Analysis Report

```
=== Log Analysis Report for 2025-03-16 ===
Generated at: 2025-03-17T01:00:00

=== ERROR ANALYSIS ===
Total unique error types: 5
Total error occurrences: 27

Top 10 most frequent errors:
  - ConnectionException: 12 occurrences
  - ValidationError: 8 occurrences
  - AuthenticationFailure: 4 occurrences
  - ResourceNotFoundException: 2 occurrences
  - ConfigurationError: 1 occurrences

=== PERFORMANCE ANALYSIS ===
Total methods with performance metrics: 15

Top 10 slowest methods (average execution time):
  - DatabaseService.executeQuery:
      Avg: 1250.45 ms
      Min: 850 ms
      Max: 2500 ms
      Count: 35
  - FileProcessor.processLargeFile:
      Avg: 875.32 ms
      Min: 450 ms
      Max: 1200 ms
      Count: 12

=== RECOMMENDATIONS ===
1. High-frequency errors that should be investigated:
   - ConnectionException (12 occurrences)
   - ValidationError (8 occurrences)

2. Slow methods that should be optimized:
   - DatabaseService.executeQuery (avg: 1250.45 ms)
   - FileProcessor.processLargeFile (avg: 875.32 ms)
```

### Log Monitoring

The `LogMonitor` actively monitors logs for critical issues:

- **Real-time Monitoring**: Scans log files for errors and exceptions
- **Email Alerts**: Sends email notifications when critical issues are detected
- **Configurable Checks**: Adjustable check intervals and alert thresholds
- **Manual Triggering**: Can be triggered programmatically for immediate checking
- **Correlation**: Groups related errors for better context

Configuration in `application.properties`:

```properties
logging.monitor.enabled=true
logging.monitor.email.enabled=true
logging.monitor.email.to=admin@example.com
logging.monitor.email.from=system@example.com
logging.monitor.email.subject=[ALERT] ATDAID Framework Log Alert
logging.monitor.check.interval=3600000
```

#### Key Methods

```java
// Scheduled methods (run automatically)
public void checkLogsForErrors() // Runs at configurable intervals

// Monitoring methods
private List<String> findCriticalErrors()
private List<String> findErrorsInFile(File logFile)
private void sendAlertEmail(List<String> errors)

// Manual triggering
public int manualCheckAndAlert()
```

#### Sample Email Alert

```
Subject: [ALERT] ATDAID Framework Log Alert - 2025-03-16T14:30:00

Critical errors were detected in the ATDAID Framework logs:

---ERROR 1---
2025-03-16T14:25:12.345 [main] ERROR [DatabaseService] - Failed to connect to database: Connection refused
java.sql.SQLException: Connection refused
    at com.tenpearls.service.DatabaseService.connect(DatabaseService.java:45)
    at com.tenpearls.service.UserService.authenticate(UserService.java:28)

---ERROR 2---
2025-03-16T14:26:05.678 [http-nio-8080-exec-3] ERROR [SecurityFilter] - Authentication failed for user: admin
com.tenpearls.exception.AuthenticationException: Invalid credentials
    at com.tenpearls.security.SecurityFilter.doFilter(SecurityFilter.java:87)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)

... and 3 more errors (see logs for details)
```

### Performance Optimization

The `LoggingPerformanceOptimizer` improves logging performance:

- **Asynchronous Logging**: Non-blocking logging for non-critical messages
- **Batch Processing**: Processes log entries in batches for better throughput
- **Dynamic Log Level Adjustment**: Automatically adjusts log levels based on system load
- **Memory Usage Monitoring**: Prevents excessive memory consumption by logging
- **Queue Management**: Handles overflow conditions gracefully
- **Scheduled Cleanup**: Periodically cleans up old log buffers

Configuration in `application.properties`:

```properties
logging.performance.async-enabled=true
```

#### Key Methods

```java
// Public methods
public void asyncLog(String level, String loggerName, String message)
public void shutdown()

// Internal methods
private void processBatch()
private void monitorMemoryUsage()
private void adjustLogLevels(String newLevel)
private void restoreLogLevels()
private void cleanupOldLogBuffers()
```

## Best Practices

1. **Use appropriate log levels**:
   - ERROR: For errors that affect functionality
   - WARN: For potential issues that don't stop execution
   - INFO: For significant events in normal operation
   - DEBUG: For detailed troubleshooting information
   - TRACE: For very detailed diagnostic information

2. **Include context information**:
   - Use correlation IDs to track requests across components
   - Include relevant business identifiers (user ID, order ID, etc.)
   - Add timing information for performance-sensitive operations

3. **Structure log messages consistently**:
   - Start with an action verb (e.g., "Processing", "Completed", "Failed")
   - Include relevant identifiers
   - For errors, include both the error message and root cause

4. **Optimize logging performance**:
   - Use asynchronous logging for non-critical messages
   - Avoid excessive logging in tight loops
   - Consider using DEBUG level for detailed information
   - Use batch processing for high-volume logging

5. **Ensure reliability**:
   - Use the direct file logging fallback for critical errors
   - Implement proper exception handling around logging code
   - Configure appropriate log rotation to prevent disk space issues

6. **Organize logs effectively**:
   - Use the daily directory structure for date-based organization
   - Use appropriate log file names for different components
   - Leverage correlation IDs for tracking related log entries

7. **Security considerations**:
   - Don't log sensitive information (passwords, tokens, PII)
   - Be cautious with exception stack traces in production
   - Consider log file permissions and access controls

## Troubleshooting

### Common Issues

1. **Logs not appearing**:
   - Check log level configuration
   - Verify log directory permissions
   - Check disk space availability

2. **Performance issues with logging**:
   - Enable asynchronous logging
   - Adjust log levels in high-volume areas
   - Consider using sampling for very high-volume logs

3. **Email alerts not working**:
   - Verify SMTP server configuration
   - Check network connectivity to mail server
   - Ensure recipient email addresses are correct
   - Check if email alerts are enabled in configuration

4. **Log rotation not working**:
   - Verify rotation configuration
   - Check file permissions
   - Ensure scheduled tasks are running

### Direct File Logging

If Log4j2 fails to initialize or encounters errors, the framework falls back to direct file logging:

```java
// This will write to direct_log.txt even if Log4j2 fails
LoggerUtils.directLog("Critical error occurred during startup");
```

The direct log file is located at `logs/direct_log.txt`. 

### Checking Log Analysis

To view the latest log analysis report:

1. Navigate to the `logs/analysis` directory
2. Open the most recent `analysis_YYYY-MM-DD.txt` file

### Manually Triggering Log Monitoring

You can manually trigger log monitoring to check for critical errors:

```java
@Autowired
private LogMonitor logMonitor;

// Later in your code
int errorCount = logMonitor.manualCheckAndAlert();
if (errorCount > 0) {
    System.out.println("Found " + errorCount + " critical errors!");
}
```

### Adjusting Performance Settings

If you're experiencing performance issues with logging, you can adjust these settings:

1. Enable/disable asynchronous logging:
   ```java
   LoggerUtils.setAsyncLoggingEnabled(true); // or false
   ```

2. Modify the `application.properties` file:
   ```properties
   logging.performance.async-enabled=true
   ```

3. For extreme cases, dynamically adjust log levels:
   ```java
   // Reduce logging during high load
   LoggingPerformanceOptimizer.adjustLogLevels("WARN");
   
   // Later, restore normal logging
   LoggingPerformanceOptimizer.restoreLogLevels();
   ``` 