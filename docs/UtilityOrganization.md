# Utility Organization Guide

This guide explains the domain-driven approach to organizing utility classes in the ATDAID Framework. Instead of placing all utilities in a single directory, they are organized by domain to improve maintainability and discoverability.

## Directory Structure

### Main Source Code

Utilities in the main source code are organized in the following structure:

```
src/main/java/com/tenpearls/utils/
├── README.md
├── file/
│   └── FileUtils.java
├── logging/
│   └── LoggerUtils.java
└── string/
    └── StringUtils.java
```

### Test Source Code

Utilities in the test source code are organized in the following structure:

```
src/test/java/com/tenpearls/utils/
├── README.md
├── security/
│   └── PasswordHashGenerator.java
└── time/
    └── DateTimeUtils.java
```

## Domain-Specific Utilities

### File Utilities

The `file` package contains utilities for file operations, such as reading and writing files, creating directories, and manipulating file paths.

Example usage:
```java
import com.tenpearls.utils.file.FileUtils;

try {
    // Read a file
    String content = FileUtils.readFileAsString("path/to/file.txt");
    
    // Write to a file
    FileUtils.writeStringToFile("Hello, World!", "path/to/output.txt");
    
    // Create a directory
    FileUtils.createDirectoryIfNotExists("path/to/directory");
} catch (IOException e) {
    // Handle exception
}
```

### Logging Utilities

The `logging` package contains utilities for logging with Log4j 2, providing a simplified interface for common logging operations.

Example usage:
```java
import com.tenpearls.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;

public class MyClass {
    private static final Logger logger = LoggerUtils.getLogger(MyClass.class);
    
    public void doSomething() {
        LoggerUtils.info(logger, "Starting operation");
        
        try {
            // Do something
            LoggerUtils.debug(logger, "Operation details: {}", details);
        } catch (Exception e) {
            LoggerUtils.error(logger, "Operation failed", e);
        }
        
        LoggerUtils.info(logger, "Operation completed");
    }
}
```

### String Utilities

The `string` package contains utilities for string manipulation, such as checking if a string is empty, truncating strings, and validating email addresses.

Example usage:
```java
import com.tenpearls.utils.string.StringUtils;

// Check if a string is empty
if (StringUtils.isEmpty(input)) {
    // Handle empty input
}

// Truncate a string
String truncated = StringUtils.truncate(input, 100);

// Validate an email address
if (StringUtils.isValidEmail(email)) {
    // Email is valid
}
```

### Security Utilities (Test)

The `security` package in the test source code contains utilities for security-related testing, such as generating password hashes.

Example usage:
```java
import com.tenpearls.utils.security.PasswordHashGenerator;

// Generate a password hash for testing
String hashedPassword = PasswordHashGenerator.generateHash("password123");
```

### Time Utilities (Test)

The `time` package in the test source code contains utilities for date and time operations in tests, such as getting the current date and time in a specific format.

Example usage:
```java
import com.tenpearls.utils.time.DateTimeUtils;

// Get the current date and time
String currentDateTime = DateTimeUtils.getCurrentDateTime();

// Format a date
String formattedDate = DateTimeUtils.formatDate(new Date(), "yyyy-MM-dd");
```

## Best Practices

### 1. Domain-Specific Organization

Place utility classes in domain-specific subdirectories rather than in the root utils directory. This makes it easier to find utilities related to a specific domain.

### 2. Naming Conventions

Use clear, descriptive names for utility classes and methods. Utility class names should end with "Utils" to indicate their purpose.

### 3. Documentation

Include comprehensive JavaDoc comments for all utility classes and methods. This makes it easier for other developers to understand how to use the utilities.

### 4. Immutability

Utility classes should be immutable and stateless. They should not maintain any state between method calls.

### 5. Static Methods

Utility classes should contain only static methods and should not be instantiated. Consider adding a private constructor to prevent instantiation.

```java
public class StringUtils {
    // Private constructor to prevent instantiation
    private StringUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }
    
    // Static utility methods
    // ...
}
```

### 6. Null Handling

All utility methods should handle null inputs gracefully. This prevents NullPointerExceptions and makes the utilities more robust.

```java
public static boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
}
```

### 7. Exception Handling

Utility methods should handle exceptions appropriately. If a method can throw an exception, it should be documented in the JavaDoc.

```java
/**
 * Reads the contents of a file as a string.
 * 
 * @param filePath The path to the file
 * @return The contents of the file as a string
 * @throws IOException If an I/O error occurs
 */
public static String readFileAsString(String filePath) throws IOException {
    // Implementation
}
```

## Adding New Utilities

When adding new utility classes:

1. Identify the appropriate domain subdirectory
2. If no appropriate subdirectory exists, create a new one
3. Follow the existing naming conventions and code style
4. Include comprehensive JavaDoc comments
5. Add unit tests for all utility methods
6. Update the README.md files if necessary

## Conclusion

The domain-driven approach to organizing utility classes improves maintainability and discoverability. By following the best practices outlined in this guide, you can ensure that your utility classes are well-organized, well-documented, and easy to use.
