# ATDAID Framework Logging System

The ATDAID Framework includes a comprehensive logging system built on Log4j2, designed to provide robust logging capabilities for applications. This document provides detailed information about the logging system architecture, configuration, and usage.

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Key Components](#key-components)
4. [Configuration](#configuration)
5. [Best Practices](#best-practices)
6. [Troubleshooting](#troubleshooting)
7. [Advanced Topics](#advanced-topics)
8. [Testing](#testing)

## Overview

The logging system is built on Log4j2 and emphasizes:

- **Reliability**: Ensuring logs are consistently captured and stored
- **Performance**: Minimizing the impact of logging on application performance
- **Organization**: Structured logs with context information for better analysis
- **Analysis**: Tools for analyzing logs to identify patterns and issues
- **Monitoring**: Real-time monitoring of logs for critical issues

## Architecture

The logging system uses a layered architecture:

1. **Application Layer**: Where log messages are generated
2. **Logging Facade**: Provides a consistent API for logging
3. **Logging Implementation**: Log4j2 implementation
4. **Output Destinations**: Console, files, databases, etc.

### Directory Structure

```
logs/
├── daily/                 # Daily log files
│   └── YYYY-MM-DD/        # Logs organized by date
├── archive/               # Archived log files
├── analysis/              # Log analysis reports
└── monitoring/            # Monitoring configuration and results
```

## Key Components

### LoggerUtils

The `LoggerUtils` class provides a singleton instance for logging operations:

```java
// Get the singleton instance
LoggerUtils logger = LoggerUtils.getInstance();

// Log messages at different levels
logger.debug("Debug message");
logger.info("Info message");
logger.warning("Warning message");

// Log with context data
Map<String, Object> context = new HashMap<>();
context.put("userId", "12345");
context.put("action", "login");
logger.debug("User login attempt", context);
```

### DirectFileLogger

For direct file logging without going through the Log4j2 configuration:

```java
// Log directly to a file
logger.directLog("Critical error occurred", "errors.log");
```

### LogDirectoryInitializer

Initializes the log directory structure:

```java
// Initialize log directories
LogDirectoryInitializer initializer = new LogDirectoryInitializer();
boolean success = initializer.initializeDirectories();
```

### LogRotationManager

Manages log rotation based on size and time:

```java
// Rotate logs based on configuration
LogRotationManager rotationManager = new LogRotationManager();
rotationManager.rotateLogsIfNeeded();
```

### LogAnalyzer

Analyzes logs for patterns and issues:

```java
// Analyze logs for errors
LogAnalyzer analyzer = new LogAnalyzer();
List<LogAnalysisResult> results = analyzer.analyzeErrorPatterns();
```

### LogMonitor

Monitors logs for critical issues:

```java
// Start log monitoring
LogMonitor monitor = new LogMonitor();
monitor.startMonitoring();
```

### LoggingPerformanceOptimizer

Optimizes logging performance:

```java
// Check if logging is initialized
if (LoggingPerformanceOptimizer.isInitialized()) {
    // Perform logging operations
}

// Enable asynchronous logging
LoggingPerformanceOptimizer.setAsyncLoggingEnabled(true);
```

## Configuration

### Application Properties

The logging system can be configured through the `application.properties` file:

```properties
# Logging Configuration
logging.level.root=INFO
logging.level.com.tenpearls=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate=ERROR

# Log4j2 Configuration
logging.log4j2.level.com.tenpearls=DEBUG

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
logging.rotation.delete-on-start=false

# Log Performance Configuration
logging.performance.async-enabled=true
logging.performance.buffer-size=1000
logging.performance.flush-interval=100

# Log Directory Configuration
logging.directory.base=logs
logging.directory.daily=logs/daily
logging.directory.analysis=logs/analysis
logging.directory.create-on-start=true
```

### Spring Configuration

For Spring applications, you can configure the logging system through Java configuration:

```java
@Configuration
public class LoggingConfig {
    
    @Bean
    public LogDirectoryInitializer logDirectoryInitializer() {
        return new LogDirectoryInitializer();
    }
    
    @Bean
    public LoggingPerformanceOptimizer loggingPerformanceOptimizer() {
        return new LoggingPerformanceOptimizer();
    }
    
    // Other logging beans
}
```

## Best Practices

### Performance

- Use asynchronous logging for high-throughput applications
- Log at the appropriate level (DEBUG for development, INFO for production)
- Use context data sparingly to avoid excessive memory usage

### Reliability

- Initialize log directories at application startup
- Configure log rotation to prevent disk space issues
- Use try-catch blocks around logging operations in critical code paths

### Organization

- Use consistent log message formats
- Include relevant context data in log messages
- Organize logs by date and component

## Troubleshooting

### Common Issues

1. **Log files not created**: Ensure the log directories exist and have appropriate permissions
2. **Missing log messages**: Check the log level configuration
3. **Performance issues**: Consider enabling asynchronous logging

### Debugging

To debug logging issues:

1. Enable DEBUG level logging for the logging system:
   ```properties
   logging.level.com.tenpearls.utils.logging=DEBUG
   ```

2. Check the console output for logging initialization messages
3. Verify the log directory structure

## Advanced Topics

### Custom Log Appenders

You can create custom log appenders for specific needs:

```java
public class CustomAppender extends AbstractAppender {
    // Implementation details
}
```

### Log Analysis

The `LogAnalyzer` class provides methods for analyzing logs:

- Pattern matching
- Error frequency analysis
- Performance bottleneck detection

## Testing

The logging system includes comprehensive tests in the `LoggingFeaturesTest` class:

```java
@Test
public void testBasicLogging() {
    // Test basic logging functionality
}

@Test
public void testAsyncLogging() {
    // Test asynchronous logging
}

@Test
public void testDirectFileLogging() {
    // Test direct file logging
}
```

To run the logging tests:

```bash
mvn test -Dtest=LoggingFeaturesTest
```

Note: Some integration tests may fail if run together with the logging tests due to resource conflicts. It's recommended to run the logging tests separately.
