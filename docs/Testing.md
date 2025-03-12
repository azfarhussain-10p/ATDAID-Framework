# Testing Guide for ATDAID Framework

## Types of Tests

ATDAID Framework supports two main types of tests: acceptance tests and integration tests.

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