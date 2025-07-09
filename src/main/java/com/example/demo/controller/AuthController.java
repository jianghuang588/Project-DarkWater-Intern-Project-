package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.UserService;
import com.example.demo.token.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication and authorization
 * Handles user registration, login, email verification, and password management
 * Public endpoints - no authentication required
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // TODO: Configure properly for production with specific origins
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Register a new user account
     * @param dto - contains username, email, password, confirmPassword
     * @return UserResponseDto with new user details or error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
        try {
            // Validate passwords match before processing
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords don't match");
            }
            UserResponseDto user = userService.registerUser(dto);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Authenticate user and generate JWT token
     * @param dto - contains username/email and password
     * @return JWT token with Bearer type on success
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto dto) {
        try {
            // Generate JWT token after successful authentication
            String token = userService.authenticateUser(dto.getUsernameOrEmail(), dto.getPassword());
            AuthResponseDto response = new AuthResponseDto(token, "Bearer");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Verify user email address using verification token
     * @param token - email verification token sent to user's email
     * @return Success message or error
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Initiate password reset process
     * @param dto - contains user's email address
     * @return Confirmation that reset email was sent
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto dto) {
        try {
            // Sends password reset link to user's email
            userService.initiatePasswordReset(dto.getEmail());
            return ResponseEntity.ok("Password reset email sent");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Complete password reset with token and new password
     * @param dto - contains reset token and new password
     * @return Success message or error
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto dto) {
        try {
            userService.resetPassword(dto.getToken(), dto.getNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Validate JWT token and return user info if valid
     * Used by frontend to check if user is still authenticated
     * @param token - JWT token with "Bearer " prefix
     * @return User details if token is valid, 401 if invalid
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // Extract JWT token (remove "Bearer " prefix)
            String jwtToken = token.substring(7);
            boolean isValid = tokenProvider.validateToken(jwtToken);
            if (isValid) {
                // Get user info from valid token
                String username = tokenProvider.getUsernameFromToken(jwtToken);
                UserResponseDto user = userService.getUserByUsername(username);
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}