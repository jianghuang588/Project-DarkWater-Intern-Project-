package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for new user registration
 * Contains all required fields for creating a new user account
 * All fields are mandatory with validation to ensure data quality
 */
public class UserRegistrationDto {

    /**
     * Unique username for the account
     * Required, 3-20 characters, alphanumeric + underscore allowed
     * Service must check this doesn't already exist
     * Used for login and display throughout the application
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * User's email address
     * Required for account recovery and notifications
     * Must be valid format and unique in system
     * May require verification before account activation
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Account password
     * Minimum 8 characters for security
     * Will be encrypted before storage
     * Consider adding complexity requirements (uppercase, numbers, symbols)
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * Password confirmation to prevent typos
     * Must match password field
     * Controller should validate match before processing
     * Not stored - only used for validation
     */
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    /**
     * Default constructor for JSON deserialization
     */
    public UserRegistrationDto() {}

    /**
     * Full constructor for programmatic creation
     * @param username - desired username
     * @param email - user's email
     * @param password - account password
     * @param confirmPassword - password confirmation
     */
    public UserRegistrationDto(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}