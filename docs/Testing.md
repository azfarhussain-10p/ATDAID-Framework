# Testing Guide for ATDAID Framework

## Types of Tests

ATDAID Framework supports three main types of tests: acceptance tests, integration tests, and service layer tests.

### Acceptance Tests (TestNG)

- **Purpose**: Verify that the application satisfies business requirements
- **Focus**: End-to-end functionality from a user's perspective
- **Approach**: Usually black-box testing
- **Use when**:
  - Writing tests that describe business features
  - Following a behavior-driven approach
  - Testing user workflows across the application
  - You want AI to generate implementations directly from your tests

Example structure:

```java
package com.tenpearls.accpetance.yourfeature;

import com.tenpearls.accpetance.BaseAcceptanceTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class YourFeatureTest extends BaseAcceptanceTest {

    @Test
    public void testSomeBusinessRequirement() {
        // Test code here
        assertTrue(true);
    }
}
```

### Integration Tests (JUnit)

- **Purpose**: Verify interactions between components
- **Focus**: Component interfaces and communication
- **Approach**: Usually involves a running application environment
- **Use when**:
  - Testing how multiple units work together
  - Testing with real databases, file systems, or network services
  - You need Spring Boot's testing features
  - Testing with mocks isn't sufficient

Example structure:

```java
package com.tenpearls.integration.yourfeature;

import com.tenpearls.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YourFeatureIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testComponentsWorkTogether() {
        // Integration test code here
        assertTrue(true);
    }
}
```

### Service Layer Tests (TestNG)

- **Purpose**: Verify business logic in isolation
- **Focus**: Service methods and their behavior
- **Approach**: Unit testing with mocked dependencies
- **Use when**:
  - Testing business logic independently from other components
  - Verifying service method behavior with different inputs
  - Testing edge cases and exception handling
  - You want to ensure the service layer works correctly before integration

Example structure:

```java
package com.tenpearls.service;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class YourServiceTest {

    @Mock
    private SomeDependency dependency;

    private YourService service;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new YourService(dependency);
    }

    @Test
    public void testSomeServiceMethod() {
        // Given
        when(dependency.someMethod()).thenReturn(expectedValue);

        // When
        Result result = service.methodUnderTest();

        // Then
        assertThat(result).isEqualTo(expectedValue);
        verify(dependency).someMethod();
    }
}
```

## Running Tests

### Running Acceptance Tests

```bash
# Run all acceptance tests
mvn test -Dtest=*Test

# Run specific acceptance test
mvn test -Dtest=ProductManagementTest
```

### Running Integration Tests

```bash
# Run all integration tests
mvn test -P junit -Dtest=*IntegrationTest

# Run specific integration test
mvn test -P junit -Dtest=ProductManagementIntegrationTest
```

### Running Service Layer Tests

```bash
# Run all service tests
mvn test -Dtest=*ServiceTest

# Run specific service test
mvn test -Dtest=ProductServiceTest
```

## Example: ProductServiceTest

The `ProductServiceTest` class demonstrates comprehensive testing of the `ProductService` class:

```java
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    private AutoCloseable closeable;

    @BeforeMethod
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testCreateProduct_Success() {
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
    }
    
    // Additional test methods...
}
```

Key aspects of the `ProductServiceTest`:

1. **Mocking Dependencies**: Uses Mockito to mock the `ProductRepository`
2. **Test Structure**: Follows the Given-When-Then pattern for clear test organization
3. **Comprehensive Coverage**: Tests all service methods including success and failure scenarios
4. **Verification**: Verifies both return values and interactions with dependencies
5. **Exception Testing**: Tests exception handling for error cases

## Leveraging AI Features

The real power of ATDAID Framework is using tests to drive AI implementation:

```bash
# Implement from a single test file
java -jar app.jar implement src/test/java/com/tenpearls/accpetance/yourfeature/YourFeatureTest.java

# Run tests to verify implementation
java -jar app.jar run src/test/java/com/tenpearls/accpetance/yourfeature/YourFeatureTest.java

# Implement and run in one step
java -jar app.jar implement-and-run src/test/java/com/tenpearls/accpetance/yourfeature/YourFeatureTest.java
```

## Best Practices

1. **Follow naming conventions**:
   - For acceptance tests: `*Test.java`
   - For integration tests: `*IntegrationTest.java`
   - For service tests: `*ServiceTest.java`

2. **Make test intentions clear**:
   - Include detailed assertions that specify expected behavior
   - Add comments explaining business requirements
   - Write descriptive test method names

3. **Separate test files by feature**: 
   - Each test class should focus on a single feature or component
   - Organize in appropriate packages

4. **Include essential details**:
   - For API tests, be explicit about endpoints, methods, and expected responses
   - For domain logic tests, clearly specify input data and expected outputs
   - For service tests, clearly define mock behavior and verify interactions

5. **Test Structure**:
   - Use the Given-When-Then pattern for clear test organization
   - Test both success and failure scenarios
   - Test edge cases and exception handling

## Troubleshooting Integration Tests

When working with integration tests, you might encounter various issues. Here are some common problems and their solutions based on our recent experience:

### 1. Authentication Issues

**Problem**: Tests fail with 401 Unauthorized or 403 Forbidden status codes.

**Solution**:
- Instead of relying on login requests to obtain authentication tokens, generate JWT tokens directly using the `JwtService`:

```java
@Autowired
private JwtService jwtService;
@Autowired
private UserRepository userRepository;

@BeforeEach
void setUp() {
    // Create or retrieve test users
    User adminUser = userRepository.findByEmail("admin@example.com").orElse(/* create user */);
    User regularUser = userRepository.findByEmail("user@example.com").orElse(/* create user */);
    
    // Generate tokens directly
    adminToken = jwtService.generateToken(adminUser);
    userToken = jwtService.generateToken(regularUser);
}
```

This approach bypasses potential issues with the login endpoint and ensures valid tokens for testing.

### 2. Database Schema Mismatches

**Problem**: Tests fail with SQL exceptions related to missing tables or columns.

**Solution**:
- Ensure your test schema (usually in `src/test/resources/schema.sql`) includes all necessary tables and columns.
- Verify that column names in the database match the entity field mappings:

```java
@Entity
@Table(name = "products")
public class Product {
    // ...
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    // ...
}
```

### 3. NULL Value Constraints

**Problem**: Tests fail with `DataIntegrityViolationException` due to NULL values for non-nullable columns.

**Solution**:
- Ensure all required fields are set in your test data:

```java
Product product = new Product();
product.setName("Test Product");
product.setSku("SKU123");
product.setPrice(new BigDecimal("99.99"));
product.setStockQuantity(100); // Don't forget required fields
product.setActive(true);
```

### 4. Status Code Expectations

**Problem**: Tests fail with assertions about expected HTTP status codes.

**Solution**:
- Verify that your test expectations match the actual API behavior:

```java
// If the API returns 204 No Content instead of 200 OK
mockMvc.perform(delete("/api/products/" + productId)
        .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isNoContent()); // Use isNoContent() instead of isOk()
```

### 5. Test Isolation

**Problem**: Tests interfere with each other due to shared state.

**Solution**:
- Clean up test data between test executions:

```java
@BeforeEach
void setUp() {
    productRepository.deleteAll();
    // Set up test data
}

@AfterEach
void tearDown() {
    productRepository.deleteAll();
}
```

By addressing these common issues, you can ensure your integration tests run reliably and accurately reflect the behavior of your application.