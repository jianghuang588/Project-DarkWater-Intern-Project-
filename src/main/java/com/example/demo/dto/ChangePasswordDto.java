package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for password change requests
 * Used when authenticated users want to update their password
 * Includes validation to ensure password security requirements
 */
public class ChangePasswordDto {

    /**
     * User's current password for verification
     * Required to prevent unauthorized password changes
     * Service layer will validate this matches the stored password
     */
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    /**
     * The new password user wants to set
     * Must be at least 8 characters for security
     * Consider adding more validation like uppercase, numbers, special chars
     */
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

    /**
     * Confirmation of new password to prevent typos
     * Controller/Service should verify this matches newPassword
     * Helps prevent users from accidentally setting wrong password
     */
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    // Getters and Setters
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}