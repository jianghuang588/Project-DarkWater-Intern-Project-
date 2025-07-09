package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for profile updates
 * Used when users want to change their username or email
 * Both fields are optional - users can update one or both
 */
public class UpdateProfileDto {

    /**
     * New username if user wants to change it
     * Optional field - can be null if only updating email
     * Must be 3-20 characters to match registration requirements
     * Service should check uniqueness before saving
     */
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * New email address if user wants to change it
     * Optional field - can be null if only updating username
     * Must be valid email format
     * Service should verify uniqueness and may require email verification
     */
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Default constructor for JSON deserialization
     */
    public UpdateProfileDto() {}

    /**
     * Convenience constructor for creating DTO with both fields
     * @param username - new username
     * @param email - new email address
     */
    public UpdateProfileDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}