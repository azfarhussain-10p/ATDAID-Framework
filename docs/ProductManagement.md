# ATDAID Framework Product Management Module

This document provides detailed information about the Product Management module in the ATDAID Framework, including its architecture, features, and usage guidelines.

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Domain Model](#domain-model)
4. [API Endpoints](#api-endpoints)
5. [Security](#security)
6. [Validation](#validation)
7. [Error Handling](#error-handling)
8. [Integration](#integration)
9. [Performance Considerations](#performance-considerations)
10. [Best Practices](#best-practices)
11. [Examples](#examples)

## Overview

The Product Management module provides a comprehensive solution for managing products in the ATDAID Framework. It includes features for creating, retrieving, updating, and deleting products, as well as managing product categories, inventory, and pricing.

### Key Features

- **Complete CRUD Operations**: Create, read, update, and delete products
- **Category Management**: Organize products into categories and subcategories
- **Inventory Management**: Track product stock levels and availability
- **Pricing Management**: Manage product prices, discounts, and promotions
- **Search and Filtering**: Advanced search and filtering capabilities
- **Role-Based Access Control**: Secure product management operations
- **Audit Logging**: Track changes to products for compliance and debugging

## Architecture

The Product Management module follows a layered architecture:

### Architectural Layers

1. **Presentation Layer**: REST API controllers
2. **Service Layer**: Business logic and transaction management
3. **Repository Layer**: Data access and persistence
4. **Domain Layer**: Entity models and business rules

### Component Diagram

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │     │                 │
│  Controllers    │────▶│    Services     │────▶│  Repositories   │────▶│   Database      │
│                 │     │                 │     │                 │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘     └─────────────────┘
        ▲                       ▲                       ▲
        │                       │                       │
        │                       │                       │
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│     DTOs        │     │   Entities      │     │   Mappers       │
│                 │     │                 │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

### Key Components

- **ProductController**: Handles HTTP requests for product operations
- **ProductService**: Implements business logic for product management
- **ProductRepository**: Provides data access methods for products
- **Product**: Domain entity representing a product
- **ProductDTO**: Data transfer object for product operations
- **ProductMapper**: Maps between Product entities and DTOs

## Domain Model

### Product Entity

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String sku;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Column(nullable = false)
    private boolean active;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductAttribute> attributes = new HashSet<>();
    
    // Getters and setters
}
```

### Category Entity

```java
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Category> subcategories = new HashSet<>();
    
    // Getters and setters
}
```

### ProductAttribute Entity

```java
@Entity
@Table(name = "product_attributes")
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String value;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    // Getters and setters
}
```

## API Endpoints

The Product Management module exposes the following REST API endpoints:

### Product Endpoints

| Method | Endpoint                   | Description                           | Access Control    |
|--------|----------------------------|---------------------------------------|-------------------|
| GET    | /api/products              | Get all products                      | All authenticated |
| GET    | /api/products/{id}         | Get product by ID                     | All authenticated |
| POST   | /api/products              | Create a new product                  | ADMIN             |
| PUT    | /api/products/{id}         | Update an existing product            | ADMIN             |
| DELETE | /api/products/{id}         | Delete a product                      | ADMIN             |
| GET    | /api/products/search       | Search products by criteria           | All authenticated |
| GET    | /api/products/category/{id}| Get products by category              | All authenticated |

### Category Endpoints

| Method | Endpoint                   | Description                           | Access Control    |
|--------|----------------------------|---------------------------------------|-------------------|
| GET    | /api/categories            | Get all categories                    | All authenticated |
| GET    | /api/categories/{id}       | Get category by ID                    | All authenticated |
| POST   | /api/categories            | Create a new category                 | ADMIN             |
| PUT    | /api/categories/{id}       | Update an existing category           | ADMIN             |
| DELETE | /api/categories/{id}       | Delete a category                     | ADMIN             |

## Security

The Product Management module implements role-based access control:

### Roles and Permissions

- **ADMIN**: Full access to all product management operations
- **USER**: Read-only access to products and categories
- **MANAGER**: Read access to all products and categories, write access to inventory

### Security Implementation

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ProductDTO> getAllProducts() {
        // Implementation
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO createProduct(@Valid @RequestBody ProductDTO productDTO) {
        // Implementation
    }
    
    // Other endpoints
}
```

## Validation

The Product Management module implements comprehensive validation:

### Input Validation

```java
public class ProductDTO {
    
    private Long id;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;
    
    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z0-9]{6,10}$", message = "SKU must be 6-10 uppercase letters and numbers")
    private String sku;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    
    // Other fields and validation
}
```

### Business Rule Validation

```java
@Service
public class ProductServiceImpl implements ProductService {
    
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        // Check if SKU already exists
        if (productRepository.findBySku(productDTO.getSku()).isPresent()) {
            throw new BusinessException("Product with SKU " + productDTO.getSku() + " already exists");
        }
        
        // Check if category exists
        Category category = null;
        if (productDTO.getCategoryId() != null) {
            category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }
        
        // Create product
        Product product = productMapper.toEntity(productDTO);
        product.setCategory(category);
        product.setActive(true);
        
        return productRepository.save(product);
    }
    
    // Other methods
}
```

## Error Handling

The Product Management module implements a global exception handling mechanism:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ValidationErrorResponse error = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            LocalDateTime.now()
        );
        
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            error.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        });
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    // Other exception handlers
}
```

## Integration

The Product Management module integrates with other modules in the ATDAID Framework:

### Integration with Order Management

```java
@Service
public class OrderServiceImpl implements OrderService {
    
    private final ProductService productService;
    
    @Autowired
    public OrderServiceImpl(ProductService productService) {
        this.productService = productService;
    }
    
    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        // Validate product availability
        for (OrderItemDTO item : orderDTO.getItems()) {
            Product product = productService.getProductById(item.getProductId());
            
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }
            
            // Update product stock
            productService.updateStock(product.getId(), product.getStockQuantity() - item.getQuantity());
        }
        
        // Create order
        // ...
    }
    
    // Other methods
}
```

### Integration with Logging

```java
@Service
public class ProductServiceImpl implements ProductService {
    
    private final LoggerUtils logger = LoggerUtils.getInstance();
    
    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        // Update product
        Product updatedProduct = productMapper.updateEntityFromDto(productDTO, product);
        Product savedProduct = productRepository.save(updatedProduct);
        
        // Log the update
        Map<String, Object> context = new HashMap<>();
        context.put("productId", savedProduct.getId());
        context.put("sku", savedProduct.getSku());
        context.put("updatedBy", SecurityContextHolder.getContext().getAuthentication().getName());
        
        logger.info("Product updated: " + savedProduct.getName(), context);
        
        return savedProduct;
    }
    
    // Other methods
}
```

## Performance Considerations

### Caching

```java
@Service
@CacheConfig(cacheNames = "products")
public class ProductServiceImpl implements ProductService {
    
    @Cacheable(key = "#id")
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
    
    @CacheEvict(key = "#id")
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        productRepository.delete(product);
    }
    
    @CachePut(key = "#result.id")
    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        // Implementation
    }
    
    // Other methods
}
```

### Pagination

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    public Page<ProductDTO> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return productService.getAllProducts(pageable);
    }
    
    // Other endpoints
}
```

## Best Practices

### Product Management Best Practices

1. **Unique SKUs**: Ensure all products have unique SKUs
2. **Consistent Naming**: Use consistent naming conventions for products
3. **Complete Information**: Ensure all required product information is provided
4. **Category Organization**: Organize products into appropriate categories
5. **Regular Audits**: Regularly audit product data for accuracy
6. **Stock Monitoring**: Monitor stock levels and set up alerts for low stock
7. **Price History**: Maintain a history of price changes
8. **Image Management**: Store and manage product images efficiently

### Implementation Best Practices

1. **Validation**: Implement comprehensive validation for all inputs
2. **Transactions**: Use transactions for operations that modify multiple entities
3. **Caching**: Implement caching for frequently accessed products
4. **Pagination**: Use pagination for large result sets
5. **Logging**: Log all significant product operations
6. **Security**: Implement proper access control for product operations
7. **Error Handling**: Provide clear error messages for failed operations
8. **Testing**: Write comprehensive tests for all product operations

## Examples

### Creating a Product

```java
// Create a product DTO
ProductDTO productDTO = new ProductDTO();
productDTO.setName("Smartphone X");
productDTO.setSku("SMX12345");
productDTO.setPrice(new BigDecimal("599.99"));
productDTO.setStockQuantity(100);
productDTO.setCategoryId(1L);

// Add attributes
List<ProductAttributeDTO> attributes = new ArrayList<>();
attributes.add(new ProductAttributeDTO("Color", "Black"));
attributes.add(new ProductAttributeDTO("Storage", "128GB"));
attributes.add(new ProductAttributeDTO("RAM", "8GB"));
productDTO.setAttributes(attributes);

// Call the service
Product createdProduct = productService.createProduct(productDTO);
```

### Searching for Products

```java
// Create search criteria
ProductSearchCriteria criteria = new ProductSearchCriteria();
criteria.setMinPrice(new BigDecimal("100.00"));
criteria.setMaxPrice(new BigDecimal("500.00"));
criteria.setCategoryId(2L);
criteria.setKeyword("laptop");

// Set up pagination
Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "price"));

// Search products
Page<ProductDTO> products = productService.searchProducts(criteria, pageable);
```

### Updating Product Stock

```java
// Update stock quantity
productService.updateStock(productId, newStockQuantity);

// Or increment/decrement stock
productService.incrementStock(productId, quantityToAdd);
productService.decrementStock(productId, quantityToSubtract);
```
