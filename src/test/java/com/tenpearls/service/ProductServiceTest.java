package com.tenpearls.service;

import com.tenpearls.base.BaseJUnitTest;
import com.tenpearls.dto.ProductRequest;
import com.tenpearls.dto.ProductResponse;
import com.tenpearls.model.Product;
import com.tenpearls.repository.ProductRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for ProductService.
 * Extends BaseJUnitTest to integrate with Log4j2 and ExtentReports.
 */
public class ProductServiceTest extends BaseJUnitTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        logger.info("Setting up ProductServiceTest");
        closeable = MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository);
        
        // Log setup completion
        logger.debug("ProductServiceTest setup completed");
    }

    @AfterEach
    public void tearDown() throws Exception {
        logger.info("Tearing down ProductServiceTest");
        closeable.close();
        
        // Log teardown completion
        logger.debug("ProductServiceTest teardown completed");
    }

    @Test
    public void testCreateProduct_Success() {
        logger.info("Testing createProduct_Success");
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-001")
                .imageUrl("https://example.com/image.jpg")
                .active(true)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-001")
                .imageUrl("https://example.com/image.jpg")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.existsBySku("TEST-SKU-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponse response = productService.createProduct(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Test Product");
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(response.getStockQuantity()).isEqualTo(100);
        assertThat(response.getSku()).isEqualTo("TEST-SKU-001");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(response.isActive()).isTrue();

        verify(productRepository).existsBySku("TEST-SKU-001");
        verify(productRepository).save(any(Product.class));
        
        // Log test completion
        logger.debug("createProduct_Success test completed");
    }

    @Test
    public void testCreateProduct_DuplicateSku() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("EXISTING-SKU")
                .active(true)
                .build();

        when(productRepository.existsBySku("EXISTING-SKU")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Product with SKU EXISTING-SKU already exists");

        verify(productRepository).existsBySku("EXISTING-SKU");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void testGetProductById_Success() {
        // Given
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-001")
                .imageUrl("https://example.com/image.jpg")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        ProductResponse response = productService.getProductById(productId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response.getName()).isEqualTo("Test Product");
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(response.getStockQuantity()).isEqualTo(100);
        assertThat(response.getSku()).isEqualTo("TEST-SKU-001");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(response.isActive()).isTrue();

        verify(productRepository).findById(productId);
    }

    @Test
    public void testGetProductById_NotFound() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");

        verify(productRepository).findById(productId);
    }

    @Test
    public void testUpdateProduct_Success() {
        // Given
        Long productId = 1L;
        ProductRequest request = ProductRequest.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("149.99"))
                .stockQuantity(200)
                .sku("TEST-SKU-001")
                .imageUrl("https://example.com/updated.jpg")
                .active(true)
                .build();

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-001")
                .imageUrl("https://example.com/image.jpg")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("149.99"))
                .stockQuantity(200)
                .sku("TEST-SKU-001")
                .imageUrl("https://example.com/updated.jpg")
                .active(true)
                .createdAt(existingProduct.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When
        ProductResponse response = productService.updateProduct(productId, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response.getName()).isEqualTo("Updated Product");
        assertThat(response.getDescription()).isEqualTo("Updated Description");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("149.99"));
        assertThat(response.getStockQuantity()).isEqualTo(200);
        assertThat(response.getSku()).isEqualTo("TEST-SKU-001");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/updated.jpg");
        assertThat(response.isActive()).isTrue();

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct_ChangeSku_AlreadyExists() {
        // Given
        Long productId = 1L;
        ProductRequest request = ProductRequest.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(new BigDecimal("149.99"))
                .stockQuantity(200)
                .sku("EXISTING-SKU") // Different SKU
                .active(true)
                .build();

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-001") // Original SKU
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySku("EXISTING-SKU")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> productService.updateProduct(productId, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Product with SKU EXISTING-SKU already exists");

        verify(productRepository).findById(productId);
        verify(productRepository).existsBySku("EXISTING-SKU");
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void testDeleteProduct_Success() {
        // Given
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    public void testDeleteProduct_NotFound() {
        // Given
        Long productId = 999L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");

        verify(productRepository).existsById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    public void testDeactivateProduct_Success() {
        // Given
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-001")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product deactivatedProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("TEST-SKU-001")
                .active(false)
                .createdAt(existingProduct.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(deactivatedProduct);

        // When
        ProductResponse response = productService.deactivateProduct(productId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response.isActive()).isFalse();

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testGetAllActiveProducts_Success() {
        // Given
        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description 1")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("SKU-001")
                .active(true)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .description("Description 2")
                .price(new BigDecimal("149.99"))
                .stockQuantity(200)
                .sku("SKU-002")
                .active(true)
                .build();

        when(productRepository.findByActiveTrue()).thenReturn(Arrays.asList(product1, product2));

        // When
        List<ProductResponse> responses = productService.getAllActiveProducts();

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getName()).isEqualTo("Product 1");
        assertThat(responses.get(1).getId()).isEqualTo(2L);
        assertThat(responses.get(1).getName()).isEqualTo("Product 2");

        verify(productRepository).findByActiveTrue();
    }

    @Test
    public void testSearchProductsByName_Success() {
        // Given
        String searchTerm = "test";
        Product product1 = Product.builder()
                .id(1L)
                .name("Test Product 1")
                .description("Description 1")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku("SKU-001")
                .active(true)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Test Product 2")
                .description("Description 2")
                .price(new BigDecimal("149.99"))
                .stockQuantity(200)
                .sku("SKU-002")
                .active(true)
                .build();

        when(productRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(Arrays.asList(product1, product2));

        // When
        List<ProductResponse> responses = productService.searchProductsByName(searchTerm);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getName()).isEqualTo("Test Product 1");
        assertThat(responses.get(1).getId()).isEqualTo(2L);
        assertThat(responses.get(1).getName()).isEqualTo("Test Product 2");

        verify(productRepository).findByNameContainingIgnoreCase(searchTerm);
    }

    @Test
    public void testSearchProductsByName_EmptyResults() {
        // Given
        String searchTerm = "nonexistent";
        when(productRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(List.of());

        // When
        List<ProductResponse> responses = productService.searchProductsByName(searchTerm);

        // Then
        assertThat(responses).isEmpty();
        verify(productRepository).findByNameContainingIgnoreCase(searchTerm);
    }

    @Test
    public void testCreateProduct_ZeroPriceAndQuantity() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Zero Price Product")
                .description("Product with zero price and quantity")
                .price(BigDecimal.ZERO)
                .stockQuantity(0)
                .sku("ZERO-PRICE-001")
                .active(true)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name("Zero Price Product")
                .description("Product with zero price and quantity")
                .price(BigDecimal.ZERO)
                .stockQuantity(0)
                .sku("ZERO-PRICE-001")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.existsBySku("ZERO-PRICE-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponse response = productService.createProduct(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getStockQuantity()).isEqualTo(0);

        verify(productRepository).existsBySku("ZERO-PRICE-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testCreateProduct_MaxLengthValues() {
        // Given
        String longName = "A".repeat(255); // Max length name
        String longDescription = "B".repeat(1000); // Max length description
        String longSku = "C".repeat(50); // Max length SKU

        ProductRequest request = ProductRequest.builder()
                .name(longName)
                .description(longDescription)
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku(longSku)
                .active(true)
                .build();

        Product savedProduct = Product.builder()
                .id(1L)
                .name(longName)
                .description(longDescription)
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .sku(longSku)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.existsBySku(longSku)).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponse response = productService.createProduct(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).hasSize(255);
        assertThat(response.getDescription()).hasSize(1000);
        assertThat(response.getSku()).hasSize(50);

        verify(productRepository).existsBySku(longSku);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testGetProductBySku_NotFound() {
        // Given
        String sku = "NONEXISTENT-SKU";
        when(productRepository.findBySku(sku)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> productService.getProductBySku(sku))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Product not found with SKU: " + sku);

        verify(productRepository).findBySku(sku);
    }

    @Test
    public void testGetAllActiveProducts_EmptyList() {
        // Given
        when(productRepository.findByActiveTrue()).thenReturn(List.of());

        // When
        List<ProductResponse> responses = productService.getAllActiveProducts();

        // Then
        assertThat(responses).isEmpty();
        verify(productRepository).findByActiveTrue();
    }
} 