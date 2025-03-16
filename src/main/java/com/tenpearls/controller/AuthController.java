package com.tenpearls.controller;

import com.tenpearls.dto.AuthResponse;
import com.tenpearls.dto.LoginRequest;
import com.tenpearls.dto.RegisterRequest;
import com.tenpearls.model.User;
import com.tenpearls.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword()
            );
            
            String token = userService.loginUser(request.getEmail(), request.getPassword());
            
            return ResponseEntity.ok(new AuthResponse(token, user.getId()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        // Log the request details
        System.out.println("Login request received - Email: " + request.getEmail());
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
            System.out.println("Validation errors: " + errors);
            return ResponseEntity.badRequest().body(new AuthResponse(errors));
        }

        try {
            // Log the authentication attempt
            System.out.println("Attempting authentication for user: " + request.getEmail());
            
            // Attempt to login
            String token = userService.loginUser(request.getEmail(), request.getPassword());
            
            // If successful, get the user details
            User user = userService.getUserByEmail(request.getEmail());
            
            System.out.println("Login successful for user: " + request.getEmail() + " with role: " + user.getRole());
            return ResponseEntity.ok(new AuthResponse(token, user.getId()));
        } catch (Exception e) {
            System.out.println("Login failed for user: " + request.getEmail() + " - Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body(new AuthResponse("Invalid email or password"));
        }
    }
}