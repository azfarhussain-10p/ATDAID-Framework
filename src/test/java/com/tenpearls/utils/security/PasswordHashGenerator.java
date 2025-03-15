package com.tenpearls.utils.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for generating password hashes for testing purposes.
 * This class provides a simple way to generate BCrypt password hashes
 * that can be used in test data setup.
 */
public class PasswordHashGenerator {
    
    /**
     * Generates BCrypt password hashes for admin and user test accounts.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "TestAdmin123!";
        String userPassword = "TestUser123!";
        
        System.out.println("Admin password hash: " + encoder.encode(adminPassword));
        System.out.println("User password hash: " + encoder.encode(userPassword));
    }
    
    /**
     * Generates a BCrypt password hash for a given plain text password.
     * 
     * @param plainTextPassword The password to hash
     * @return The BCrypt hashed password
     */
    public static String generateHash(String plainTextPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(plainTextPassword);
    }
} 