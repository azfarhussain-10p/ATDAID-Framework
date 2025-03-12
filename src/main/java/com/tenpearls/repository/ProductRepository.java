package com.tenpearls.repository;

import com.tenpearls.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    List<Product> findByActiveTrue();
    
    Page<Product> findByActiveTrue(Pageable pageable);
    
    List<Product> findByNameContainingIgnoreCase(String name);
} 