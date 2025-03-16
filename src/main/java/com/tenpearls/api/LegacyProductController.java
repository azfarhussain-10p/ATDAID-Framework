package com.tenpearls.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Product Management operations.
 * 
 * This controller provides endpoints for creating, retrieving, and listing products.
 * Product creation is restricted to users with admin privileges.
 * 
 * @deprecated This is a legacy implementation. Use {@link com.tenpearls.controller.ProductController} instead.
 */
@RestController
@RequestMapping("/api/legacy/products")
@Deprecated
public class LegacyProductController {

    private final Map<String, Product> products = new HashMap<>();

    /**
     * Creates a new product.
     * 
     * @param product The product data to create
     * @param authHeader Authorization header value, must contain "admin" to create products
     * @return ResponseEntity with created product or error message
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product, 
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.contains("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Insufficient permissions to create product"));
        }
        
        String id = UUID.randomUUID().toString();
        product.setId(id);
        products.put(id, product);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    /**
     * Retrieves a product by ID.
     * 
     * @param id The ID of the product to retrieve
     * @return ResponseEntity with product or error message if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        Product product = products.get(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Product not found"));
        }
        return ResponseEntity.ok(product);
    }

    /**
     * Retrieves all products.
     * 
     * @return ResponseEntity with all available products
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(products.values());
    }

    /**
     * Product model representing a product in the system.
     */
    public static class Product {
        private String id;
        private String name;
        private String description;
        private double price;
        private String category;
        private boolean inStock;

        public Product() {
        }

        public Product(String id, String name, String description, double price, String category, boolean inStock) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.inStock = inStock;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public boolean isInStock() {
            return inStock;
        }

        public void setInStock(boolean inStock) {
            this.inStock = inStock;
        }
    }
}