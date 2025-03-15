# Test Utilities Organization

This directory contains utility classes for testing purposes, organized by domain. Each subdirectory contains utilities related to a specific testing domain or functionality.

## Directory Structure

- `security/` - Security-related test utilities
- `time/` - Date and time test utilities

## Best Practices

1. **Domain-Specific Organization**: Place test utility classes in domain-specific subdirectories rather than in the root utils directory.
2. **Naming Conventions**: Use clear, descriptive names for test utility classes and methods.
3. **Documentation**: Include comprehensive JavaDoc comments for all test utility classes and methods.
4. **Test-Specific**: These utilities should be used only for testing purposes and should not be used in production code.
5. **Static Methods**: Test utility classes should contain only static methods and should not be instantiated.
6. **Null Handling**: All test utility methods should handle null inputs gracefully.

## Adding New Test Utilities

When adding new test utility classes:

1. Identify the appropriate domain subdirectory
2. If no appropriate subdirectory exists, create a new one
3. Follow the existing naming conventions and code style
4. Include comprehensive JavaDoc comments
5. Update this README.md if necessary

## Example Usage

```java
// Security utilities
import com.tenpearls.utils.security.PasswordHashGenerator;

String hashedPassword = PasswordHashGenerator.generateHash("password123");

// Time utilities
import com.tenpearls.utils.time.DateTimeUtils;

String currentDateTime = DateTimeUtils.getCurrentDateTime();
``` 