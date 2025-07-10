package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import com.example.demo.token.JwtTokenProvider;
import com.example.demo.user.User;
import com.example.demo.user.AccountStatus;
import com.example.demo.user.UserRole;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.dto.UpdateProfileDto;
import com.example.demo.dto.ChangePasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // Register new user
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto dto) {
        // Check if username taken
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        // Check if email taken
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create user
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(AccountStatus.PENDING_VERIFICATION);
        user.setVerificationToken(UUID.randomUUID().toString());

        User savedUser = userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationToken());

        return convertToDto(savedUser);
    }

    // Login user
    @Transactional
    public String authenticateUser(String usernameOrEmail, String password) {
        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if account locked
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account is locked. Try again later.");
        }

        // Check if account active
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            handleFailedLogin(user);
            throw new RuntimeException("Invalid credentials");
        }

        // Reset failed attempts and update last login
        user.setFailedLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token
        return tokenProvider.generateToken(user);
    }

    // Verify email with token
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setStatus(AccountStatus.ACTIVE);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    // Start password reset
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create reset token
        user.setResetToken(UUID.randomUUID().toString());
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // Send reset email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getResetToken());
    }

    // Reset password with token
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        // Check if token expired
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    // Update user profile - MODIFIED TO PREVENT USERNAME CHANGES
    @Transactional
    public UserResponseDto updateProfile(String currentUsername, UpdateProfileDto dto) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newEmail = dto.getEmail();

        // Only update email, not username
        // Check if new email taken by someone else
        if (!newEmail.equals(user.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(newEmail);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                throw new RuntimeException("Email already exists");
            }

            // Update email
            user.setEmail(newEmail);

            // Send verification if email changed
            user.setEmailVerified(false);
            user.setVerificationToken(UUID.randomUUID().toString());
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
        }

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    // Change password
    @Transactional
    public void changePassword(String username, ChangePasswordDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Check passwords match
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords don't match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    // Handle failed login attempts
    private void handleFailedLogin(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        // Lock after 5 attempts
        if (user.getFailedLoginAttempts() >= 5) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        }

        userRepository.save(user);
    }

    // Get user by ID
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    // Get user by username
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    // Validate user for login
    public boolean validateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        return passwordEncoder.matches(password, user.getPassword()) &&
                user.getStatus() == AccountStatus.ACTIVE;
    }

    // Simple register (username is email)
    public boolean registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            return false;
        }

        if (userRepository.existsByEmail(username)) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(username); // Username is email
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(true);
        user.setRole(UserRole.USER);
        userRepository.save(user);
        return true;
    }

    // Full register with separate username and email
    public boolean registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            return false;
        }
        if (userRepository.existsByEmail(email)) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(true);
        user.setRole(UserRole.USER);
        userRepository.save(user);
        return true;
    }

    // Check if user is admin
    public boolean isAdmin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPER_ADMIN;
    }

    // Convert User to DTO (hide sensitive data) - UPDATED WITH DATE FORMATTING
    private UserResponseDto convertToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());

        // Format the creation date
        if (user.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy @ h:mm a 'EST'");
            dto.setFormattedCreatedAt(formatter.format(user.getCreatedAt()));
        }

        return dto;
    }
}