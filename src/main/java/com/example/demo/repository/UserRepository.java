package com.example.demo.repository;

import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by username
    Optional<User> findByUsername(String username);

    // Find user by email
    Optional<User> findByEmail(String email);

    // Find user by username OR email (for login)
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Check if username already exists
    boolean existsByUsername(String username);

    // Check if email already exists
    boolean existsByEmail(String email);

    // Find user by email verification token
    Optional<User> findByVerificationToken(String token);

    // Find user by password reset token
    Optional<User> findByResetToken(String token);

    // Get all admin users
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' OR u.role = 'SUPER_ADMIN'")
    List<User> findAllAdmins();
}