# ATDAID Framework Security

The ATDAID Framework provides a comprehensive security system built on Spring Security with JWT (JSON Web Token) authentication and authorization. This document outlines the security architecture, configuration options, and best practices.

## Table of Contents

1. [Overview](#overview)
2. [JWT Authentication](#jwt-authentication)
3. [Role-Based Access Control](#role-based-access-control)
4. [Security Configuration](#security-configuration)
5. [Best Practices](#best-practices)
6. [Recent Improvements](#recent-improvements)

## Overview

The security system is designed to provide:

- Secure authentication using JWT tokens
- Role-based access control for API endpoints
- Protection against common security vulnerabilities
- Comprehensive logging of security events
- Modern cryptographic algorithms for token signing and verification

## JWT Authentication

The framework uses JWT (JSON Web Token) for authentication. JWT is a compact, URL-safe means of representing claims to be transferred between two parties. The claims in a JWT are encoded as a JSON object that is digitally signed using a secret key.

### JWT Flow

1. User provides credentials (username/password)
2. Server validates credentials and generates a JWT token
3. Token is returned to the client
4. Client includes token in the Authorization header for subsequent requests
5. Server validates the token and processes the request if valid

### JWT Token Structure

The JWT token consists of three parts:

1. **Header**: Contains the token type and signing algorithm
2. **Payload**: Contains the claims (user information, roles, expiration time)
3. **Signature**: Ensures the token hasn't been tampered with

Example token payload:
```json
{
  "sub": "user@example.com",
  "authorities": ["ROLE_USER"],
  "iat": 1742084086,
  "exp": 1742170486
}
```

## Role-Based Access Control

The framework implements role-based access control (RBAC) to restrict access to API endpoints based on user roles:

- **ROLE_USER**: Basic access to view products and user-specific operations
- **ROLE_ADMIN**: Full access to all operations, including product management

Example security configuration:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/products").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

## Security Configuration

The security system can be configured through the `application.properties` file:

```properties
# JWT Configuration
application.security.jwt.secret-key=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
application.security.jwt.expiration=86400000  # 24 hours in milliseconds
```

## Best Practices

1. **Use HTTPS**: Always use HTTPS in production to encrypt data in transit
2. **Secure Secret Key**: Use a strong, randomly generated secret key and keep it secure
3. **Short Token Expiration**: Use short-lived tokens to minimize the impact of token theft
4. **Validate Input**: Always validate and sanitize user input to prevent injection attacks
5. **Principle of Least Privilege**: Grant only the minimum necessary permissions to users
6. **Log Security Events**: Log all security-related events for audit and troubleshooting

## Recent Improvements

### Modern JWT Implementation

The JWT implementation has been updated to use the latest JJWT library features:

- Replaced deprecated `SignatureAlgorithm.HS256` with the modern `Jwts.SIG.HS384` approach
- Updated method signatures for `signWith` and `verifyWith` to use the latest API
- Changed return type of `getSignInKey()` to `SecretKey` for better type safety
- Enhanced token validation with improved error handling

### Code Example

```java
// Modern approach for building JWT tokens
String token = Jwts
    .builder()
    .subject(userDetails.getUsername())
    .claims(extraClaims)
    .issuedAt(new Date(System.currentTimeMillis()))
    .expiration(new Date(System.currentTimeMillis() + expiration))
    .signWith(getSignInKey())
    .compact();

// Modern approach for parsing JWT tokens
Claims claims = Jwts
    .parser()
    .verifyWith((SecretKey) getSignInKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

### Enhanced Logging

Security operations now include detailed logging:

- Token generation events with user information (without sensitive data)
- Token validation results
- Authentication successes and failures
- Authorization decisions

This improved logging helps with troubleshooting security issues and provides an audit trail for security events. 