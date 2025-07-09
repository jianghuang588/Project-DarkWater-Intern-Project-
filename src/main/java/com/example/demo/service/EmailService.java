package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Send email to verify account
    public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your email");
        message.setText("Click the link to verify your email: " +
                "http://localhost:8080/api/auth/verify?token=" + token);
        mailSender.send(message);
    }

    // Send email to reset password
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText("Click the link to reset your password: " +
                "http://localhost:8080/reset-password?token=" + token +
                "\n\nThis link will expire in 1 hour.");
        mailSender.send(message);
    }
}