package com.learningSpringBoot.E_Commerce.Spring.Boot.application.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String verifyUrl = "http://localhost:8080/auth/verify-email?token=" + token;

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Verify your email");
        mail.setText("Click the link to verify your email: " + verifyUrl);

        mailSender.send(mail);
    }
}