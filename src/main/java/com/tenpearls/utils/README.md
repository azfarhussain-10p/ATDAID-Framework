# Utilities Organization

This directory contains utility classes organized by domain. Each subdirectory contains utilities related to a specific domain or functionality.

## Directory Structure

- `file/` - File-related utilities
- `logging/` - Logging-related utilities
- `string/` - String manipulation utilities
- `time/` - Date and time utilities

## Best Practices

1. **Domain-Specific Organization**: Place utility classes in domain-specific subdirectories rather than in the root utils directory.
2. **Naming Conventions**: Use clear, descriptive names for utility classes and methods.
3. **Documentation**: Include comprehensive JavaDoc comments for all utility classes and methods.
4. **Immutability**: Utility classes should be immutable and stateless.
5. **Static Methods**: Utility classes should contain only static methods and should not be instantiated.
6. **Null Handling**: All utility methods should handle null inputs gracefully.

## Adding New Utilities

When adding new utility classes:

1. Identify the appropriate domain subdirectory
2. If no appropriate subdirectory exists, create a new one
3. Follow the existing naming conventions and code style
4. Include comprehensive JavaDoc comments
5. Add unit tests for all utility methods
6. Update this README.md if necessary

## Example Usage

```java
// String utilities
import com.tenpearls.utils.string.StringUtils;

if (StringUtils.isNotEmpty(input)) {
    String truncated = StringUtils.truncate(input, 100);
    // ...
}

// File utilities
import com.tenpearls.utils.file.FileUtils;

try {
    String content = FileUtils.readFileAsString("path/to/file.txt");
    // ...
} catch (IOException e) {
    // Handle exception
}

// Logging utilities
import com.tenpearls.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;

Logger logger = LoggerUtils.getLogger(MyClass.class);
LoggerUtils.info(logger, "This is an info message");
``` 