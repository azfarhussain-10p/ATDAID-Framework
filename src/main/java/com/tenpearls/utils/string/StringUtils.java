package com.tenpearls.utils.string;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for string operations.
 * Provides methods for common string manipulation tasks.
 */
public class StringUtils {
    
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    /**
     * Checks if a string is null or empty.
     * 
     * @param str The string to check
     * @return true if the string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Checks if a string is not null and not empty.
     * 
     * @param str The string to check
     * @return true if the string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * Truncates a string to the specified length.
     * 
     * @param str The string to truncate
     * @param maxLength The maximum length
     * @return The truncated string
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        
        if (str.length() <= maxLength) {
            return str;
        }
        
        return str.substring(0, maxLength);
    }
    
    /**
     * Generates a random UUID string.
     * 
     * @return A random UUID string
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Validates if a string is a valid email address.
     * 
     * @param email The email address to validate
     * @return true if the email is valid
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Capitalizes the first letter of a string.
     * 
     * @param str The string to capitalize
     * @return The capitalized string
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    /**
     * Removes all whitespace from a string.
     * 
     * @param str The string to process
     * @return The string with all whitespace removed
     */
    public static String removeWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        return str.replaceAll("\\s+", "");
    }
} 