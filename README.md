# ATDAID Framework

## Acceptance Test-Driven AI Development

ATDAID (Acceptance Test-Driven AI Development) is a framework for developing software using acceptance tests as the driver for AI-powered implementation. This approach combines the benefits of Test-Driven Development (TDD) with the power of AI to accelerate software development.

## Features

- Integration with TestNG and JUnit for writing expressive tests
- AI-driven implementation based on test specifications
- Automatic refinement of implementations when tests fail
- Support for different types of tests (acceptance, integration, unit)
- RESTful API for Product Management
- JWT Authentication
- Comprehensive logging with Log4j 2 and organized daily log directories
- Advanced test reporting with ExtentReports
- Domain-organized utility classes for better maintainability

## Project Structure

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── tenpearls
│   │   │           ├── config
│   │   │           ├── controller
│   │   │           ├── dto
│   │   │           ├── exception
│   │   │           ├── model
│   │   │           ├── repository
│   │   │           ├── security
│   │   │           ├── service
│   │   │           └── utils
│   │   │               ├── file
│   │   │               ├── logging
│   │   │               └── string
│   │   └── resources
│   │       ├── application.properties
│   │       ├── application-test.properties
│   │       ├── log4j2.properties
│   │       └── schema.sql
│   └── test
│       ├── java
│       │   └── com
│       │       └── tenpearls
│       │           ├── accpetance
│       │           ├── base
│       │           ├── config
│       │           ├── integration
│       │           ├── listeners
│       │           ├── reports
│       │           ├── service
│       │           └── utils
│       │               ├── security
│       │               └── time
│       └── resources
│           └── testng.xml
├── docs
│   ├── Logging.md
│   ├── ProductManagement.md
│   ├── README.md
│   └── Testing.md
└── pom.xml
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- H2 Database (embedded)

### Installation

1. Clone the repository:

```bash
git clone https://github.com/yourusername/atdaid-framework.git
cd atdaid-framework
```

2. Build the project:

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run tests with specific groups (TestNG)
mvn test -Dgroups=smoke,auth
```

### Test Reports

After running tests, ExtentReports generates detailed HTML reports in the `test-output/reports` directory. These reports include:

- Test status (pass, fail, skip)
- Test duration
- Test logs
- Exception details for failed tests
- Test metadata (categories, authors)
- Screenshots for UI tests (if available)

To view the latest report, open the most recent HTML file in the reports directory:

```bash
# Windows
start test-output\reports\TestReport_<timestamp>.html

# Linux/Mac
open test-output/reports/TestReport_<timestamp>.html
```

## Logging

The framework uses Log4j 2 for comprehensive logging with an enhanced fallback mechanism. Logs are organized in a structured directory hierarchy:

```
logs/
├── atdaid.log            # Main application log file
├── direct_log.txt        # Fallback log file for reliability
├── daily/                # Daily logs directory
│   ├── 2025-03-16/       # Date-specific directory
│   │   ├── test.log      # Test-specific logs
│   │   └── api.log       # API-specific logs
│   └── ...
├── archive/              # Archived log files
└── analysis/             # Log analysis reports
```

### Key Logging Features

- **Structured Directory Organization**: Logs are automatically organized by date in the `daily` directory
- **Direct File Logging Fallback**: Ensures logs are captured even if Log4j2 configuration issues occur
- **Enhanced Logging Utilities**: The `LoggerUtils` class provides user-friendly methods with visual enhancements
- **Correlation IDs**: Track related log entries across the application with unique identifiers
- **Context-Aware Logging**: Add custom context data to log entries for better debugging
- **Automatic Log Management**: Logs are automatically rotated, compressed, and cleaned up
- **Email Alerts**: Critical errors can trigger email notifications to administrators
- **Log Analysis**: Built-in tools to analyze log patterns and identify recurring issues
- **Performance Optimization**: Asynchronous logging for high-volume scenarios
- **Memory Management**: Dynamic log level adjustment based on system load

### Using LoggerUtils

```java
import com.tenpearls.utils.logging.LoggerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyClass {
    private static final Logger logger = LogManager.getLogger(MyClass.class);

    public void doSomething() {
        // Start a logging context with correlation ID
        LoggerUtils.startContext("operation-123");
        LoggerUtils.addToContext("user", "admin");
        
        // Enhanced logging with visual indicators
        LoggerUtils.info(logger, "Starting operation");
        LoggerUtils.debug(logger, "Processing data");
        
        // Log test steps and assertions
        LoggerUtils.testStep(logger, 1, "Preparing data");
        LoggerUtils.assertion(logger, "Data is valid");
        
        try {
            // Process data...
            long startTime = System.currentTimeMillis();
            // ... operation code ...
            long duration = System.currentTimeMillis() - startTime;
            
            // Log performance metrics
            LoggerUtils.performance(logger, "Data processing", duration);
            LoggerUtils.success(logger, "Operation completed successfully");
        } catch (Exception e) {
            LoggerUtils.error(logger, "Operation failed", e);
            LoggerUtils.critical(logger, "Critical system failure", e);
        } finally {
            // Clear the logging context
            LoggerUtils.clearContext();
        }
    }
}
```

For more details on logging and reporting, see [Logging and Reporting Guide](Logging.md).

## Writing Tests

### Acceptance Tests

Create a new test class in the `src/test/java/com/tenpearls/accpetance` package. These tests use RestAssured to test the API endpoints.

Example:
```java
@Test(groups = {"smoke", "product"})
public void testCreateProduct_Success() {
    logger.info("Testing createProduct_Success");
    
    // Given
    ProductRequest request = ProductRequest.builder()
            .name("Test Product")
            .description("Test Description")
            .price(new BigDecimal("99.99"))
            .stockQuantity(100)
            .sku("TEST-SKU-001")
            .build();
    
    // When
    Response response = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/products");
    
    // Then
    assertThat(response.getStatusCode()).isEqualTo(201);
    assertThat(response.jsonPath().getString("name")).isEqualTo("Test Product");
    
    logger.debug("createProduct_Success test completed");
}
```

### Integration Tests

Create a new test class in the `src/test/java/com/tenpearls/integration` package. These tests use Spring's MockMvc to test the API endpoints.

Example:
```java
@Test
void testCreateProduct_AsAdmin_Success() throws Exception {
    logger.info("Testing testCreateProduct_AsAdmin_Success");
    
    // Given
    ProductRequest request = ProductRequest.builder()
            .name("Test Product")
            .sku("SKU123")
            .price(new BigDecimal("99.99"))
            .stockQuantity(100)
            .active(true)
            .build();

    // When/Then
    mockMvc.perform(post("/api/products")
            .header("Authorization", "Bearer " + adminToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(request.getName()));
    
    logger.debug("testCreateProduct_AsAdmin_Success test completed");
}
```

### Service Tests

Create a new test class in the `src/test/java/com/tenpearls/service` package. These tests focus on the business logic layer.

Example:
```java
@Test
public void testCreateProduct_Success() {
    logger.info("Testing createProduct_Success");
    
    // Given
    ProductRequest request = ProductRequest.builder()
            .name("Test Product")
            .description("Test Description")
            .price(new BigDecimal("99.99"))
            .stockQuantity(100)
            .sku("TEST-SKU-001")
            .build();

    when(productRepository.existsBySku("TEST-SKU-001")).thenReturn(false);
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    // When
    ProductResponse response = productService.createProduct(request);

    // Then
    assertThat(response.getName()).isEqualTo("Test Product");
    verify(productRepository).save(any(Product.class));
    
    logger.debug("createProduct_Success test completed");
}
```

## Using AI-Driven Implementation

```bash
# Implement from a single test file
java -jar app.jar implement src/test/java/com/tenpearls/accpetance/product/ProductManagementTest.java

# Run tests to verify implementation
java -jar app.jar run src/test/java/com/tenpearls/accpetance/product/ProductManagementTest.java
```

## Recent Updates

### Enhanced Logging and Reporting (March 2025)

- **Advanced Log Management**: New classes for log rotation, analysis, and monitoring
  - `LogRotationManager`: Automatically rotates, compresses, and cleans up log files
  - `LogAnalyzer`: Analyzes logs for patterns, errors, and performance bottlenecks
  - `LogMonitor`: Monitors logs for critical errors and sends email alerts
  - `LoggingPerformanceOptimizer`: Optimizes logging performance with async processing

- **Performance Optimizations**:
  - Asynchronous logging for non-critical messages
  - Batch processing of log entries
  - Dynamic log level adjustment based on system load
  - Memory usage monitoring to prevent excessive resource consumption

- **Reliability Improvements**:
  - Enhanced direct file logging fallback mechanism
  - Automatic creation of log directories
  - Graceful handling of logging failures

- **Analysis and Monitoring**:
  - Error frequency analysis
  - Performance bottleneck detection
  - Correlation of related log entries
  - Email alerts for critical errors
  - Daily log analysis reports

### Utility Organization

- **Domain-Specific Utilities**: Utilities are now organized by domain for better maintainability
- **LoggerUtils**: Enhanced logging utilities for consistent logging across the application
- **DateTimeUtils**: Comprehensive date and time utilities for various formatting needs

### Documentation

- **Updated Guides**: Comprehensive documentation for logging, testing, and product management
- **Code Examples**: Practical examples for common use cases
- **Best Practices**: Guidelines for effective use of the framework

## Documentation

For more detailed documentation, see the [docs](docs) directory:

- [Product Management](docs/ProductManagement.md)
- [Testing Guide](docs/Testing.md)
- [Logging and Reporting Guide](docs/Logging.md)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Utility Organization

The framework follows a domain-driven approach to organizing utility classes. Instead of placing all utilities in a single directory, they are organized by domain to improve maintainability and discoverability.

### Main Utilities

Utilities in the main source code are organized in the following structure:

- `utils/file/` - File-related utilities (e.g., `FileUtils`)
- `utils/logging/` - Logging-related utilities (e.g., `LoggerUtils`)
- `utils/string/` - String manipulation utilities (e.g., `StringUtils`)

### Test Utilities

Utilities in the test source code are organized in the following structure:

- `utils/security/` - Security-related test utilities (e.g., `PasswordHashGenerator`)
- `utils/time/` - Date and time test utilities (e.g., `DateTimeUtils`)

### Best Practices

1. **Domain-Specific Organization**: Place utility classes in domain-specific subdirectories rather than in the root utils directory.
2. **Naming Conventions**: Use clear, descriptive names for utility classes and methods.
3. **Documentation**: Include comprehensive JavaDoc comments for all utility classes and methods.
4. **Immutability**: Utility classes should be immutable and stateless.
5. **Static Methods**: Utility classes should contain only static methods and should not be instantiated.

For more details, see the README.md files in the respective utils directories.