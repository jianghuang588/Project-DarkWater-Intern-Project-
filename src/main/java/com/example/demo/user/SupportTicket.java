package com.example.demo.user;

import com.example.demo.Enum.TicketPriority;
import com.example.demo.Enum.TicketStatus;
import com.example.demo.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who created the ticket
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Ticket title
    @Column(nullable = false)
    private String subject;

    // Full problem description
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // Current ticket status (defaults to OPEN)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    // How urgent (defaults to MEDIUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority = TicketPriority.MEDIUM;

    // Type of issue (billing, technical, etc)
    @Column(name = "category")
    private String category;

    // Support staff handling this ticket
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    // When ticket was created
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Last update time
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // When ticket was resolved
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // Auto-set creation time
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // Auto-update modification time
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}