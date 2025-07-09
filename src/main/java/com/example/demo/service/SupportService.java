package com.example.demo.service;

import com.example.demo.Enum.TicketStatus;
import com.example.demo.dto.SupportTicketDto;
import com.example.demo.repository.SupportTicketRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.user.SupportTicket;
import com.example.demo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class SupportService {

    @Autowired
    private SupportTicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    // Create new support ticket
    @Transactional
    public SupportTicketDto createTicket(String username, SupportTicketDto dto) {
        // Find the user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create ticket
        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);
        ticket.setSubject(dto.getSubject());
        ticket.setDescription(dto.getDescription());
        ticket.setCategory(dto.getCategory());
        if (dto.getPriority() != null) {
            ticket.setPriority(dto.getPriority());
        }

        // Save and return
        SupportTicket savedTicket = ticketRepository.save(ticket);
        return convertToDto(savedTicket);
    }

    // Get all tickets for a user
    public Page<SupportTicketDto> getUserTickets(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ticketRepository.findByUser(user, pageable).map(this::convertToDto);
    }

    // Get single ticket (only if it belongs to the user)
    public SupportTicketDto getTicket(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicket ticket = ticketRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return convertToDto(ticket);
    }

    // Update ticket details
    @Transactional
    public SupportTicketDto updateTicket(Long id, SupportTicketDto dto) {
        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Update status
        if (dto.getStatus() != null) {
            ticket.setStatus(dto.getStatus());
            // Mark resolved time if closing
            if (dto.getStatus() == TicketStatus.RESOLVED || dto.getStatus() == TicketStatus.CLOSED) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }

        // Update priority
        if (dto.getPriority() != null) {
            ticket.setPriority(dto.getPriority());
        }

        // Update category
        if (dto.getCategory() != null) {
            ticket.setCategory(dto.getCategory());
        }

        // Assign to someone
        if (dto.getAssignedToUsername() != null) {
            User assignedTo = userRepository.findByUsername(dto.getAssignedToUsername())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            ticket.setAssignedTo(assignedTo);
        }

        SupportTicket updatedTicket = ticketRepository.save(ticket);
        return convertToDto(updatedTicket);
    }

    // Get all tickets (for support staff)
    public Page<SupportTicketDto> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(this::convertToDto);
    }

    // Get tickets by status
    public Page<SupportTicketDto> getTicketsByStatus(TicketStatus status, Pageable pageable) {
        return ticketRepository.findByStatus(status, pageable).map(this::convertToDto);
    }

    // Assign ticket to support staff
    @Transactional
    public void assignTicket(Long ticketId, String assignToUsername) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User assignedTo = userRepository.findByUsername(assignToUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ticket.setAssignedTo(assignedTo);
        ticket.setStatus(TicketStatus.IN_PROGRESS);  // Auto-update to in progress
        ticketRepository.save(ticket);
    }

    // Convert ticket to DTO (hide sensitive data)
    private SupportTicketDto convertToDto(SupportTicket ticket) {
        SupportTicketDto dto = new SupportTicketDto();
        dto.setId(ticket.getId());
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        dto.setCategory(ticket.getCategory());
        dto.setUsername(ticket.getUser().getUsername());
        if (ticket.getAssignedTo() != null) {
            dto.setAssignedToUsername(ticket.getAssignedTo().getUsername());
        }
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setResolvedAt(ticket.getResolvedAt());
        return dto;
    }
}