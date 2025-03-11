package com.tenpearls.controller;

import com.tenpearls.dto.AuthResponse;
import com.tenpearls.dto.LoginRequest;
import com.tenpearls.dto.RegisterRequest;
import com.tenpearls.model.User;
import com.tenpearls.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = userService.loginUser(request.getEmail(), request.getPassword());
            User user = userService.getUserByEmail(request.getEmail());
            
            return ResponseEntity.ok(new AuthResponse(token, user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        }
    }
}