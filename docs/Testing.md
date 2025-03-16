# ATDAID Framework Testing Guide

This document provides comprehensive information about the testing approach, methodologies, and best practices used in the ATDAID Framework.

## Table of Contents

1. [Overview](#overview)
2. [Test Types](#test-types)
3. [Test Structure](#test-structure)
4. [Running Tests](#running-tests)
5. [Test Fixtures](#test-fixtures)
6. [Mocking and Stubbing](#mocking-and-stubbing)
7. [Test Logging](#test-logging)
8. [Continuous Integration](#continuous-integration)
9. [Best Practices](#best-practices)
10. [Troubleshooting](#troubleshooting)

## Overview

The ATDAID Framework employs a comprehensive testing strategy to ensure code quality, reliability, and maintainability. Our testing approach follows these principles:

- **Test-Driven Development (TDD)**: Tests are written before implementation code
- **Automated Testing**: All tests are automated and can be run as part of the build process
- **Comprehensive Coverage**: Unit, integration, and feature tests cover all aspects of the application
- **Isolation**: Tests are isolated from each other to prevent interdependencies
- **Readability**: Tests are written to be readable and maintainable

## Test Types

### Unit Tests

Unit tests focus on testing individual components in isolation:

- **Service Tests**: Test service layer logic
- **Repository Tests**: Test data access logic
- **Utility Tests**: Test utility classes and helper functions

Example unit test:

```java
@Test
public void testCreateProduct_Success() {
    // Arrange
    ProductDTO productDTO = new ProductDTO();
    productDTO.setName("Test Product");
    productDTO.setSku("SKU123");
    productDTO.setPrice(BigDecimal.valueOf(99.99));
    productDTO.setStockQuantity(100);
    
    when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());
    when(productRepository.save(any(Product.class))).thenReturn(mockProduct());
    
    // Act
    Product result = productService.createProduct(productDTO);
    
    // Assert
    assertNotNull(result);
    assertEquals("Test Product", result.getName());
    assertEquals("SKU123", result.getSku());
    verify(productRepository).save(any(Product.class));
}
```

### Integration Tests

Integration tests verify that different components work together correctly:

- **API Integration Tests**: Test REST API endpoints
- **Database Integration Tests**: Test database interactions
- **Service Integration Tests**: Test service interactions

Example integration test:

```java
@Test
public void testGetAllActiveProducts_Success() {
    // Arrange
    Product testProduct = createTestProduct(true);
    productRepository.save(testProduct);
    
    // Act
    MvcResult result = mockMvc.perform(get("/api/products")
            .header("Authorization", "Bearer " + userToken)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    
    // Assert
    String content = result.getResponse().getContentAsString();
    List<ProductDTO> products = objectMapper.readValue(content, 
            new TypeReference<List<ProductDTO>>() {});
    
    assertFalse(products.isEmpty());
    assertTrue(products.stream()
            .anyMatch(p -> p.getSku().equals(testProduct.getSku())));
}
```

### Feature Tests

Feature tests verify that the application meets business requirements:

- **End-to-End Tests**: Test complete user workflows
- **Acceptance Tests**: Verify acceptance criteria
- **Scenario Tests**: Test specific business scenarios

Example feature test:

```java
@Test
public void testCompleteOrderWorkflow() {
    // Create a product
    ProductDTO product = createProduct();
    
    // Add product to cart
    CartDTO cart = addToCart(product.getId(), 2);
    
    // Checkout
    OrderDTO order = checkout(cart.getId(), paymentDetails);
    
    // Verify order status
    assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    
    // Verify inventory updated
    ProductDTO updatedProduct = getProduct(product.getId());
    assertEquals(product.getStockQuantity() - 2, updatedProduct.getStockQuantity());
}
```

## Test Structure

Tests in the ATDAID Framework follow a consistent structure:

### Test Class Organization

```java
@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

    // Test fixtures and mocks
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    @BeforeEach
    public void setUp() {
        // Common setup for all tests
        MockitoAnnotations.openMocks(this);
        // Additional setup
    }
    
    @AfterEach
    public void tearDown() {
        // Clean up after tests
    }
    
    // Test methods grouped by functionality
    
    // Create operations
    @Test
    public void testCreateProduct_Success() { /* ... */ }
    
    @Test
    public void testCreateProduct_DuplicateSku() { /* ... */ }
    
    // Read operations
    @Test
    public void testGetProductById_Success() { /* ... */ }
    
    // Update operations
    @Test
    public void testUpdateProduct_Success() { /* ... */ }
    
    // Delete operations
    @Test
    public void testDeleteProduct_Success() { /* ... */ }
    
    // Helper methods
    private Product mockProduct() { /* ... */ }
}
```

### Test Method Structure

Each test method follows the Arrange-Act-Assert (AAA) pattern:

```java
@Test
public void testMethodName_Scenario() {
    // Arrange - Set up the test conditions
    // ...
    
    // Act - Execute the method being tested
    // ...
    
    // Assert - Verify the results
    // ...
}
```

## Running Tests

### Running All Tests

```bash
mvn test
```

### Running Specific Test Classes

```bash
mvn test -Dtest=ProductServiceTest
```

### Running Specific Test Methods

```bash
mvn test -Dtest=ProductServiceTest#testCreateProduct_Success
```

### Running Tests by Category

```bash
mvn test -Dgroups="UnitTest,FastTest"
```

## Test Fixtures

### Test Data Builders

The framework uses builder patterns for creating test data:

```java
public class ProductBuilder {
    private Long id = 1L;
    private String name = "Default Product";
    private String sku = "SKU-DEFAULT";
    private BigDecimal price = BigDecimal.valueOf(99.99);
    private Integer stockQuantity = 100;
    private boolean active = true;
    
    public ProductBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    // Other builder methods
    
    public Product build() {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setSku(sku);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setActive(active);
        return product;
    }
}
```

### Test Utilities

Common test utilities are available in the `com.tenpearls.test.utils` package:

```java
// Create a test user with specified role
public static User createTestUser(String email, Role role) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode("password"));
    user.setRole(role);
    return user;
}

// Generate a valid JWT token for testing
public static String generateTestToken(User user) {
    return jwtService.generateToken(user);
}
```

## Mocking and Stubbing

The framework uses Mockito for mocking dependencies:

```java
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    @Test
    public void testGetProductById_Success() {
        // Arrange
        Long productId = 1L;
        Product mockProduct = new ProductBuilder().withId(productId).build();
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        
        // Act
        Product result = productService.getProductById(productId);
        
        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productRepository).findById(productId);
    }
}
```

## Test Logging

The framework includes specialized logging for tests:

```java
@ExtendWith(TestLoggerExtension.class)
public class ProductManagementIntegrationTest {

    @Test
    public void testCreateProduct_AsAdmin_Success() {
        LoggerUtils.getInstance().testStep("Creating a new product as admin");
        
        // Test implementation
        
        LoggerUtils.getInstance().testAssertion("Product created successfully");
    }
}
```

Test logs are stored in a separate directory and include:
- Test execution details
- Test steps and assertions
- Performance metrics
- Test context information

## Continuous Integration

The ATDAID Framework is configured for continuous integration with:

- **Automated Test Execution**: Tests run automatically on each commit
- **Test Reports**: Detailed test reports are generated
- **Code Coverage**: Code coverage reports track test coverage
- **Quality Gates**: Tests must pass before code can be merged

## Best Practices

### Writing Effective Tests

1. **Test One Thing Per Test**: Each test should verify a single behavior
2. **Use Descriptive Test Names**: Test names should describe the scenario and expected outcome
3. **Keep Tests Independent**: Tests should not depend on each other
4. **Avoid Test Logic**: Minimize conditional logic in tests
5. **Test Edge Cases**: Include tests for boundary conditions and error scenarios

### Test Data Management

1. **Use Test Data Builders**: Create reusable builders for test data
2. **Avoid Hardcoded Values**: Use constants or generate test data
3. **Clean Up Test Data**: Ensure tests clean up after themselves
4. **Use In-Memory Databases**: Use H2 or other in-memory databases for tests

### Test Performance

1. **Keep Tests Fast**: Unit tests should run in milliseconds
2. **Use Test Categories**: Separate fast and slow tests
3. **Optimize Test Execution**: Run tests in parallel when possible
4. **Mock External Dependencies**: Use mocks for external services

## Troubleshooting

### Common Test Issues

1. **Flaky Tests**: Tests that sometimes pass and sometimes fail
   - Solution: Identify and eliminate test dependencies
   - Solution: Add proper synchronization for asynchronous operations

2. **Slow Tests**: Tests that take too long to run
   - Solution: Use mocks instead of real implementations
   - Solution: Optimize database operations
   - Solution: Run slow tests separately

3. **Resource Conflicts**: Tests competing for the same resources
   - Solution: Use unique resources for each test
   - Solution: Clean up resources after tests
   - Solution: Run conflicting tests separately

### Debugging Tests

1. Enable test logging:
   ```properties
   logging.level.com.tenpearls.test=DEBUG
   ```

2. Use the `@TestLoggerExtension` to capture detailed test logs

3. Run tests with the debug option:
   ```bash
   mvn test -Dmaven.surefire.debug
   ```

4. Inspect test reports in the `target/surefire-reports` directory
