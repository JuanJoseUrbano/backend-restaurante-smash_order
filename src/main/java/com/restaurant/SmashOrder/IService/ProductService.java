package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Page<Product> getProductsPaginated(int page, int size);
    Optional<Product> getProductById(Long id);
    List<Product> getProductsByCategory(Long categoryId);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    Page<Product> getPaginatedProductsByCategory(Long categoryId, int page, int size);
    Page<Product> getPaginatedProductsByName(String name, int page, int size);
    Page<Product> getPaginatedProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size);
    ResponseEntity<String> createProduct(Product product);
    ResponseEntity<String> updateProduct(Long id, Product product);
    ResponseEntity<String> deleteProduct(Long id);
    Long countAllProducts();
}
