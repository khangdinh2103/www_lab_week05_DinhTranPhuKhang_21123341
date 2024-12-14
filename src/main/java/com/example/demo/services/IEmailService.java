package com.example.demo.services;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
}
