package com.example.demo.dto;

import com.example.demo.user.UserRole;
import com.example.demo.user.AccountStatus;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for user information responses
 * Used to send user data back to clients without exposing sensitive info
 * Never includes password, security tokens, or internal fields
 */
public class UserResponseDto {
    /**
     * Unique user identifier from database
     * Used for API operations requiring user ID
     */
    private Long id;

    /**
     * User's display name
     * Public identifier shown throughout the application
     */
    private String username;

    /**
     * User's email address
     * May be partially masked for privacy in some contexts
     */
    private String email;

    /**
     * User's role in the system (USER, ADMIN, MODERATOR, etc.)
     * Determines access permissions and available features
     */
    private UserRole role;

    /**
     * Current account status (ACTIVE, SUSPENDED, LOCKED, etc.)
     * ACTIVE - normal access
     * SUSPENDED - admin action required
     * LOCKED - too many failed login attempts
     */
    private AccountStatus status;

    /**
     * Whether user has verified their email address
     * May limit features until verified
     */
    private boolean emailVerified;

    /**
     * Account creation timestamp
     * Useful for sorting users, showing account age
     */
    private LocalDateTime createdAt;

    /**
     * Most recent successful login time
     * Helps track user activity and detect unusual access patterns
     * Null if user has never logged in
     */
    private LocalDateTime lastLogin;

    /**
     * Formatted creation date for display
     * Example: "July 9, 2025 @ 5:47 PM EST"
     */
    private String formattedCreatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public String getFormattedCreatedAt() { return formattedCreatedAt; }
    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }
}