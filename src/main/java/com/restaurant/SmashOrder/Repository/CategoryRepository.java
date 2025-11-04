package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Long countBy();
}
