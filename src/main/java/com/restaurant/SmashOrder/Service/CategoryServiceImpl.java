package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.Category;
import com.restaurant.SmashOrder.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public ResponseEntity<String> createCategory(Category category) {
        try {
            validateCategory(category);
            categoryRepository.save(category);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Category created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating category: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updateCategory(Long id, Category category) {
        try {
            Category existingCategory = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

            validateCategory(category);

            existingCategory.setName(category.getName());

            categoryRepository.save(existingCategory);

            return ResponseEntity.ok("Category updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating category: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    private void validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuntimeException("Category name is required");
        }
    }
}
