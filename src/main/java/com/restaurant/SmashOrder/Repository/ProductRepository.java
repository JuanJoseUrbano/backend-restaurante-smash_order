package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.Category;
import com.restaurant.SmashOrder.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findProductsByName(String name);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

}
