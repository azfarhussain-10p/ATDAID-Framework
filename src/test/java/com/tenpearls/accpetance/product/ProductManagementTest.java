package com.tenpearls.accpetance.product;

import com.tenpearls.accpetance.BaseAcceptanceTest;
import com.tenpearls.dto.ProductRequest;
import com.tenpearls.dto.ProductResponse;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class ProductManagementTest extends BaseAcceptanceTest {
    
    @Test
    public void testCreateProduct_Success() {
        // Arrange
        String token = getAdminToken();
        ProductRequest request = ProductRequest.builder()
                .name("Acceptance Test Product")
                .description("Product created in acceptance test")
                .price(new BigDecimal("149.99"))
                .stockQuantity(50)
                .sku("ACC-TEST-001")
                .imageUrl("https://example.com/acceptance-test.jpg")
                .active(true)
                .build();
        
        // Act
        ProductResponse response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/products")
                .then()
                .statusCode(201)
                .extract()
                .as(ProductResponse.class);
        
        // Assert
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("Acceptance Test Product");
        assertThat(response.getDescription()).isEqualTo("Product created in acceptance test");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("149.99"));
        assertThat(response.getStockQuantity()).isEqualTo(50);
        assertThat(response.getSku()).isEqualTo("ACC-TEST-001");
        assertThat(response.isActive()).isTrue();
    }
    
    @Test
    public void testGetProduct_Success() {
        // Arrange - Create a product first
        String token = getAdminToken();
        ProductRequest request = ProductRequest.builder()
                .name("Product to Retrieve")
                .description("This product will be retrieved")
                .price(new BigDecimal("99.99"))
                .stockQuantity(25)
                .sku("ACC-TEST-002")
                .active(true)
                .build();
        
        ProductResponse createdProduct = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/products")
                .then()
                .statusCode(201)
                .extract()
                .as(ProductResponse.class);
        
        // Act & Assert - Get the product
        given()
                .when()
                .get("/api/products/" + createdProduct.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(createdProduct.getId().intValue()))
                .body("name", equalTo("Product to Retrieve"))
                .body("description", equalTo("This product will be retrieved"))
                .body("price", equalTo(99.99f))
                .body("stockQuantity", equalTo(25))
                .body("sku", equalTo("ACC-TEST-002"))
                .body("active", equalTo(true));
    }
    
    @Test
    public void testUpdateProduct_Success() {
        // Arrange - Create a product first
        String token = getAdminToken();
        ProductRequest createRequest = ProductRequest.builder()
                .name("Original Product")
                .description("Original description")
                .price(new BigDecimal("199.99"))
                .stockQuantity(75)
                .sku("ACC-TEST-003")
                .active(true)
                .build();
        
        ProductResponse createdProduct = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(createRequest)
                .when()
                .post("/api/products")
                .then()
                .statusCode(201)
                .extract()
                .as(ProductResponse.class);
        
        // Create update request
        ProductRequest updateRequest = ProductRequest.builder()
                .name("Updated Product")
                .description("Updated description")
                .price(new BigDecimal("249.99"))
                .stockQuantity(100)
                .sku("ACC-TEST-003") // Same SKU
                .imageUrl("https://example.com/updated.jpg")
                .active(true)
                .build();
        
        // Act & Assert - Update the product
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(updateRequest)
                .when()
                .put("/api/products/" + createdProduct.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(createdProduct.getId().intValue()))
                .body("name", equalTo("Updated Product"))
                .body("description", equalTo("Updated description"))
                .body("price", equalTo(249.99f))
                .body("stockQuantity", equalTo(100))
                .body("sku", equalTo("ACC-TEST-003"))
                .body("imageUrl", equalTo("https://example.com/updated.jpg"))
                .body("active", equalTo(true));
    }
    
    @Test
    public void testDeleteProduct_Success() {
        // Arrange - Create a product first
        String token = getAdminToken();
        ProductRequest request = ProductRequest.builder()
                .name("Product to Delete")
                .description("This product will be deleted")
                .price(new BigDecimal("49.99"))
                .stockQuantity(30)
                .sku("ACC-TEST-004")
                .active(true)
                .build();
        
        ProductResponse createdProduct = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post("/api/products")
                .then()
                .statusCode(201)
                .extract()
                .as(ProductResponse.class);
        
        // Act - Delete the product
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/products/" + createdProduct.getId())
                .then()
                .statusCode(204);
        
        // Assert - Verify product is deleted
        given()
                .when()
                .get("/api/products/" + createdProduct.getId())
                .then()
                .statusCode(404);
    }
    
    @Test
    public void testSearchProducts_Success() {
        // Arrange - Create multiple products
        String token = getAdminToken();
        
        // Create iPhone product
        ProductRequest iPhoneRequest = ProductRequest.builder()
                .name("iPhone 13 Pro")
                .description("Apple iPhone 13 Pro")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .sku("ACC-IPHONE-13-PRO")
                .active(true)
                .build();
        
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(iPhoneRequest)
                .when()
                .post("/api/products")
                .then()
                .statusCode(201);
        
        // Create Samsung product
        ProductRequest samsungRequest = ProductRequest.builder()
                .name("Samsung Galaxy S21")
                .description("Samsung Galaxy S21")
                .price(new BigDecimal("899.99"))
                .stockQuantity(60)
                .sku("ACC-SAMSUNG-S21")
                .active(true)
                .build();
        
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(samsungRequest)
                .when()
                .post("/api/products")
                .then()
                .statusCode(201);
        
        // Act & Assert - Search for iPhone products
        given()
                .param("name", "iPhone")
                .when()
                .get("/api/products/search")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("find { it.name == 'iPhone 13 Pro' }", notNullValue())
                .body("find { it.name == 'iPhone 13 Pro' }.sku", equalTo("ACC-IPHONE-13-PRO"));
        
        // Act & Assert - Search for Samsung products
        given()
                .param("name", "Samsung")
                .when()
                .get("/api/products/search")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("find { it.name == 'Samsung Galaxy S21' }", notNullValue())
                .body("find { it.name == 'Samsung Galaxy S21' }.sku", equalTo("ACC-SAMSUNG-S21"));
    }
    
    private String getAdminToken() {
        // Implementation to get admin token
        // In a real implementation, this would authenticate as an admin user
        return "admin-token";
    }
} 