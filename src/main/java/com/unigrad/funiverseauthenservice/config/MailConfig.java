package com.unigrad.funiverseauthenservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.mail.notification")
    public JavaMailSender notificationSender() {
        return javaMailSenderWithProperties(new JavaMailSenderImpl());
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.mail.services")
    public JavaMailSender servicesSender() {
        return javaMailSenderWithProperties(new JavaMailSenderImpl());
    }

    private JavaMailSender javaMailSenderWithProperties(JavaMailSenderImpl javaMailSender) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        javaMailSender.setJavaMailProperties(props);
        return javaMailSender;
    }
}