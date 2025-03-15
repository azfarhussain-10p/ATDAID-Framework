# Logging System Documentation

## Overview

The ATDAID Framework includes a comprehensive logging system built on Log4j 2 with enhanced features for reliability, organization, and performance. The system provides structured log directories, fallback mechanisms, correlation tracking, and advanced log management capabilities.

## Key Components

### LoggerUtils

The central utility class for all logging operations, providing enhanced methods for various log levels and specialized logging needs.

```java
// Basic logging
LoggerUtils.info(logger, "Operation started");
LoggerUtils.debug(logger, "Processing data: {}", data);
LoggerUtils.error(logger, "Operation failed", exception);
LoggerUtils.warn(logger, "Resource running low: {}", resourceName);

// Enhanced logging
LoggerUtils.success(logger, "Operation completed successfully");
LoggerUtils.important(logger, "Critical configuration change detected");
LoggerUtils.critical(logger, "System failure detected");

// Test-specific logging
LoggerUtils.testStep(logger, 1, "Preparing test data");
LoggerUtils.assertion(logger, "Response status is 200 OK");
LoggerUtils.section(logger, "User Authentication Tests");

// Process logging
LoggerUtils.startProcess(logger, "Data Import");
LoggerUtils.step(logger, 1, "Validating input file");
LoggerUtils.endProcess(logger, "Data Import");

// Performance logging
LoggerUtils.performance(logger, "Database query", 235); // time in ms

// Context management
LoggerUtils.startContext("transaction-123");
LoggerUtils.addToContext("user", "admin");
LoggerUtils.clearContext();
```

### DirectFileLogger

A fallback logging mechanism that ensures logs are captured even if the primary logging system fails.

```java
// Direct file logging is used automatically by LoggerUtils
// but can also be used directly in critical sections
LoggerUtils.directLog("CRITICAL", "Database connection lost");
```

### LogRotationManager

Manages log file rotation, compression, and archiving to prevent log files from growing too large and consuming excessive disk space.

```java
@Autowired
private LogRotationManager rotationManager;

// Manually trigger log rotation (normally happens automatically)
rotationManager.rotateLogFiles();

// Configure rotation settings
rotationManager.setMaxLogFileSize(10 * 1024 * 1024); // 10 MB
rotationManager.setMaxLogAge(30); // 30 days
```

### LogAnalyzer

Analyzes log files to identify patterns, recurring errors, and potential issues.

```java
@Autowired
private LogAnalyzer logAnalyzer;

// Get error frequency report
Map<String, Integer> errorFrequency = logAnalyzer.analyzeErrorFrequency();

// Find all logs related to a specific correlation ID
List<String> relatedLogs = logAnalyzer.findLogsByCorrelationId("transaction-123");

// Identify performance bottlenecks
List<PerformanceIssue> issues = logAnalyzer.identifyPerformanceBottlenecks();
```

### LogMonitor

Monitors logs for critical issues and can send email alerts to administrators.

```java
@Autowired
private LogMonitor logMonitor;

// Configure monitoring
logMonitor.addCriticalPattern("Database connection failed");
logMonitor.setAlertThreshold(5); // Alert after 5 occurrences

// Manually trigger an alert (normally happens automatically)
logMonitor.sendAlertEmail("Critical error detected", "Details of the error...");
```

### LoggingPerformanceOptimizer

Optimizes logging performance through asynchronous logging, batch processing, and dynamic log level adjustment.

```java
@Autowired
private LoggingPerformanceOptimizer optimizer;

// Enable/disable asynchronous logging
LoggerUtils.setAsyncLoggingEnabled(true);

// Configure batch size for log processing
optimizer.setBatchSize(100);

// Configure memory thresholds for dynamic log level adjustment
optimizer.setHighMemoryThreshold(0.85); // 85% memory usage
optimizer.setMediumMemoryThreshold(0.70); // 70% memory usage
```

## Log Directory Structure

The logging system organizes logs in a structured directory hierarchy:

```
logs/
├── atdaid.log            # Main application log file
├── daily/                # Daily logs directory
│   ├── 2025-03-16/       # Date-specific directory
│   │   ├── application.log  # Application logs for this date
│   │   └── api.log       # API-specific logs
│   └── ...
└── archive/              # Archived log files
    ├── atdaid-2025-03-15.log.gz  # Compressed archived logs
    └── ...
```

## Configuration

### application.properties

```properties
# Logging configuration
logging.level.root=INFO
logging.level.com.tenpearls=DEBUG
logging.file.path=logs
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Log monitoring configuration
logging.monitor.email.enabled=false
logging.monitor.email.recipient=admin@example.com
logging.monitor.email.critical.patterns=Database connection failed,Out of memory error

# Log rotation configuration
logging.rotation.max-file-size=10MB
logging.rotation.max-history=30
logging.rotation.total-size-cap=1GB

# Log performance configuration
logging.performance.async.enabled=true
logging.performance.batch.size=100
logging.performance.flush.interval=5000
```

### log4j2.properties

The framework uses a custom Log4j 2 configuration for advanced logging features:

```properties
status = debug
name = PropertiesConfig

# Console appender configuration
appender.console.type = Console
appender.console.name = STDOUT
appender.console.layout.type = PatternLayout
appender.console.layout.pattern = %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# File appender configuration
appender.file.type = RollingFile
appender.file.name = File
appender.file.fileName = ${sys:logging.file.path}/atdaid.log
appender.file.filePattern = ${sys:logging.file.path}/archive/atdaid-%d{yyyy-MM-dd}-%i.log.gz
appender.file.layout.type = PatternLayout
appender.file.layout.pattern = %d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
appender.file.policies.type = Policies
appender.file.policies.time.type = TimeBasedTriggeringPolicy
appender.file.policies.time.interval = 1
appender.file.policies.time.modulate = true
appender.file.policies.size.type = SizeBasedTriggeringPolicy
appender.file.policies.size.size = 10MB
appender.file.strategy.type = DefaultRolloverStrategy
appender.file.strategy.max = 20

# Root logger configuration
rootLogger.level = info
rootLogger.appenderRef.stdout.ref = STDOUT
rootLogger.appenderRef.file.ref = File

# Application logger configuration
logger.app.name = com.tenpearls
logger.app.level = debug
logger.app.additivity = false
logger.app.appenderRef.stdout.ref = STDOUT
logger.app.appenderRef.file.ref = File
```

## Best Practices

1. **Use LoggerUtils**: Always use the LoggerUtils class for logging to ensure consistent formatting and take advantage of enhanced features.

2. **Correlation IDs**: Use correlation IDs for tracking related log entries across different components.

3. **Context Data**: Add relevant context data to logs for better debugging.

4. **Log Levels**: Use appropriate log levels:
   - ERROR: For errors that affect functionality
   - WARN: For potential issues that don't stop execution
   - INFO: For significant events in normal operation
   - DEBUG: For detailed troubleshooting information
   - TRACE: For very detailed debugging

5. **Performance Considerations**:
   - Enable asynchronous logging for high-volume scenarios
   - Use parameterized logging to avoid string concatenation
   - Avoid excessive logging in tight loops

6. **Sensitive Data**: Never log sensitive information like passwords, tokens, or personal data.

7. **Exception Logging**: Always include the exception object when logging exceptions.

## Troubleshooting

### Common Issues

1. **Missing Log Files**: Ensure the logs directory exists and has appropriate permissions.

2. **Log4j2 Configuration Issues**: If Log4j2 is not working, check the direct log file for errors.

3. **Performance Issues**: If logging is causing performance problems, enable asynchronous logging.

4. **Email Alerts Not Working**: Check the SMTP configuration in application.properties.

### Diagnostic Tools

1. **Log Analysis**: Use the LogAnalyzer to identify patterns and issues in logs.

2. **Performance Monitoring**: Use the LoggingPerformanceOptimizer to monitor and optimize logging performance.

3. **Direct File Logging**: Check the direct log file for issues with the primary logging system.

## Advanced Features

### Custom Log Appenders

The framework supports custom Log4j2 appenders for specialized logging needs:

```java
@Component
public class CustomAppender extends AbstractAppender {
    // Implementation details
}
```

### Log Filtering

Filter logs based on specific criteria:

```java
logAnalyzer.filterLogsByLevel("ERROR");
logAnalyzer.filterLogsByTimeRange(startDate, endDate);
logAnalyzer.filterLogsByPattern("Database.*failed");
```

### Integration with Monitoring Systems

The logging system can be integrated with external monitoring systems:

```java
@Component
public class PrometheusLogMetricsExporter {
    // Implementation details
}
```

## Conclusion

The ATDAID Framework's logging system provides a comprehensive solution for application logging, with features for reliability, organization, analysis, and performance optimization. By following the best practices and utilizing the provided utilities, developers can ensure effective logging throughout their applications.
