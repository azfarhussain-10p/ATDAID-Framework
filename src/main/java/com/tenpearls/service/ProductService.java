package com.tenpearls.service;

import com.tenpearls.dto.ProductRequest;
import com.tenpearls.dto.ProductResponse;
import com.tenpearls.model.Product;
import com.tenpearls.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing product-related operations.
 * 
 * This service provides methods for creating, retrieving, updating, and deleting products,
 * as well as searching for products by various criteria. It handles the business logic
 * for product management and ensures data integrity through validation and transaction management.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Creates a new product based on the provided request.
     * 
     * @param request The product request containing all necessary product information
     * @return A ProductResponse object containing the details of the created product
     * @throws IllegalStateException If a product with the same SKU already exists
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        // Check if SKU already exists
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalStateException("Product with SKU " + request.getSku() + " already exists");
        }

        // Create new product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .imageUrl(request.getImageUrl())
                .active(request.isActive())
                .build();

        // Save product
        Product savedProduct = productRepository.save(product);

        // Return response
        return mapToProductResponse(savedProduct);
    }

    /**
     * Retrieves a product by its ID.
     * 
     * @param id The ID of the product to retrieve
     * @return A ProductResponse object containing the details of the found product
     * @throws EntityNotFoundException If no product is found with the given ID
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        return mapToProductResponse(product);
    }

    /**
     * Retrieves a product by its SKU.
     * 
     * @param sku The SKU of the product to retrieve
     * @return A ProductResponse object containing the details of the found product
     * @throws EntityNotFoundException If no product is found with the given SKU
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with SKU: " + sku));
        
        return mapToProductResponse(product);
    }

    /**
     * Retrieves all active products.
     * 
     * @return A list of ProductResponse objects representing all active products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves active products with pagination support.
     * 
     * @param pageable Pagination information including page number, size, and sorting
     * @return A Page of ProductResponse objects representing the paginated active products
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProductsPaginated(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToProductResponse);
    }

    /**
     * Updates an existing product with the provided information.
     * 
     * @param id The ID of the product to update
     * @param request The product request containing the updated product information
     * @return A ProductResponse object containing the details of the updated product
     * @throws EntityNotFoundException If no product is found with the given ID
     * @throws IllegalStateException If the SKU is being changed to one that already exists
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        // Check if SKU is being changed and if new SKU already exists
        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new IllegalStateException("Product with SKU " + request.getSku() + " already exists");
        }

        // Update product
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(request.getSku());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.isActive());

        // Save updated product
        Product updatedProduct = productRepository.save(product);

        // Return response
        return mapToProductResponse(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     * 
     * @param id The ID of the product to delete
     * @throws EntityNotFoundException If no product is found with the given ID
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
    }

    /**
     * Deactivates a product by setting its active status to false.
     * 
     * @param id The ID of the product to deactivate
     * @return A ProductResponse object containing the details of the deactivated product
     * @throws EntityNotFoundException If no product is found with the given ID
     */
    @Transactional
    public ProductResponse deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        product.setActive(false);
        Product updatedProduct = productRepository.save(product);
        
        return mapToProductResponse(updatedProduct);
    }

    /**
     * Searches for products by name (case-insensitive, partial match).
     * 
     * @param name The name or part of the name to search for
     * @return A list of ProductResponse objects representing products that match the search criteria
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps a Product entity to a ProductResponse DTO.
     * 
     * @param product The Product entity to map
     * @return A ProductResponse object containing the mapped product data
     */
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .sku(product.getSku())
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
} 