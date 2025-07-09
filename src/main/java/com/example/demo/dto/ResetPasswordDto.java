package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for completing password reset
 * Used after user clicks reset link from email (second step of forgot password flow)
 * No authentication required since user proves identity via token
 */
public class ResetPasswordDto {

    /**
     * Password reset token from email link
     * Generated when user requests password reset
     * Validates user identity without requiring old password
     * Should expire after certain time (e.g., 24 hours) for security
     * Example: "a7b9c3d5-e2f4-4a6b-8c9e-1d3f5a7b9c3d"
     */
    @NotBlank(message = "Token is required")
    private String token;

    /**
     * New password user wants to set
     * No need for current password since user forgot it
     * Minimum 8 characters for security
     * Consider adding more complexity requirements
     */
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}