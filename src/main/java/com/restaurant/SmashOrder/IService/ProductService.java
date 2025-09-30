package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.Entity.Product;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    List<Product> getProductsByCategory(Long categoryId);
    List<Product> getProductsByName(String name);
    ResponseEntity<String> createProduct(Product product);
    ResponseEntity<String> updateProduct(Long id, Product product);
    ResponseEntity<String> deleteProduct(Long id);
    boolean existsById(Long id);
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}
