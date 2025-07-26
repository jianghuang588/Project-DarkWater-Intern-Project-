package com.example.demo.Enum;


/**
 * Status of posts for scheduling and publication workflow
 * Allows PR team to draft posts and schedule them for release days
 */
public enum PostStatus {
    DRAFT,          // Not published, work in progress
    SCHEDULED,      // Scheduled for future publication
    PUBLISHED,      // Currently live and visible
    ARCHIVED        // No longer active but kept for history
}