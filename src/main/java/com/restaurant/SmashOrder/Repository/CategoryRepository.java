package com.restaurant.SmashOrder.Repository;

import com.restaurant.SmashOrder.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
