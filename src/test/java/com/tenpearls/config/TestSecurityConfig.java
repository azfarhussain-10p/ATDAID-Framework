package com.tenpearls.config;

import com.tenpearls.security.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class TestSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestSecurityConfig.class);

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring test security filter chain");

        http.csrf(csrf -> {
            csrf.disable();
            logger.info("CSRF disabled for testing");
        });

        http.authorizeHttpRequests(auth -> {
            logger.info("Configuring authorization rules");
            
            // Public endpoints
            auth.requestMatchers(
                new AntPathRequestMatcher("/api/auth/login"),
                new AntPathRequestMatcher("/api/auth/register"),
                new AntPathRequestMatcher("/h2-console/**")
            ).permitAll();
            
            // GET product endpoints are public
            auth.requestMatchers(
                new AntPathRequestMatcher("/api/products", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/products/{id}", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/products/sku/{sku}", HttpMethod.GET.name()),
                new AntPathRequestMatcher("/api/products/search", HttpMethod.GET.name())
            ).permitAll();
            
            // Admin-only endpoints
            auth.requestMatchers(
                new AntPathRequestMatcher("/api/products/**")
            ).hasRole("ADMIN");
            
            // All other requests need authentication
            auth.anyRequest().authenticated();
            
            logger.info("Security paths configured successfully");
        });

        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            logger.info("Session management configured as STATELESS");
        });

        http.authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        logger.info("Security filter chain configured successfully");
        return http.build();
    }
} 