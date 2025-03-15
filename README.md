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
- Comprehensive logging with Log4j 2
- Advanced test reporting with ExtentReports

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
│   │   │           └── service
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
│       │           └── service
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

To view the latest report, open the most recent HTML file in the reports directory:

```bash
# Windows
start test-output\reports\ExtentReport_<timestamp>.html

# Linux/Mac
open test-output/reports/ExtentReport_<timestamp>.html
```

## Logging

The framework uses Log4j 2 for comprehensive logging. Logs are written to both the console and log files in the `logs` directory.

Log levels can be configured in the `src/main/resources/log4j2.properties` file.

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

# Implement and run in one step
java -jar app.jar implement-and-run src/test/java/com/tenpearls/accpetance/product/ProductManagementTest.java
```

## Recent Updates

### Integration Test Improvements
- Fixed database schema alignment to ensure entity fields map correctly to database columns
- Enhanced authentication in tests by generating JWT tokens directly using JwtService
- Improved test data consistency with proper cleanup between tests
- Corrected status code expectations in test assertions

### Logging and Reporting Enhancements
- Integrated Log4j 2 for comprehensive logging across the application
- Added ExtentReports for detailed HTML test reports
- Created base test classes (BaseTest for TestNG, BaseJUnitTest for JUnit) with logging and reporting capabilities
- Added TestLoggerExtension for JUnit tests to capture test execution events

## Documentation

For more detailed documentation, see the [docs](docs) directory:

- [Product Management](docs/ProductManagement.md)
- [Testing Guide](docs/Testing.md)
- [Logging and Reporting Guide](docs/Logging.md)

## License

This project is licensed under the MIT License - see the LICENSE file for details.