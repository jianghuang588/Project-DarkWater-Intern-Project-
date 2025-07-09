package com.example.demo.dto;

/**
 * Data Transfer Object for authentication response
 * Returned after successful login to provide JWT token to client
 * Client uses this token for subsequent authenticated API requests
 */
public class AuthResponseDto {
    /**
     * JWT token string that contains encoded user information
     * Client must include this in Authorization header for protected endpoints
     * Example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     */
    private String accessToken;

    /**
     * Type of token - typically "Bearer"
     * Tells client how to use the token in Authorization header
     * Full header format: "Authorization: Bearer {accessToken}"
     */
    private String tokenType;

    /**
     * Constructor for creating auth response after successful login
     * @param accessToken - generated JWT token
     * @param tokenType - usually "Bearer"
     */
    public AuthResponseDto(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    // Getters and Setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}