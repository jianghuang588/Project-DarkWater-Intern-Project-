package com.example.demo.service;

import com.example.demo.dto.ActivityLogDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.user.AccountStatus;
import com.example.demo.user.User;
import com.example.demo.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    // Get all users with pagination
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDto);
    }

    // Get single user by ID
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    // Change user status (active, banned, suspended)
    @Transactional
    public UserResponseDto updateUserStatus(Long id, AccountStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        return convertToDto(userRepository.save(user));
    }

    // Change user role (user, admin, support)
    @Transactional
    public UserResponseDto updateUserRole(Long id, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        return convertToDto(userRepository.save(user));
    }

    // Delete user
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Search users by name or email
    public Page<UserResponseDto> searchUsers(String query, Pageable pageable) {
        // TODO: Add actual search logic
        return userRepository.findAll(pageable).map(this::convertToDto);
    }

    // Unlock user after too many failed login attempts
    @Transactional
    public UserResponseDto unlockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLockedUntil(null);           // Remove lock
        user.setFailedLoginAttempts(0);      // Reset counter
        return convertToDto(userRepository.save(user));
    }

    // Get activity logs (who did what and when)
    public Page<ActivityLogDto> getActivityLogs(Pageable pageable) {
        // TODO: Need ActivityLog table first
        return Page.empty();
    }

    // Convert User to UserResponseDto (hide sensitive data)
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
        return dto;
    }
}