package com.example.demo.user;

// User account states
public enum AccountStatus {
    ACTIVE,                 // Can login and use the system
    DISABLED,              // Temporarily disabled by admin
    BANNED,                // Permanently banned
    PENDING_VERIFICATION   // Waiting for email verification
}