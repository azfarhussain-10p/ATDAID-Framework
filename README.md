# ATDAID Framework

## Acceptance Test Driven AI Development Framework

ATDAID (Acceptance Test Driven AI Development) is a testing framework designed to facilitate test-driven development for AI-enhanced applications. It provides a structured approach to writing acceptance tests that can be used to validate the behavior of AI components within your application.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Project Structure](#project-structure)
- [Writing Tests](#writing-tests)
  - [Acceptance Tests](#acceptance-tests)
  - [Service Tests](#service-tests)
- [Running Tests](#running-tests)
- [Configuration](#configuration)
- [Logging](#logging)
- [Contributing](#contributing)
- [License](#license)

## Overview

ATDAID Framework is built on top of Spring Boot and provides a comprehensive testing infrastructure for developing applications with AI components. It follows the principles of Acceptance Test Driven Development (ATDD) and extends them to accommodate the unique challenges of testing AI systems.

## Features

- **Acceptance Testing**: Write high-level acceptance tests that validate the behavior of your application from an end-user perspective.
- **Service Testing**: Test individual services and components in isolation.
- **Mock Server Integration**: Use MockServer to simulate external dependencies and APIs.
- **REST Assured Integration**: Test RESTful APIs with ease.
- **Playwright Support**: Perform UI testing with Playwright.
- **TestNG Framework**: Leverage the power of TestNG for test organization and execution.
- **Spring Boot Integration**: Seamlessly integrate with Spring Boot applications.
- **H2 Database Support**: Use an in-memory H2 database for testing.
- **JWT Authentication**: Test authentication and authorization with JWT tokens.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/azfarhussain-10p/ATDAID-Framework.git
   cd ATDAID-Framework
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

## Project Structure

```
ATDAID-Framework/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── tenpearls/
│   │   │           ├── domain/         # Domain models
│   │   │           ├── persistence/    # Repository interfaces
│   │   │           ├── service/        # Service implementations
│   │   │           └── SimpleApplication.java
│   │   └── resources/
│   │       ├── application.properties  # Application configuration
│   │       ├── application.yml         # YAML configuration
│   │       └── logback.xml             # Logging configuration
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── tenpearls/
│       │           ├── accpetance/     # Acceptance tests
│       │           │   ├── auth/       # Authentication tests
│       │           │   └── BaseAcceptanceTest.java
│       │           ├── config/         # Test configuration
│       │           └── service/        # Service tests
│       └── resources/
│           └── application-test.properties  # Test configuration
└── resources/
    └── config/                         # Additional configuration
```

## Writing Tests

### Acceptance Tests

Acceptance tests validate the behavior of your application from an end-user perspective. They are written in a way that reflects the user's interaction with the system.

Example:

```java
@Test(description = "Register a new user with valid credentials")
public void testRegisterUserWithValidCredentials() {
    // Given
    Map<String, String> user = new HashMap<>();
    user.put("email", "test@example.com");
    user.put("password", "Password123");
    user.put("firstName", "Test");
    user.put("lastName", "User");
    
    // When
    Response response = given()
        .body(user)
        .when()
        .post("/api/auth/register")
        .then()
        .extract()
        .response();
    
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SC_CREATED);
    assertThat(response.jsonPath().getString("email")).isEqualTo("test@example.com");
}
```

### Service Tests

Service tests validate the behavior of individual services and components in isolation.

Example:

```java
@Test
public void testRegisterUser_Success() {
    // Given
    String email = "test@example.com";
    String password = "Password123";
    String firstName = "Test";
    String lastName = "User";
    
    // When
    User registeredUser = userService.registerUser(email, password, firstName, lastName);
    
    // Then
    assertThat(registeredUser).isNotNull();
    assertThat(registeredUser.getEmail()).isEqualTo(email);
}
```

## Running Tests

To run all tests:

```bash
mvn test
```

To run a specific test class:

```bash
mvn test -Dtest=UserRegistrationTest
```

To run a specific test method:

```bash
mvn test -Dtest=UserRegistrationTest#testRegisterUserWithValidCredentials
```

## Configuration

The framework uses Spring Boot's configuration system. You can configure the application using the following files:

- `src/main/resources/application.properties` or `application.yml`: Main application configuration
- `src/test/resources/application-test.properties`: Test-specific configuration

Example test configuration:

```properties
# Test configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# JWT configuration
jwt.secret=test-secret-key-for-testing-purposes-only
jwt.expiration=86400000

# Logging configuration
logging.level.root=INFO
logging.level.com.tenpearls=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

## Logging

The framework uses Logback for logging. You can configure logging in the `src/main/resources/logback.xml` file.

Example logging configuration:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
    
    <logger name="io.restassured" level="WARN" />
    <logger name="org.apache.http" level="WARN" />
</configuration>
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.