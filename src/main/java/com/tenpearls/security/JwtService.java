package com.tenpearls.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final String secretKey;
    private final long jwtExpiration;

    public JwtService(
            @Value("${application.security.jwt.secret-key}") String secretKey,
            @Value("${application.security.jwt.expiration}") long jwtExpiration) {
        System.out.println("Initializing JwtService with secret key: " + secretKey);
        System.out.println("Initializing JwtService with expiration: " + jwtExpiration);
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    public String extractUsername(String token) {
        System.out.println("Extracting username from token");
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        System.out.println("Extracting claim from token");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        System.out.println("Generating token for user: " + userDetails.getUsername());
        System.out.println("Using secret key: " + secretKey);
        System.out.println("Using expiration: " + jwtExpiration);
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        System.out.println("Added authorities to claims: " + claims.get("authorities"));
        return generateToken(claims, userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        System.out.println("Building token with expiration: " + expiration);
        String token = Jwts
                .builder()
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
        System.out.println("Generated token: " + token);
        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        System.out.println("Validating token for user: " + userDetails.getUsername());
        System.out.println("Using secret key: " + secretKey);
        final String username = extractUsername(token);
        boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        System.out.println("Token validation result: " + isValid);
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        boolean isExpired = expiration.before(new Date());
        System.out.println("Token expiration date: " + expiration);
        System.out.println("Current date: " + new Date());
        System.out.println("Is token expired: " + isExpired);
        return isExpired;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        System.out.println("Extracting all claims from token using secret key: " + secretKey);
        try {
            Claims claims = Jwts
                    .parser()
                    .verifyWith((SecretKey) getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            System.out.println("Successfully extracted claims: " + claims);
            return claims;
        } catch (Exception e) {
            System.out.println("Error extracting claims: " + e.getMessage());
            throw e;
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}