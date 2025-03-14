package com.tenpearls.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "TestAdmin123!";
        String userPassword = "TestUser123!";
        
        System.out.println("Admin password hash: " + encoder.encode(adminPassword));
        System.out.println("User password hash: " + encoder.encode(userPassword));
    }
} 