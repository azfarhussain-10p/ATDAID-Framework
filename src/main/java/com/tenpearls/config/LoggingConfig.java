package com.tenpearls.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for logging-related components.
 * Enables scheduling for log rotation, analysis, and monitoring tasks.
 */
@Configuration
@EnableScheduling
public class LoggingConfig {

    /**
     * Configures a JavaMailSender for email alerts.
     * This is conditionally created based on application properties.
     * 
     * @return JavaMailSender instance
     */
    @Bean
    @ConditionalOnProperty(name = "logging.monitor.email.enabled", havingValue = "true", matchIfMissing = false)
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // Default configuration - should be overridden in application.properties
        mailSender.setHost("smtp.example.com");
        mailSender.setPort(587);
        mailSender.setUsername("username");
        mailSender.setPassword("password");
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        
        return mailSender;
    }
} 