# ATDAID Framework Documentation

This directory contains comprehensive documentation for the ATDAID (Acceptance Test-Driven AI Development) Framework.

## Available Documentation

### Core Documentation

- [Main README](../README.md) - Overview of the ATDAID Framework, features, and getting started guide

### Feature Documentation

- [Product Management](ProductManagement.md) - Detailed documentation of the Product Management module, including domain model, API endpoints, and usage examples

### Testing Documentation

- [Testing Guide](Testing.md) - Comprehensive guide to testing approaches, including acceptance tests, integration tests, and service layer tests

### Logging and Reporting Documentation

- [Logging and Reporting Guide](Logging.md) - Detailed documentation of the logging and reporting capabilities, including Log4j 2 configuration and ExtentReports integration

## Recent Updates

The following documentation has been recently updated:

- **[Logging and Reporting Guide](Logging.md)** - Enhanced with detailed information about ExtentReports integration, including how to execute and view reports after test execution, programmatically generate reports, customize reports, and troubleshoot common issues. Added examples of base test classes, test listeners, and complete test examples with logging.

- **[Testing Guide](Testing.md)** - Added a new "Troubleshooting Integration Tests" section with solutions for common issues encountered in integration tests, including authentication problems, database schema mismatches, NULL value constraints, status code expectations, and test isolation.

- **[Product Management](ProductManagement.md)** - Added a new "Entity-Database Mapping" section detailing the mapping between entity fields and database columns, including table mapping, column naming conventions, database schema, and lifecycle callbacks.

- **[Main README](../README.md)** - Updated with comprehensive information about Log4j2 and ExtentReports integration, including features, project structure, test execution, report generation, and recent updates.

These updates reflect recent improvements to the codebase, particularly focusing on ensuring that integration tests run successfully and reliably, and enhancing the logging and reporting capabilities of the framework.

## Documentation Structure

Each feature documentation typically includes:

1. **Overview** - High-level description of the feature
2. **Domain Model** - Description of the entities and their relationships
3. **API Endpoints** - Details of the REST API endpoints
4. **Service Layer** - Description of the business logic
5. **Testing** - Information about how the feature is tested
6. **Usage Examples** - Code examples showing how to use the feature
7. **Best Practices** - Recommendations for working with the feature

## Contributing to Documentation

When adding new documentation:

1. Create a new Markdown file in the `docs` directory
2. Follow the existing documentation structure
3. Update this README.md file to include a reference to the new documentation
4. Ensure cross-references between related documentation

## Documentation Conventions

- Use Markdown for all documentation
- Include code examples where appropriate
- Use tables for structured data
- Include diagrams when necessary to explain complex concepts
- Keep documentation up-to-date with code changes 