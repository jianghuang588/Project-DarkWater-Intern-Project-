package com.example.demo;

import com.example.demo.repository.UserRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @PostConstruct
    public void initAdminUser() {
        try {
            User admin = userRepository.findByUsername("admin").orElse(null);
            if (admin != null && admin.getRole() == UserRole.USER) {
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                System.out.println("Updated admin user role to ADMIN");
            }
        } catch (Exception e) {
            System.out.println("Could not update admin role: " + e.getMessage());
        }
    }
}