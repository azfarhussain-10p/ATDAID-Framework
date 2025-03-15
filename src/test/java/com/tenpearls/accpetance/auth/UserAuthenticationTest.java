package com.tenpearls.accpetance.auth;

import com.tenpearls.accpetance.BaseAcceptanceTest;
import com.tenpearls.dto.AuthRequest;
import com.tenpearls.dto.RegisterRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Acceptance tests for user authentication.
 */
public class UserAuthenticationTest extends BaseAcceptanceTest {

    @Test(groups = {"smoke", "auth"})
    public void testRegisterUser_Success() {
        logger.info("Testing registerUser_Success");
        
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("test_" + System.currentTimeMillis() + "@example.com")
                .password("Password123")
                .build();
        
        // When
        Response response = given()
                .body(request)
                .when()
                .post("/api/auth/register");
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("id")).isNotNull();
        assertThat(response.jsonPath().getString("email")).isEqualTo(request.getEmail());
        assertThat(response.jsonPath().getString("firstName")).isEqualTo(request.getFirstName());
        assertThat(response.jsonPath().getString("lastName")).isEqualTo(request.getLastName());
        
        logger.debug("registerUser_Success test completed");
    }
    
    @Test(groups = {"smoke", "auth"})
    public void testLoginUser_Success() {
        logger.info("Testing loginUser_Success");
        
        // Given
        String email = "test_" + System.currentTimeMillis() + "@example.com";
        String password = "Password123";
        
        // Register a user first
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .password(password)
                .build();
        
        given()
                .body(registerRequest)
                .when()
                .post("/api/auth/register");
        
        // Login request
        AuthRequest loginRequest = AuthRequest.builder()
                .email(email)
                .password(password)
                .build();
        
        // When
        Response response = given()
                .body(loginRequest)
                .when()
                .post("/api/auth/login");
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("token")).isNotNull();
        
        logger.debug("loginUser_Success test completed");
    }
    
    @Test(groups = {"auth"})
    public void testLoginUser_InvalidCredentials() {
        logger.info("Testing loginUser_InvalidCredentials");
        
        // Given
        AuthRequest request = AuthRequest.builder()
                .email("nonexistent@example.com")
                .password("WrongPassword")
                .build();
        
        // When
        Response response = given()
                .body(request)
                .when()
                .post("/api/auth/login");
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(401);
        
        logger.debug("loginUser_InvalidCredentials test completed");
    }
} 