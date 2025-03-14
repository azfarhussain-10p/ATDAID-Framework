package com.tenpearls.config;

import com.tenpearls.model.Role;
import com.tenpearls.model.User;
import com.tenpearls.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@TestConfiguration
@DependsOn("databasePopulator")
public class TestDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(TestDataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeTestData() {
        log.info("Starting test data initialization...");
        try {
            createUserIfNotExists("admin@example.com", "admin123", Role.ROLE_ADMIN);
            createUserIfNotExists("user@example.com", "user123", Role.ROLE_USER);
            log.info("Test data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Error during test data initialization", e);
            throw e;
        }
    }

    private void createUserIfNotExists(String email, String password, Role role) {
        log.info("Checking if user exists: {}", email);
        
        if (!userRepository.existsByEmail(email)) {
            log.info("Creating new user with email: {} and role: {}", email, role);
            User user = new User(
                email.split("@")[0], // firstName
                "Test", // lastName
                email,
                passwordEncoder.encode(password),
                role
            );
            userRepository.save(user);
            log.info("User created successfully: {}", email);
        } else {
            log.info("User already exists: {}", email);
            User existingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            log.info("Existing user details - Email: {}, Role: {}", existingUser.getEmail(), existingUser.getRole());
        }
    }

    @Bean
    public String testDataInitialized() {
        log.info("Test data initialization bean created");
        return "Test data initialized";
    }
}