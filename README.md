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

### Key Logging Features

- **Structured Directory Organization**: Logs are automatically organized by date in the `daily` directory
- **Direct File Logging Fallback**: Ensures logs are captured even if Log4j2 configuration issues occur
- **Enhanced Logging Utilities**: The `LoggerUtils` class provides user-friendly methods with visual enhancements
- **Correlation IDs**: Track related log entries across the application with unique identifiers
- **Context-Aware Logging**: Add custom context data to log entries for better debugging
- **Automatic Log Management**: Logs are automatically rotated, compressed, and cleaned up

### Using LoggerUtils

```java
import com.tenpearls.utils.logging.LoggerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyClass {
    private static final Logger logger = LogManager.getLogger(MyClass.class);
    
    public void doSomething() {
        // Enhanced logging with visual indicators
        LoggerUtils.info(logger, "Starting operation");
        LoggerUtils.debug(logger, "Processing data");
        
        try {
            // Process data...
            LoggerUtils.success(logger, "Operation completed successfully");
        } catch (Exception e) {
            LoggerUtils.error(logger, "Operation failed", e);
        }
        
        // Organize logs with sections and separators
        LoggerUtils.section(logger, "IMPORTANT SECTION");
        LoggerUtils.data(logger, "Key", "Value");
    }
}
```

Log files are automatically:
- Organized by date in the `daily` directory
- Rotated when they reach 10MB in size
- Compressed and archived in the `archive` directory
- Deleted after 10 days to manage disk space

Log levels and other configurations can be modified in the `src/main/resources/log4j2.properties` or `src/main/resources/log4j2.xml` file.

For more details on logging and reporting, see [Logging and Reporting Guide](docs/Logging.md).

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

### Enhanced Logging and Reporting

- **Structured Log Directory**: Logs are now organized in daily directories for better management
- **Direct File Logging Fallback**: Added a robust fallback mechanism to ensure logs are captured even if Log4j2 configuration issues occur
- **Correlation IDs**: Track related log entries across the application
- **Visual Enhancements**: Improved log readability with emojis and formatting
- **Automatic Log Rotation**: Logs are automatically rotated, compressed, and cleaned up
- **Comprehensive ExtentReports**: Enhanced test reports with detailed test information
- **Dual Logging System**: Logs are now captured by both Log4j2 and a direct file logging system for maximum reliability

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