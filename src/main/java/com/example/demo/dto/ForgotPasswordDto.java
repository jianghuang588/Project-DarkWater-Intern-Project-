package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for forgot password requests
 * Used when users can't remember their password and need a reset link
 * Only requires email - no authentication needed since user can't log in
 */
public class ForgotPasswordDto {

    /**
     * Email address where password reset link will be sent
     * Must be a registered email in the system
     * Service will generate reset token and send email with link
     * Validated for proper email format to prevent errors
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}