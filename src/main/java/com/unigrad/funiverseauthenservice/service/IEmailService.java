package com.unigrad.funiverseauthenservice.service;

import com.unigrad.funiverseauthenservice.entity.Token;
import com.unigrad.funiverseauthenservice.service.impl.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface IEmailService {

    void send(EmailService.EmailServer server, String from, String to, String subject, String text);

    void sendEmailResetPassword(Token token) throws MessagingException, UnsupportedEncodingException;
}