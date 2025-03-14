package com.tenpearls.service;

import com.tenpearls.model.Role;
import com.tenpearls.model.User;
import com.tenpearls.repository.UserRepository;
import com.tenpearls.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    // Password must be at least 8 characters, contain at least one digit, one lowercase, one uppercase
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public User registerUser(String firstName, String lastName, String email, String password) {
        // Validate email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Validate password strength
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must be at least 8 characters and contain at least one digit, one lowercase, and one uppercase letter");
        }
        
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already registered");
        }
        
        // Create new user
        User user = new User(
            firstName,
            lastName,
            email,
            passwordEncoder.encode(password),
            Role.ROLE_USER
        );
        
        return userRepository.save(user);
    }

    public String loginUser(String email, String password) {
        try {
            System.out.println("Attempting to authenticate user with email: " + email);
            
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            System.out.println("Authentication successful for user: " + email);
            User user = (User) authentication.getPrincipal();
            System.out.println("User role: " + user.getRole());
            
            // Generate token
            String token = jwtService.generateToken(user);
            System.out.println("JWT token generated successfully for user: " + email);
            
            return token;
        } catch (Exception e) {
            System.out.println("Authentication failed for user: " + email + ", Error: " + e.getMessage());
            e.printStackTrace();
            throw e; // Let Spring Security handle the exception
        }
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}