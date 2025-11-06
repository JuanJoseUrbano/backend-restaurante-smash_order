package com.restaurant.SmashOrder.IService;

import com.restaurant.SmashOrder.Entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
        List<Category> getAllCategories();
        Page<Category> getCategoriesPaginated(int page, int size);
        Optional<Category> getCategoryById(Long id);
        Page<Category> getCategoriesByName(String name, int page, int size);
        ResponseEntity<String> createCategory(Category category);
        ResponseEntity<String> updateCategory(Long id, Category category);
        ResponseEntity<String> deleteCategory(Long id);
        Long countAllCategories();
}
