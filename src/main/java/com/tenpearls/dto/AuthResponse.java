package com.tenpearls.dto;

public class AuthResponse {

    private String token;
    private Long userId;
    private String error;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
        this.error = null;
    }

    public AuthResponse(String error) {
        this.token = null;
        this.userId = null;
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }
}