package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.Entity.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
        List<Category> getAllCategories();
        Optional<Category> getCategoryById(Long id);
        List<Category> getCategoriesByName(String name);
        ResponseEntity<String> createCategory(Category category);
        ResponseEntity<String> updateCategory(Long id, Category category);
        ResponseEntity<String> deleteCategory(Long id);
        boolean existsById(Long id);

}
