package com.tenpearls.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for REST-related beans.
 * Provides beans for HTTP client operations.
 */
@Configuration
public class RestConfig {
    
    /**
     * Creates a RestTemplate bean for making HTTP requests.
     * 
     * @return Configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}