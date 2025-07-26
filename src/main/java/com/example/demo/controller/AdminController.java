package com.example.demo.controller;

import com.example.demo.dto.ActivityLogDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.service.AdminService;
import com.example.demo.user.AccountStatus;
import com.example.demo.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admin operations
 * Handles user management, activity monitoring, and administrative tasks
 * All endpoints require admin authorization (should be secured with @PreAuthorize)
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Get all users with pagination support
     * @param pageable - pagination parameters (page number, size, sort)
     * @return Page of users wrapped in ResponseEntity
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    /**
     * Get a specific user by their ID
     * @param id - user ID to look up
     * @return User details wrapped in ResponseEntity
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    /**
     * Update a user's account status (ACTIVE, SUSPENDED, LOCKED, etc.)
     * @param id - user ID to update
     * @param status - new account status
     * @return Updated user details
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserResponseDto> updateUserStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status) {
        return ResponseEntity.ok(adminService.updateUserStatus(id, status));
    }

    /**
     * Change a user's role (USER, ADMIN, MODERATOR, etc.)
     * @param id - user ID to update
     * @param role - new user role
     * @return Updated user details
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {
        return ResponseEntity.ok(adminService.updateUserRole(id, role));
    }

    /**
     * Permanently delete a user from the system
     * @param id - user ID to delete
     * @return 204 No Content on successful deletion
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search users by query string (searches username, email, name, etc.)
     * @param query - search term
     * @param pageable - pagination parameters
     * @return Page of matching users
     */
    @GetMapping("/users/search")
    public ResponseEntity<Page<UserResponseDto>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.ok(adminService.searchUsers(query, pageable));
    }

    /**
     * Get system activity logs (login attempts, user actions, etc.)
     * @param pageable - pagination parameters
     * @return Page of activity logs
     */
    @GetMapping("/activity-logs")
    public ResponseEntity<Page<ActivityLogDto>> getActivityLogs(Pageable pageable) {
        return ResponseEntity.ok(adminService.getActivityLogs(pageable));
    }

    /**
     * Unlock a user account (typically after too many failed login attempts)
     * @param id - user ID to unlock
     * @return Updated user details with unlocked status
     */
    @PostMapping("/users/{id}/unlock")
    public ResponseEntity<UserResponseDto> unlockUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.unlockUser(id));
    }




}