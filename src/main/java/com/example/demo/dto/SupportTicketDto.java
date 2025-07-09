package com.example.demo.dto;

import com.example.demo.Enum.TicketPriority;
import com.example.demo.Enum.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for support tickets
 * Used for customer support system to track user issues and requests
 * Supports full lifecycle from creation to resolution
 */
public class SupportTicketDto {
    /**
     * Unique ticket identifier
     * Auto-generated for new tickets
     */
    private Long id;

    /**
     * Brief summary of the issue
     * Required field - helps support staff quickly understand the problem
     * Example: "Cannot reset password" or "Billing question"
     */
    @NotBlank(message = "Subject is required")
    private String subject;

    /**
     * Detailed description of the issue
     * User provides full context, steps to reproduce, error messages, etc.
     * Required to ensure support has enough info to help
     */
    @NotBlank(message = "Description is required")
    private String description;

    /**
     * Current ticket status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)
     * Tracks ticket lifecycle and helps prioritize support work
     */
    private TicketStatus status;

    /**
     * Urgency level (LOW, MEDIUM, HIGH, CRITICAL)
     * Helps support team prioritize which tickets to handle first
     */
    private TicketPriority priority;

    /**
     * Type of issue (e.g., "Technical", "Billing", "Account", "General")
     * Used for routing to appropriate support team
     */
    private String category;

    /**
     * Username of customer who created the ticket
     * Links ticket to specific user account
     */
    private String username;

    /**
     * Support staff member assigned to this ticket
     * Null if unassigned, contains staff username when assigned
     */
    private String assignedToUsername;

    /**
     * When ticket was submitted
     */
    private LocalDateTime createdAt;

    /**
     * Last modification timestamp
     * Updates when status changes, comments added, etc.
     */
    private LocalDateTime updatedAt;

    /**
     * When ticket was marked as resolved
     * Null while ticket is open, set when status changes to RESOLVED
     * Used for SLA tracking and reporting
     */
    private LocalDateTime resolvedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAssignedToUsername() { return assignedToUsername; }
    public void setAssignedToUsername(String assignedToUsername) { this.assignedToUsername = assignedToUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}