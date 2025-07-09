package com.example.demo.controller;

import com.example.demo.dto.SupportTicketDto;
import com.example.demo.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for support ticket system
 * Handles customer support requests and ticket management
 * Requires authentication for all endpoints
 */
@RestController
@RequestMapping("/api/support")
public class SupportController {

    @Autowired
    private SupportService supportService;

    /**
     * Create a new support ticket
     * @param authentication - Spring Security auth object to get current user
     * @param dto - ticket details (subject, description, category, priority)
     * @return Created ticket with assigned ID and timestamp
     */
    @PostMapping("/tickets")
    public ResponseEntity<SupportTicketDto> createTicket(
            Authentication authentication,
            @RequestBody SupportTicketDto dto) {
        // Get username from authenticated user
        String username = authentication.getName();
        return ResponseEntity.ok(supportService.createTicket(username, dto));
    }

    /**
     * Get all tickets created by the current user
     * @param authentication - to identify the user
     * @param pageable - pagination parameters (page, size, sort)
     * @return Page of user's tickets
     */
    @GetMapping("/tickets")
    public ResponseEntity<Page<SupportTicketDto>> getUserTickets(
            Authentication authentication,
            Pageable pageable) {
        String username = authentication.getName();
        return ResponseEntity.ok(supportService.getUserTickets(username, pageable));
    }

    /**
     * Get a specific ticket by ID
     * Only returns ticket if user owns it (security check in service)
     * @param authentication - to verify ownership
     * @param id - ticket ID
     * @return Ticket details if user has access
     */
    @GetMapping("/tickets/{id}")
    public ResponseEntity<SupportTicketDto> getTicket(
            Authentication authentication,
            @PathVariable Long id) {
        String username = authentication.getName();
        return ResponseEntity.ok(supportService.getTicket(id, username));
    }

    /**
     * Update ticket status/details
     * Typically used by support staff to update status, add notes, etc.
     * @param id - ticket ID to update
     * @param dto - updated ticket information
     * @return Updated ticket
     */
    @PutMapping("/tickets/{id}")
    public ResponseEntity<SupportTicketDto> updateTicket(
            @PathVariable Long id,
            @RequestBody SupportTicketDto dto) {
        // Note: No authentication check - verify in service if only staff can update
        return ResponseEntity.ok(supportService.updateTicket(id, dto));
    }

    /**
     * Get all tickets in the system
     * Admin/Support staff endpoint to view all customer tickets
     * Should be protected with role-based security
     * @param pageable - pagination parameters
     * @return Page of all tickets
     */
    @GetMapping("/tickets/all")
    public ResponseEntity<Page<SupportTicketDto>> getAllTickets(Pageable pageable) {
        // TODO: Add @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
        return ResponseEntity.ok(supportService.getAllTickets(pageable));
    }
}