package com.unigrad.funiverseauthenservice.service;

import com.unigrad.funiverseauthenservice.entity.Token;
import com.unigrad.funiverseauthenservice.entity.User;
import com.unigrad.funiverseauthenservice.entity.Workspace;
import com.unigrad.funiverseauthenservice.service.impl.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
public interface IEmailService {

    void send(EmailService.EmailServer server, String from, String to, String subject, String text) throws MessagingException, UnsupportedEncodingException;

    void sendResetPasswordEmail(Token token) throws MessagingException, UnsupportedEncodingException;

    void sendWelcomeEmail(Workspace workspace, User user, String password) throws IOException, MessagingException;
}