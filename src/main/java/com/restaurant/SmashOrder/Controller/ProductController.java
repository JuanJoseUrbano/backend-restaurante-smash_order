package com.restaurant.SmashOrder.Controller;

import com.restaurant.SmashOrder.Entity.Product;
import com.restaurant.SmashOrder.IService.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.status(404).body("Product not found with id: " + id);
        }
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping("/search")
    public List<Product> getProductsByName(@RequestParam String name) {
        return productService.getProductsByName(name);
    }
    @GetMapping("/price-range")
    public List<Product> getProductsByPriceRange(@RequestParam BigDecimal minPrice,
                                                 @RequestParam BigDecimal maxPrice) {
        return productService.getProductsByPriceRange(minPrice, maxPrice);
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countAllProducts() {
        return ResponseEntity.ok( productService.countAllProducts());
    }
}
