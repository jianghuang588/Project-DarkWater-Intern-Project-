package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordDto;
import com.example.demo.dto.UpdateProfileDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user account management
 * Handles user profile operations for authenticated users
 * All endpoints require valid authentication token
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user's profile information
     * @param authentication - Spring Security auth to identify current user
     * @return User details (username, email, role, created date, etc.)
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        // Extract username from JWT token/security context
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    /**
     * Update current user's profile
     * @param authentication - to identify which user is updating
     * @param dto - new profile data (username, email, etc.) with validation
     * @return Updated user information
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDto> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileDto dto) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateProfile(username, dto));
    }

    /**
     * Change current user's password
     * @param authentication - to identify user changing password
     * @param dto - contains currentPassword, newPassword, confirmPassword
     * @return Success message on password change
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordDto dto) {
        String username = authentication.getName();
        // Service will verify current password before changing
        userService.changePassword(username, dto);
        return ResponseEntity.ok("Password changed successfully");
    }
}