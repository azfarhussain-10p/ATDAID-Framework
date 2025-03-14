# Product Management Documentation

## Overview

The Product Management module provides a comprehensive set of features for managing products in an e-commerce application. It allows administrators to create, update, delete, and deactivate products, while providing users with the ability to browse and search for products.

## Domain Model

### Product Entity

The core `Product` entity is defined in `com.tenpearls.model.Product` and includes the following attributes:

| Attribute      | Type            | Description                                      | Constraints                |
|----------------|-----------------|--------------------------------------------------|----------------------------|
| id             | Long            | Unique identifier                                | Auto-generated             |
| name           | String          | Product name                                     | Not null                   |
| description    | String          | Product description                              | Max length 1000 characters |
| price          | BigDecimal      | Product price                                    | Not null, precision 10,2   |
| stockQuantity  | Integer         | Available quantity in stock                      | Not null                   |
| sku            | String          | Stock Keeping Unit (unique product identifier)   | Not null, unique           |
| imageUrl       | String          | URL to product image                             | Optional                   |
| active         | boolean         | Whether the product is active                    | Not null, default true     |
| createdAt      | LocalDateTime   | Timestamp when the product was created           | Auto-generated             |
| updatedAt      | LocalDateTime   | Timestamp when the product was last updated      | Auto-updated               |

## Entity-Database Mapping

The `Product` entity is mapped to the `products` table in the database using JPA annotations. The mapping follows these conventions:

1. **Table Mapping**: The entity class is mapped to the database table using the `@Table` annotation:
   ```java
   @Entity
   @Table(name = "products")
   public class Product {
       // ...
   }
   ```

2. **Column Naming Convention**: The database uses snake_case for column names, while the entity uses camelCase for field names. The mapping is handled using the `@Column` annotation:
   ```java
   @Column(name = "stock_quantity", nullable = false)
   private Integer stockQuantity;
   
   @Column(name = "image_url")
   private String imageUrl;
   
   @Column(name = "created_at", nullable = false)
   private LocalDateTime createdAt;
   
   @Column(name = "updated_at", nullable = false)
   private LocalDateTime updatedAt;
   ```

3. **Database Schema**: The database schema for the `products` table is defined in `schema.sql`:
   ```sql
   CREATE TABLE products (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       description VARCHAR(1000),
       price DECIMAL(10, 2) NOT NULL,
       stock_quantity INT NOT NULL,
       sku VARCHAR(100) NOT NULL,
       image_url VARCHAR(255),
       active BOOLEAN NOT NULL,
       created_at TIMESTAMP NOT NULL,
       updated_at TIMESTAMP NOT NULL
   );
   ```

4. **Lifecycle Callbacks**: The entity uses JPA lifecycle callbacks to automatically set the creation and update timestamps:
   ```java
   @PrePersist
   protected void onCreate() {
       createdAt = LocalDateTime.now();
       updatedAt = LocalDateTime.now();
   }
   
   @PreUpdate
   protected void onUpdate() {
       updatedAt = LocalDateTime.now();
   }
   ```

This mapping ensures that the entity fields are correctly stored in and retrieved from the database, maintaining data integrity and consistency.

## Data Transfer Objects (DTOs)

### ProductRequest

Used for creating and updating products. Defined in `com.tenpearls.dto.ProductRequest`.

| Field          | Type            | Description                                      | Validation                                     |
|----------------|-----------------|--------------------------------------------------|------------------------------------------------|
| name           | String          | Product name                                     | Not blank, max 255 characters                  |
| description    | String          | Product description                              | Max 1000 characters                            |
| price          | BigDecimal      | Product price                                    | Not null, min 0                                |
| stockQuantity  | Integer         | Available quantity in stock                      | Not null, min 0                                |
| sku            | String          | Stock Keeping Unit                               | Not blank, max 50 characters                   |
| imageUrl       | String          | URL to product image                             | Optional                                       |
| active         | boolean         | Whether the product is active                    | Default true                                   |

### ProductResponse

Used for returning product information to clients. Defined in `com.tenpearls.dto.ProductResponse`.

| Field          | Type            | Description                                      |
|----------------|-----------------|--------------------------------------------------|
| id             | Long            | Unique identifier                                |
| name           | String          | Product name                                     |
| description    | String          | Product description                              |
| price          | BigDecimal      | Product price                                    |
| stockQuantity  | Integer         | Available quantity in stock                      |
| sku            | String          | Stock Keeping Unit                               |
| imageUrl       | String          | URL to product image                             |
| active         | boolean         | Whether the product is active                    |
| createdAt      | LocalDateTime   | Timestamp when the product was created           |
| updatedAt      | LocalDateTime   | Timestamp when the product was last updated      |

## Service Layer

The `ProductService` class (`com.tenpearls.service.ProductService`) provides the business logic for product management operations.

### Methods

| Method                                                  | Description                                           | Authorization     |
|---------------------------------------------------------|-------------------------------------------------------|-------------------|
| `createProduct(ProductRequest request)`                 | Creates a new product                                 | Admin only        |
| `getProductById(Long id)`                               | Retrieves a product by ID                             | All users         |
| `getProductBySku(String sku)`                           | Retrieves a product by SKU                            | All users         |
| `getAllActiveProducts()`                                | Retrieves all active products                         | All users         |
| `getActiveProductsPaginated(Pageable pageable)`         | Retrieves active products with pagination             | All users         |
| `updateProduct(Long id, ProductRequest request)`        | Updates an existing product                           | Admin only        |
| `deleteProduct(Long id)`                                | Deletes a product                                     | Admin only        |
| `deactivateProduct(Long id)`                            | Deactivates a product without deleting it             | Admin only        |
| `searchProductsByName(String name)`                     | Searches for products by name                         | All users         |

## REST API

The `ProductController` class (`com.tenpearls.controller.ProductController`) exposes the product management functionality through a RESTful API.

### Endpoints

| HTTP Method | Endpoint                          | Description                                           | Request Body      | Response Body     | Status Codes      |
|-------------|-----------------------------------|-------------------------------------------------------|-------------------|-------------------|-------------------|
| POST        | `/api/products`                   | Creates a new product                                 | ProductRequest    | ProductResponse   | 201 Created       |
| GET         | `/api/products/{id}`              | Retrieves a product by ID                             | -                 | ProductResponse   | 200 OK, 404 Not Found |
| GET         | `/api/products/sku/{sku}`         | Retrieves a product by SKU                            | -                 | ProductResponse   | 200 OK, 404 Not Found |
| GET         | `/api/products`                   | Retrieves all active products                         | -                 | List<ProductResponse> | 200 OK          |
| GET         | `/api/products/paginated`         | Retrieves active products with pagination             | Query params      | Page<ProductResponse> | 200 OK          |
| PUT         | `/api/products/{id}`              | Updates an existing product                           | ProductRequest    | ProductResponse   | 200 OK, 404 Not Found |
| DELETE      | `/api/products/{id}`              | Deletes a product                                     | -                 | -                 | 204 No Content, 404 Not Found |
| PATCH       | `/api/products/{id}/deactivate`   | Deactivates a product                                 | -                 | ProductResponse   | 200 OK, 404 Not Found |
| GET         | `/api/products/search`            | Searches for products by name                         | Query param: name | List<ProductResponse> | 200 OK          |

### Query Parameters for Pagination

The `/api/products/paginated` endpoint supports the following query parameters:

| Parameter  | Description                                      | Default Value    |
|------------|--------------------------------------------------|------------------|
| page       | Page number (0-based)                            | 0                |
| size       | Number of items per page                         | 10               |
| sortBy     | Field to sort by                                 | "id"             |
| direction  | Sort direction (asc or desc)                     | "asc"            |

## Exception Handling

The service handles the following exceptions:

| Exception                  | Cause                                              | HTTP Status Code  |
|----------------------------|----------------------------------------------------|--------------------|
| EntityNotFoundException    | Product with specified ID or SKU not found         | 404 Not Found      |
| IllegalStateException      | Duplicate SKU when creating or updating a product  | 400 Bad Request    |
| ValidationException        | Invalid request data                               | 400 Bad Request    |
| AccessDeniedException      | Unauthorized access to admin-only operations       | 403 Forbidden      |

## Testing

The product management functionality is thoroughly tested at multiple levels:

### Unit Tests

Located in `src/test/java/com/tenpearls/service/ProductServiceTest.java`, these tests verify the business logic in isolation by mocking dependencies.

Key test cases:
- Creating products with valid and invalid data
- Retrieving products by ID and SKU
- Updating products with valid and invalid data
- Deleting and deactivating products
- Searching for products by name

### Integration Tests

Located in `src/test/java/com/tenpearls/integration/ProductManagementIntegrationTest.java`, these tests verify the interaction between components using Spring's MockMvc.

Key test cases:
- Creating products as admin and regular users
- Retrieving products by ID
- Updating products as admin and regular users
- Deleting and deactivating products
- Searching for products by name
- Authorization checks for admin-only operations

### Acceptance Tests

Located in `src/test/java/com/tenpearls/accpetance/product/ProductManagementTest.java`, these tests verify the end-to-end functionality using RestAssured.

Key test cases:
- Creating products
- Retrieving products
- Updating products
- Deleting products
- Searching for products

## Usage Examples

### Creating a Product

```java
// Create a product request
ProductRequest request = ProductRequest.builder()
        .name("Smartphone X")
        .description("Latest smartphone with advanced features")
        .price(new BigDecimal("999.99"))
        .stockQuantity(100)
        .sku("PHONE-X-001")
        .imageUrl("https://example.com/images/smartphone-x.jpg")
        .active(true)
        .build();

// Call the service
ProductResponse response = productService.createProduct(request);
```

### Retrieving a Product

```java
// By ID
ProductResponse product = productService.getProductById(1L);

// By SKU
ProductResponse product = productService.getProductBySku("PHONE-X-001");
```

### Updating a Product

```java
// Create an update request
ProductRequest updateRequest = ProductRequest.builder()
        .name("Smartphone X Pro")
        .description("Enhanced version of Smartphone X")
        .price(new BigDecimal("1299.99"))
        .stockQuantity(50)
        .sku("PHONE-X-001") // Same SKU
        .imageUrl("https://example.com/images/smartphone-x-pro.jpg")
        .active(true)
        .build();

// Call the service
ProductResponse updatedProduct = productService.updateProduct(1L, updateRequest);
```

### Searching for Products

```java
// Search by name
List<ProductResponse> products = productService.searchProductsByName("Smartphone");
```

## Best Practices

1. **SKU Management**: Ensure SKUs are unique across the product catalog.
2. **Validation**: Always validate input data before processing.
3. **Authorization**: Enforce proper authorization for admin-only operations.
4. **Pagination**: Use pagination for large result sets to improve performance.
5. **Soft Delete**: Consider using deactivation instead of deletion for historical records.
6. **Testing**: Maintain comprehensive test coverage at all levels. 