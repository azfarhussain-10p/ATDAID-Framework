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

- [Logging and Reporting Guide](Logging.md) - Detailed documentation of the logging and reporting capabilities, including Log4j 2 configuration, daily log organization, and ExtentReports integration
- [Enhanced Logging Capabilities](LoggingEnhancements.md) - Summary of the enhanced logging capabilities, including structured directory organization, log utilities, and integration with test frameworks

### Architecture Documentation

- [Utility Organization](UtilityOrganization.md) - Guide to the domain-driven organization of utility classes in the framework
- [Utility Organization README](../src/main/java/com/tenpearls/utils/README.md) - Detailed README for the utils directory

## Recent Updates

The following documentation has been recently updated:

- **[Enhanced Logging Capabilities](LoggingEnhancements.md)** - New document providing a comprehensive summary of the enhanced logging capabilities, including structured directory organization, log utilities, automatic directory creation, and integration with test frameworks.

- **[Logging and Reporting Guide](Logging.md)** - Completely revamped with comprehensive information about:
  - Enhanced log directory structure with daily organization
  - Correlation IDs for tracking related log entries
  - Visual enhancements for improved log readability
  - Automatic log rotation, compression, and cleanup
  - LoggerUtils class for consistent and enhanced logging
  - ExtentReports integration with detailed test reporting
  - Programmatic report generation and customization
  - Best practices for effective logging and reporting
  - Troubleshooting common logging and reporting issues

- **[Testing Guide](Testing.md)** - Added a new "Troubleshooting Integration Tests" section with solutions for common issues encountered in integration tests, including authentication problems, database schema mismatches, NULL value constraints, status code expectations, and test isolation.

- **[Product Management](ProductManagement.md)** - Added a new "Entity-Database Mapping" section detailing the mapping between entity fields and database columns, including table mapping, column naming conventions, database schema, and lifecycle callbacks.

- **[Main README](../README.md)** - Updated with comprehensive information about Log4j2 and ExtentReports integration, including features, project structure, test execution, report generation, and recent updates. Added a new section on utility organization.

- **[Utility Organization](UtilityOrganization.md)** - Added comprehensive documentation on the domain-driven organization of utility classes, including best practices, directory structure, and examples of domain-specific utilities.

These updates reflect recent improvements to the codebase, particularly focusing on enhancing the logging and reporting capabilities of the framework, ensuring that integration tests run successfully and reliably, and improving code organization through domain-driven utility classes.

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