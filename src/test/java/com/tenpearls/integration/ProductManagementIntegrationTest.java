package com.tenpearls.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpearls.base.BaseJUnitTest;
import com.tenpearls.config.TestConfig;
import com.tenpearls.config.TestDataInitializer;
import com.tenpearls.config.TestSecurityConfig;
import com.tenpearls.dto.ProductRequest;
import com.tenpearls.model.Product;
import com.tenpearls.model.Role;
import com.tenpearls.model.User;
import com.tenpearls.repository.ProductRepository;
import com.tenpearls.repository.UserRepository;
import com.tenpearls.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for Product Management API.
 * Extends BaseJUnitTest to integrate with Log4j2 and ExtentReports.
 */
@SpringBootTest(
    properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.jpa.hibernate.ddl-auto=create-drop"
    }
)
@Import({TestConfig.class, TestDataInitializer.class, TestSecurityConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductManagementIntegrationTest extends BaseJUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String userToken;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() throws Exception {
        logger.info("Setting up ProductManagementIntegrationTest");
        System.out.println("Starting setUp method...");
        productRepository.deleteAll();
        System.out.println("Products deleted...");

        // Clean up any existing products
        productRepository.deleteAll();
        
        // Create test users directly
        adminUser = createOrUpdateUser("admin@example.com", "admin123", Role.ROLE_ADMIN);
        regularUser = createOrUpdateUser("user@example.com", "user123", Role.ROLE_USER);
        
        // Generate tokens directly using JwtService
        adminToken = jwtService.generateToken(adminUser);
        userToken = jwtService.generateToken(regularUser);
        
        System.out.println("Admin token: " + adminToken);
        System.out.println("User token: " + userToken);
        
        // Log setup completion
        logger.debug("ProductManagementIntegrationTest setup completed");
    }

    private User createOrUpdateUser(String email, String password, Role role) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            User newUser = new User(
                email.split("@")[0], // firstName
                "Test", // lastName
                email,
                passwordEncoder.encode(password),
                role
            );
            return userRepository.save(newUser);
        }
    }

    @AfterEach
    void tearDown() {
        logger.info("Tearing down ProductManagementIntegrationTest");
        productRepository.deleteAll();
        
        // Log teardown completion
        logger.debug("ProductManagementIntegrationTest teardown completed");
    }

    @Test
    void testCreateProduct_AsAdmin_Success() throws Exception {
        logger.info("Testing testCreateProduct_AsAdmin_Success");
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .sku("SKU123")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .active(true)
                .build();

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.sku").value(request.getSku()))
                .andExpect(jsonPath("$.price").value(request.getPrice()))
                .andExpect(jsonPath("$.active").value(request.isActive()));
        
        // Log test completion
        logger.debug("testCreateProduct_AsAdmin_Success test completed");
    }

    @Test
    void testCreateProduct_AsUser_Forbidden() throws Exception {
        logger.info("Testing testCreateProduct_AsUser_Forbidden");
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .sku("SKU123")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .active(true)
                .build();

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        
        // Log test completion
        logger.debug("testCreateProduct_AsUser_Forbidden test completed");
    }

    @Test
    void testGetAllActiveProducts_Success() throws Exception {
        logger.info("Testing testGetAllActiveProducts_Success");
        // Create a test product
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("SKU123");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(100);
        product.setActive(true);
        productRepository.save(product);

        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(product.getName()))
                .andExpect(jsonPath("$[0].sku").value(product.getSku()))
                .andExpect(jsonPath("$[0].price").value(product.getPrice()))
                .andExpect(jsonPath("$[0].active").value(product.isActive()));
        
        // Log test completion
        logger.debug("getTestAllActiveProducts_Success test completed");
    }

    @Test
    void testGetProductById_Success() throws Exception {
        logger.info("Testing testGetProductById_Success");
        // Create a test product
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("SKU123");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(100);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(get("/api/products/" + savedProduct.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.sku").value(product.getSku()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.active").value(product.isActive()));
        
        // Log test completion
        logger.debug("getTestProductById_Success test completed");
    }

    @Test
    void testUpdateProduct_AsAdmin_Success() throws Exception {
        logger.info("Testing testUpdateProduct_AsAdmin_Success");
        // Create a test product
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("SKU123");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(100);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);

        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Product")
                .sku("SKU456")
                .price(new BigDecimal("149.99"))
                .stockQuantity(100)
                .active(true)
                .build();

        mockMvc.perform(put("/api/products/" + savedProduct.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updateRequest.getName()))
                .andExpect(jsonPath("$.sku").value(updateRequest.getSku()))
                .andExpect(jsonPath("$.price").value(updateRequest.getPrice()))
                .andExpect(jsonPath("$.active").value(updateRequest.isActive()));
        
        // Log test completion
        logger.debug("testUpdateProduct_AsAdmin_Success test completed");
    }

    @Test
    void testUpdateProduct_AsUser_Forbidden() throws Exception {
        logger.info("Testing testUpdateProduct_AsUser_Forbidden");
        // Create a test product
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("SKU123");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(100);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);

        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Product")
                .sku("SKU456")
                .price(new BigDecimal("149.99"))
                .stockQuantity(100)
                .active(true)
                .build();

        mockMvc.perform(put("/api/products/" + savedProduct.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
        
        // Log test completion
        logger.debug("testUpdateProduct_AsUser_Forbidden test completed");
    }

    @Test
    void testDeactivateProduct_AsAdmin_Success() throws Exception {
        logger.info("Testing testDeactivateProduct_AsAdmin_Success");
        // Create a test product
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("SKU123");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(100);
        product.setActive(true);
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(delete("/api/products/" + savedProduct.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
        
        // Log test completion
        logger.debug("testDeactivateProduct_AsAdmin_Success test completed");
    }
} 