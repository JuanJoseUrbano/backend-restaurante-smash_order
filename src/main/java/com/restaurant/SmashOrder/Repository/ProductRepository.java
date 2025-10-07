package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.Category;
import com.restaurant.SmashOrder.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findProductsByName(String name);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    @Query("SELECT COUNT(p) FROM Product p")
    Long countAllProducts();
}
