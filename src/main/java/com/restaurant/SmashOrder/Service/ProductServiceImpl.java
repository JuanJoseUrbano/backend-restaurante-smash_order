package com.restaurant.SmashOrder.Service;

import com.restaurant.SmashOrder.Entity.Category;
import com.restaurant.SmashOrder.Entity.Product;
import com.restaurant.SmashOrder.Repository.CategoryRepository;
import com.restaurant.SmashOrder.Repository.ProductRepository;
import com.restaurant.SmashOrder.IService.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findProductsByName(name);
    }

    @Override
    public ResponseEntity<String> createProduct(Product product) {
        try {
            validateProduct(product);
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setCategory(category);
            productRepository.save(product);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Product created successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating product: " + e.getMessage());
        }
    }

    @Override
        public ResponseEntity<String> updateProduct(Long id, Product product) {
            try {
                Product existingProduct = productRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

                validateProduct(product);

                if (product.getCategory() != null && product.getCategory().getId() != null) {
                    Category category = categoryRepository.findById(product.getCategory().getId())
                            .orElseThrow(() -> new RuntimeException("Category not found"));
                    existingProduct.setCategory(category);
                }

                existingProduct.setName(product.getName());
                existingProduct.setDescription(product.getDescription());
                existingProduct.setPrice(product.getPrice());

                productRepository.save(existingProduct);

                return ResponseEntity.ok("Product updated successfully");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error updating product: " + e.getMessage());
            }
        }

    @Override
    public ResponseEntity<String> deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Price must be greater than zero");
        }

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new RuntimeException("Category is required");
        }
    }
}
