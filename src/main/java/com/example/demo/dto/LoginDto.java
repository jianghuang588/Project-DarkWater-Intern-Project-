package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user login requests
 * Used for authentication endpoint to validate user credentials
 * Returns JWT token on successful authentication
 */
public class LoginDto {

    /**
     * User identifier - can be either username OR email
     * Provides flexibility for users to login with either credential
     * Service layer will check both username and email fields in database
     * Example: "john_doe" or "john@gmail.com"
     */
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    /**
     * User's password for authentication
     * Will be validated against encrypted password in database
     * Never logged or stored in plain text
     */
    @NotBlank(message = "Password is required")
    private String password;

    // Getters and Setters
    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}