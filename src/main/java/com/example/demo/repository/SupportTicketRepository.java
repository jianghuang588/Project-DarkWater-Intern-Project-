package com.example.demo.repository;

import com.example.demo.Enum.TicketStatus;
import com.example.demo.user.SupportTicket;
import com.example.demo.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    // Get all tickets for a specific user
    Page<SupportTicket> findByUser(User user, Pageable pageable);

    // Get all tickets with a specific status (like OPEN or CLOSED)
    Page<SupportTicket> findByStatus(TicketStatus status, Pageable pageable);

    // Get all tickets assigned to a specific support person
    Page<SupportTicket> findByAssignedTo(User assignedTo, Pageable pageable);

    // Get a specific ticket that belongs to a specific user
    Optional<SupportTicket> findByIdAndUser(Long id, User user);
}