package com.example.demo.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for activity logs
 * Used to track and audit user actions in the system
 * Typically displayed in admin dashboards for security monitoring
 */
public class ActivityLogDto {
    /**
     * Unique identifier for the log entry
     */
    private Long id;

    /**
     * Username of the user who performed the action
     */
    private String username;

    /**
     * Type of action performed (e.g., "LOGIN", "LOGOUT", "PASSWORD_CHANGE", "PROFILE_UPDATE")
     */
    private String action;

    /**
     * Additional context about the action (e.g., "Failed login attempt", "Changed email to xxx@gmail.com")
     */
    private String details;

    /**
     * IP address from which the action was performed
     * Used for security tracking and suspicious activity detection
     */
    private String ipAddress;

    /**
     * When the action occurred
     */
    private LocalDateTime timestamp;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}