package com.tenpearls.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Configuration class for logging-related components.
 * Enables scheduling for log rotation, analysis, and monitoring tasks.
 * Enables async execution for performance optimization.
 */
@Configuration
@EnableScheduling
@EnableAsync
public class LoggingConfig {

    @Value("${logging.performance.buffer-size:1000}")
    private int bufferSize;
    
    @Value("${logging.performance.flush-interval:100}")
    private int flushInterval;
    
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
    
    /**
     * Configures an async executor for logging operations.
     * This improves performance by offloading logging to background threads.
     * 
     * @return Executor instance
     */
    @Bean("loggingTaskExecutor")
    @ConditionalOnProperty(name = "logging.performance.async-enabled", havingValue = "true", matchIfMissing = true)
    public Executor loggingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(bufferSize);
        executor.setThreadNamePrefix("logging-");
        executor.initialize();
        return executor;
    }
} 