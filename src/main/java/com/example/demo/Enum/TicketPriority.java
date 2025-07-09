package com.example.demo.Enum;

/**
 * Priority levels for support tickets
 * Used to determine response time SLAs and queue ordering
 * Support staff should handle tickets based on priority
 */
public enum TicketPriority {
    /**
     * LOW - General questions, feature requests, minor issues
     * Typical SLA: 48-72 hours response time
     * Example: "How do I change my profile picture?"
     */
    LOW,

    /**
     * MEDIUM - Standard issues affecting user experience
     * Typical SLA: 24-48 hours response time
     * Example: "Cannot update my email address"
     */
    MEDIUM,

    /**
     * HIGH - Significant problems blocking user functionality
     * Typical SLA: 4-24 hours response time
     * Example: "Cannot access my account"
     */
    HIGH,

    /**
     * URGENT - Critical issues requiring immediate attention
     * Typical SLA: 1-4 hours response time
     * Example: "Security breach" or "Payment processing failed"
     */
    URGENT
}