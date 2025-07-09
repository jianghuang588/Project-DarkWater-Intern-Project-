package com.example.demo.Enum;

/**
 * Enum representing the lifecycle states of a support ticket.
 * Tracks the current status from creation to closure.
 */
public enum TicketStatus {
    /**
     * Initial state when a ticket is created.
     * Indicates the issue hasn't been addressed yet.
     */
    OPEN,

    /**
     * Ticket is actively being worked on by support staff.
     * Someone is currently handling the issue.
     */
    IN_PROGRESS,

    /**
     * Support is waiting for additional information or response from the customer.
     * The ball is in the customer's court.
     */
    WAITING_FOR_CUSTOMER,

    /**
     * The issue has been fixed or answered.
     * Solution provided but ticket remains accessible.
     */
    RESOLVED,

    /**
     * Final state - ticket is archived.
     * No further action needed or possible.
     */
    CLOSED
}