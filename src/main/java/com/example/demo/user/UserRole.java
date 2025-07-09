package com.example.demo.user;

// Different permission levels
public enum UserRole {
    USER,           // Regular user
    SUPPORT_STAFF,  // Can handle support tickets
    MODERATOR,      // Can moderate content
    ADMIN,          // Can manage users and system
    SUPER_ADMIN     // Full system access
}