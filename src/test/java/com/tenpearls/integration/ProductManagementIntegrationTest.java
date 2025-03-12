package com.tenpearls.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpearls.dto.AuthResponse;
import com.tenpearls.dto.LoginRequest;
import com.tenpearls.dto.ProductRequest;
import com.tenpearls.dto.ProductResponse;
import com.tenpearls.model.Product;
import com.tenpearls.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up product repository
        productRepository.deleteAll();
        
        // Get admin token
        LoginRequest adminLoginRequest = new LoginRequest("admin@example.com", "Admin123!");
        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        AuthResponse adminResponse = objectMapper.readValue(
                adminResult.getResponse().getContentAsString(), AuthResponse.class);
        adminToken = adminResponse.getToken();
        
        // Get regular user token
        LoginRequest userLoginRequest = new LoginRequest("user@example.com", "User123!");
        MvcResult userResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        AuthResponse userResponse = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), AuthResponse.class);
        userToken = userResponse.getToken();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void testCreateProduct_AsAdmin_Success() throws Exception {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("This is a test product")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-001")
                .imageUrl("https://example.com/image.jpg")
                .active(true)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("This is a test product"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.stockQuantity").value(100))
                .andExpect(jsonPath("$.sku").value("TEST-SKU-001"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        // Verify database state
        Optional<Product> productOpt = productRepository.findBySku("TEST-SKU-001");
        assertThat(productOpt).isPresent();
        Product product = productOpt.get();
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getDescription()).isEqualTo("This is a test product");
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(product.getStockQuantity()).isEqualTo(100);
    }

    @Test
    void testCreateProduct_AsUser_Forbidden() throws Exception {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("This is a test product")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-002")
                .imageUrl("https://example.com/image.jpg")
                .active(true)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // Verify database state
        assertThat(productRepository.findBySku("TEST-SKU-002")).isEmpty();
    }

    @Test
    void testGetProductById_Success() throws Exception {
        // Arrange - Create a product first
        ProductRequest request = ProductRequest.builder()
                .name("Get By ID Test")
                .description("Product for get by ID test")
                .price(new BigDecimal("129.99"))
                .stockQuantity(50)
                .sku("TEST-SKU-003")
                .imageUrl("https://example.com/image2.jpg")
                .active(true)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponse createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ProductResponse.class);
        Long productId = createdProduct.getId();

        // Act & Assert - Get the product by ID
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Get By ID Test"))
                .andExpect(jsonPath("$.description").value("Product for get by ID test"))
                .andExpect(jsonPath("$.price").value(129.99))
                .andExpect(jsonPath("$.stockQuantity").value(50))
                .andExpect(jsonPath("$.sku").value("TEST-SKU-003"));
    }

    @Test
    void testUpdateProduct_AsAdmin_Success() throws Exception {
        // Arrange - Create a product first
        ProductRequest createRequest = ProductRequest.builder()
                .name("Original Product")
                .description("Original description")
                .price(new BigDecimal("199.99"))
                .stockQuantity(75)
                .sku("TEST-SKU-004")
                .imageUrl("https://example.com/original.jpg")
                .active(true)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponse createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ProductResponse.class);
        Long productId = createdProduct.getId();

        // Create update request
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Product")
                .description("Updated description")
                .price(new BigDecimal("249.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-004") // Same SKU
                .imageUrl("https://example.com/updated.jpg")
                .active(true)
                .build();

        // Act & Assert - Update the product
        mockMvc.perform(put("/api/products/" + productId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.price").value(249.99))
                .andExpect(jsonPath("$.stockQuantity").value(100))
                .andExpect(jsonPath("$.sku").value("TEST-SKU-004"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/updated.jpg"));

        // Verify database state
        Optional<Product> productOpt = productRepository.findById(productId);
        assertThat(productOpt).isPresent();
        Product product = productOpt.get();
        assertThat(product.getName()).isEqualTo("Updated Product");
        assertThat(product.getDescription()).isEqualTo("Updated description");
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("249.99"));
        assertThat(product.getStockQuantity()).isEqualTo(100);
    }

    @Test
    void testUpdateProduct_AsUser_Forbidden() throws Exception {
        // Arrange - Create a product first
        ProductRequest createRequest = ProductRequest.builder()
                .name("Original Product")
                .description("Original description")
                .price(new BigDecimal("199.99"))
                .stockQuantity(75)
                .sku("TEST-SKU-005")
                .imageUrl("https://example.com/original.jpg")
                .active(true)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponse createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ProductResponse.class);
        Long productId = createdProduct.getId();

        // Create update request
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated By User")
                .description("User updated description")
                .price(new BigDecimal("299.99"))
                .stockQuantity(200)
                .sku("TEST-SKU-005") // Same SKU
                .imageUrl("https://example.com/user-updated.jpg")
                .active(true)
                .build();

        // Act & Assert - Try to update as regular user
        mockMvc.perform(put("/api/products/" + productId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        // Verify database state - should remain unchanged
        Optional<Product> productOpt = productRepository.findById(productId);
        assertThat(productOpt).isPresent();
        Product product = productOpt.get();
        assertThat(product.getName()).isEqualTo("Original Product");
        assertThat(product.getDescription()).isEqualTo("Original description");
        assertThat(product.getPrice()).isEqualByComparingTo(new BigDecimal("199.99"));
        assertThat(product.getStockQuantity()).isEqualTo(75);
    }

    @Test
    void testDeleteProduct_AsAdmin_Success() throws Exception {
        // Arrange - Create a product first
        ProductRequest createRequest = ProductRequest.builder()
                .name("Product to Delete")
                .description("This product will be deleted")
                .price(new BigDecimal("49.99"))
                .stockQuantity(30)
                .sku("TEST-SKU-006")
                .imageUrl("https://example.com/delete.jpg")
                .active(true)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponse createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ProductResponse.class);
        Long productId = createdProduct.getId();

        // Verify product exists
        assertThat(productRepository.findById(productId)).isPresent();

        // Act & Assert - Delete the product
        mockMvc.perform(delete("/api/products/" + productId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify database state - product should be deleted
        assertThat(productRepository.findById(productId)).isEmpty();
    }

    @Test
    void testDeactivateProduct_AsAdmin_Success() throws Exception {
        // Arrange - Create a product first
        ProductRequest createRequest = ProductRequest.builder()
                .name("Product to Deactivate")
                .description("This product will be deactivated")
                .price(new BigDecimal("79.99"))
                .stockQuantity(40)
                .sku("TEST-SKU-007")
                .imageUrl("https://example.com/deactivate.jpg")
                .active(true)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponse createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), ProductResponse.class);
        Long productId = createdProduct.getId();

        // Verify product is active
        Optional<Product> productOpt = productRepository.findById(productId);
        assertThat(productOpt).isPresent();
        assertThat(productOpt.get().isActive()).isTrue();

        // Act & Assert - Deactivate the product
        mockMvc.perform(patch("/api/products/" + productId + "/deactivate")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Product to Deactivate"))
                .andExpect(jsonPath("$.active").value(false));

        // Verify database state - product should be deactivated
        productOpt = productRepository.findById(productId);
        assertThat(productOpt).isPresent();
        assertThat(productOpt.get().isActive()).isFalse();
    }

    @Test
    void testSearchProductsByName_Success() throws Exception {
        // Arrange - Create multiple products
        ProductRequest product1 = ProductRequest.builder()
                .name("iPhone 13 Pro")
                .description("Apple iPhone 13 Pro")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .sku("IPHONE-13-PRO")
                .active(true)
                .build();

        ProductRequest product2 = ProductRequest.builder()
                .name("iPhone 13")
                .description("Apple iPhone 13")
                .price(new BigDecimal("799.99"))
                .stockQuantity(75)
                .sku("IPHONE-13")
                .active(true)
                .build();

        ProductRequest product3 = ProductRequest.builder()
                .name("Samsung Galaxy S21")
                .description("Samsung Galaxy S21")
                .price(new BigDecimal("899.99"))
                .stockQuantity(60)
                .sku("SAMSUNG-S21")
                .active(true)
                .build();

        // Create products
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product3)))
                .andExpect(status().isCreated());

        // Act & Assert - Search for iPhone products
        mockMvc.perform(get("/api/products/search")
                .param("name", "iPhone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("iPhone 13 Pro"))
                .andExpect(jsonPath("$[1].name").value("iPhone 13"));

        // Act & Assert - Search for Samsung products
        mockMvc.perform(get("/api/products/search")
                .param("name", "Samsung"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Samsung Galaxy S21"));
    }

    @Test
    void testGetAllActiveProducts_Success() throws Exception {
        // Arrange - Create active and inactive products
        ProductRequest activeProduct = ProductRequest.builder()
                .name("Active Product")
                .description("This is an active product")
                .price(new BigDecimal("59.99"))
                .stockQuantity(25)
                .sku("ACTIVE-001")
                .active(true)
                .build();

        ProductRequest inactiveProduct = ProductRequest.builder()
                .name("Inactive Product")
                .description("This is an inactive product")
                .price(new BigDecimal("39.99"))
                .stockQuantity(15)
                .sku("INACTIVE-001")
                .active(false)
                .build();

        // Create products
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activeProduct)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inactiveProduct)))
                .andExpect(status().isCreated());

        // Act & Assert - Get all active products
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Active Product"))
                .andExpect(jsonPath("$[0].active").value(true));
    }
} 