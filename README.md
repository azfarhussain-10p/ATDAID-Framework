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

## Project Structure

```
├── src
│   ├── main
│   │   ├── java           # Main application code
│   │   └── resources      # Application properties
│   └── test
│       ├── java           # Test code
│       └── resources      # Test properties
└── pom.xml               # Maven configuration
```

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL (for production)
- H2 (for testing)

## Building the Project

```bash
mvn clean install
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=ProductManagementTest

# Run JUnit integration tests
mvn test -Dtest=ProductManagementIntegrationTest
```

## Workflow

1. **Write acceptance tests**: Define what your feature should do
2. **Run the framework**: Let AI implement the feature
3. **Refine**: Make adjustments as needed
4. **Repeat**: For each new feature

## API Endpoints

### Product Management

- `POST /api/products` - Create a new product (admin only)
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products` - Get all products

### Authentication

- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/register` - Register a new user

## Adding New Tests

### Acceptance Tests (TestNG)

Create a new test class in the `src/test/java/com/tenpearls/accpetance` package. These tests verify that your application meets business requirements from a user's perspective.

### Integration Tests (JUnit)

Create a new test class in the `src/test/java/com/tenpearls/integration` package. These tests verify that different components work together correctly.

## Using AI-Driven Implementation

```bash
# Implement from a single test file
java -jar app.jar implement src/test/java/com/tenpearls/accpetance/yourfeature/YourFeatureTest.java

# Run tests to verify implementation
java -jar app.jar run src/test/java/com/tenpearls/accpetance/yourfeature/YourFeatureTest.java

# Implement and run in one step
java -jar app.jar implement-and-run src/test/java/com/tenpearls/accpetance/yourfeature/YourFeatureTest.java
```