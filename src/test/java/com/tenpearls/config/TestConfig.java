package com.tenpearls.config;

import com.tenpearls.repository.UserRepository;
import com.tenpearls.security.JwtAuthenticationFilter;
import com.tenpearls.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;

@TestConfiguration
@EnableJpaRepositories(basePackages = "com.tenpearls.repository")
@EntityScan(basePackages = "com.tenpearls.model")
public class TestConfig {

    private static final Logger log = LoggerFactory.getLogger(TestConfig.class);

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.tenpearls.model");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        log.info("Creating userDetailsService bean");
        return username -> {
            log.info("Loading user details for username: {}", username);
            return userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        log.error("User not found: {}", username);
                        return new UsernameNotFoundException("User not found");
                    });
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        log.info("Creating authenticationProvider bean");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("Creating authenticationManager bean");
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtService jwtService() {
        log.info("Creating jwtService bean");
        String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        long jwtExpiration = 86400000; // 24 hours
        return new JwtService(secretKey, jwtExpiration);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        log.info("Creating jwtAuthFilter bean");
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    public DatabasePopulator databasePopulator(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));
        populator.addScript(new ClassPathResource("data.sql"));
        populator.setSeparator(ScriptUtils.DEFAULT_STATEMENT_SEPARATOR);
        populator.setIgnoreFailedDrops(true);
        try {
            populator.populate(dataSource.getConnection());
        } catch (Exception e) {
            throw new RuntimeException("Failed to populate database", e);
        }
        return populator;
    }
}