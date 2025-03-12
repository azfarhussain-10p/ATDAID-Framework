# ATDAID Framework

## Acceptance Test-Driven AI Development Framework

ATDAID (Acceptance Test-Driven AI Development) is a framework that combines the principles of Acceptance Test-Driven Development (ATDD) with AI-powered implementation. This approach allows developers to write acceptance tests first and then use AI to automatically generate the implementation code.

## Features

- **TestNG and JUnit Integration**: Support for both TestNG and JUnit testing frameworks
- **AI-Driven Implementation**: Automatic code generation based on test specifications
- **Product Management API**: RESTful API for product management with authentication
- **JWT Authentication**: Secure authentication using JWT tokens

## Project Structure

```
ATDAID-Framework/
├── src/
│   ├── main/java/com/tenpearls/
│   │   ├── api/                  # REST API controllers
│   │   ├── service/              # Business logic services
│   │   │   └── mcp/              # AI service integrations
│   │   └── testng/               # TestNG integration
│   └── test/java/com/tenpearls/
│       ├── accpetance/           # Acceptance tests (TestNG)
│       ├── integration/          # Integration tests (JUnit)
│       └── service/              # Service unit tests
└── pom.xml                       # Maven configuration
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Building the Project

```bash
mvn clean install
```

### Running Tests

Run all tests:
```bash
mvn test
```

Run only TestNG tests:
```bash
mvn test -Dtest=*Test
```

Run only JUnit integration tests:
```bash
mvn test -Pjunit -Dtest=*IntegrationTest
```

## Test-Driven Development Workflow

1. Write acceptance tests that define the expected behavior
2. Run the tests (they will fail initially)
3. Use the AI-driven implementation service to generate code
4. Run the tests again to verify the implementation
5. Refine the implementation as needed

## API Endpoints

### Product Management

- `POST /api/products`: Create a new product (admin only)
- `GET /api/products/{id}`: Get a product by ID
- `GET /api/products`: Get all products

### Authentication

- `POST /api/auth/register`: Register a new user
- `POST /api/auth/login`: Login and get JWT token

## Documentation

Detailed documentation for each feature is available in the `docs` directory:

- [Product Management](docs/product-management.md): Documentation for the product management API

## License

This project is licensed under the MIT License - see the LICENSE file for details.