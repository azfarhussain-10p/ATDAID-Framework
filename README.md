# ATDAID Framework

The ATDAID (Automated Test-Driven AI Development) Framework is a comprehensive solution for building robust, testable applications with integrated logging, monitoring, and analysis capabilities.

## Features

- **Product Management**: Complete CRUD operations with role-based access control
- **Security**: JWT-based authentication and authorization with modern JJWT implementation
- **Logging System**: Advanced logging with performance optimization, rotation, and analysis
- **Testing Framework**: Comprehensive test suite with unit, integration, and feature tests
- **Edge Case Coverage**: Extensive test coverage for boundary conditions and edge cases

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/atdaid-framework.git
   cd atdaid-framework
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

## Recent Improvements

The framework has been enhanced with several key improvements:

### 1. Optimized Batch Processing

The `BatchProcessor` class has been optimized for better performance and reliability:
- Added logger caching to reduce overhead of repeated logger lookups
- Implemented thread-safe batch processing with `ReentrantLock`
- Added default values for configuration parameters to prevent initialization issues
- Improved error handling for queue overflow conditions
- Added force processing capability for testing and immediate log flushing

### 2. Modernized Security Implementation

The JWT security implementation has been updated:
- Replaced deprecated `SignatureAlgorithm.HS256` with modern JJWT approach
- Updated method signatures for `signWith` and `verifyWith`
- Enhanced token validation and error handling
- Improved logging for security operations

For detailed information about the security system, see [Security Documentation](docs/Security.md).

### 3. Enhanced Test Coverage

Test coverage has been expanded to include edge cases:
- Zero price and quantity product testing
- Maximum length field validation
- Empty search results handling
- Non-existent entity error handling
- Comprehensive validation of business rules

### 4. Improved Documentation

Documentation has been enhanced throughout the codebase:
- Comprehensive Javadoc for all service classes
- Detailed parameter, return value, and exception descriptions
- Consistent class-level documentation
- Updated README and logging documentation

For a complete list of changes, see the [Changelog](CHANGELOG.md).

## Logging System

The ATDAID Framework includes a comprehensive logging system built on Log4j2 with the following features:

- **Modular Design**: Separate components for core logging, context-aware logging, asynchronous logging, and performance optimization
- **Synchronous and Asynchronous Logging**: Choose between immediate logging or non-blocking asynchronous logging for better performance
- **Context-Aware Logging**: Add context data to log messages for better traceability
- **Batch Processing**: Process log entries in batches for better performance
- **Memory Monitoring**: Automatically adjust log levels based on memory usage
- **Log Cleanup**: Automatically clean up old log files to prevent disk space issues
- **Log Rotation and Archiving**: Automatically rotate and archive logs based on size and age
- **Log Analysis and Monitoring**: Analyze logs for patterns and issues, with optional email alerts

### Using the Logging System

The logging system provides a simple, unified API through the `LoggingFacade` class:

```java
@Autowired
private LoggingFacade logger;

// Basic logging
logger.info("This is an info message");
logger.debug("This is a debug message with parameters: {}, {}", param1, param2);
logger.error("This is an error message with exception", exception);

// Asynchronous logging
logger.asyncInfo("com.example.MyClass", "This is an async info message");
logger.asyncError("com.example.MyClass", "This is an async error message", exception);

// Context-aware logging
String correlationId = logger.startContext();
logger.putContext("userId", "12345");
logger.infoWithContext("User logged in");
logger.endContext();
```

For more advanced usage, you can inject the specific logger components:

```java
@Autowired
private CoreLogger coreLogger;

@Autowired
private ContextLogger contextLogger;

@Autowired
private AsyncLogger asyncLogger;

@Autowired
private LoggerFactory loggerFactory;
```

For detailed information about the logging system, see [Logging Documentation](Logging.md) and [General Logging Guide](docs/Logging.md).

## Testing Framework

The ATDAID Framework includes a comprehensive testing framework with the following features:

- **Common Test Interface**: The `TestBase` interface defines common functionality for all test classes
- **Test Utilities**: The `TestUtils` class provides common utility methods for tests
- **Reporting**: Integration with ExtentReports for detailed test reporting
- **Logging**: Integration with the logging system for test logging
- **Edge Case Testing**: Support for testing boundary conditions and edge cases

### Writing Tests

To write a test class, implement the `TestBase` interface or extend one of the base test classes:

```java
public class MyTest implements TestBase {
    private Logger logger;
    private ExtentTest extentTest;
    
    @BeforeClass
    public void beforeSuite() {
        // Set up test suite
    }
    
    @Test
    public void testSomething() {
        logInfo("Starting test");
        // Test code
        addTestStep("Step 1", "Description", true);
        // More test code
    }
}
```

Run the tests with:

```
mvn test
```

## Configuration

The application can be configured through the `application.properties` file. Key configuration options include:

- Database settings
- JWT security settings
- Logging configuration:
  - `logging.async.queue.size`: Size of the asynchronous logging queue (default: 1000)
  - `logging.async.thread.pool.size`: Size of the thread pool for asynchronous logging (default: 2)
  - `logging.batch.size`: Size of the batch for batch processing (default: 50)
  - `logging.batch.queue.size`: Size of the batch processing queue (default: 1000)
  - `logging.batch.flush.interval`: Interval in milliseconds for flushing the batch (default: 5000)
  - `logging.memory.high.threshold`: High memory threshold for log level adjustment (default: 0.85)
  - `logging.memory.medium.threshold`: Medium memory threshold for log level adjustment (default: 0.70)
  - `logging.cleanup.retention.days`: Number of days to retain log files (default: 30)
  - `logging.cleanup.max.size.mb`: Maximum size of log files in MB (default: 1000)
  - `logging.dir`: Directory for log files (default: logs)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
