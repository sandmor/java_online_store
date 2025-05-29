package com.sandmor.online_store.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandmor.online_store.dto.ProductFilterCriteria;
import com.sandmor.online_store.model.Category;
import com.sandmor.online_store.model.Product;
import com.sandmor.online_store.repository.ProductRepository;
import com.sandmor.online_store.service.CategoryService;
import com.sandmor.online_store.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryService categoryService;
    
    @Override
    public Product createProduct(Product product) {
        validateProduct(product);
        return productRepository.save(product);
    }
    
    @Override
    public Product updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new IllegalArgumentException("Product not found");
        }
        validateProduct(product);
        return productRepository.save(product);
    }
    
    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    
    @Override
    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }
    
    @Override
    public List<Product> findActiveProducts() {
        return productRepository.findByActiveTrue();
    }
    
    @Override
    public List<Product> findActiveByCategoryId(Long categoryId) {
        Optional<Category> category = categoryService.findById(categoryId);
        if (category.isPresent()) {
            return productRepository.findByCategoryAndActiveTrue(category.get());
        }
        return List.of();
    }
    
    @Override
    public List<Product> findActiveByCategoryAndSubcategories(Long categoryId) {
        Optional<Category> category = categoryService.findById(categoryId);
        if (category.isPresent()) {
            // Get all descendant categories (including the category itself)
            List<Category> allCategories = category.get().getAllDescendants();
            return productRepository.findByCategoryInAndActiveTrue(allCategories);
        }
        return List.of();
    }
    
    @Override
    public List<Product> searchProducts(String searchTerm) {
        return productRepository.findByNameContainingIgnoreCase(searchTerm);
    }
    
    @Override
    public void updateStock(Long productId, Integer newStock) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            Product p = product.get();
            p.setStockQuantity(newStock);
            productRepository.save(p);
        } else {
            throw new IllegalArgumentException("Product not found");
        }
    }
    
    @Override
    public boolean isProductAvailable(Long productId, Integer quantity) {
        Optional<Product> product = productRepository.findById(productId);
        return product.isPresent() && 
               product.get().isActive() && 
               product.get().getStockQuantity() >= quantity;
    }
    
    @Override
    public long countByCategory(Category category) {
        return productRepository.findByCategory(category).size();
    }
    
    @Override
    public long countActiveByCategoryId(Long categoryId) {
        Optional<Category> category = categoryService.findById(categoryId);
        if (category.isPresent()) {
            return productRepository.findByCategoryAndActiveTrue(category.get()).size();
        }
        return 0;
    }
    
    @Override
    public List<Product> findWithFilters(ProductFilterCriteria criteria) {
        return productRepository.findWithFilters(criteria);
    }
    
    @Override
    public long countInStock() {
        return productRepository.countByStockQuantityGreaterThan(0);
    }
    
    @Override
    public long countLowStock() {
        return productRepository.countByStockQuantityBetween(1, 5);
    }
    
    @Override
    public long countOutOfStock() {
        return productRepository.countByStockQuantity(0);
    }
    
    private void validateProduct(Product product) {
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Product must have a category");
        }
        if (!categoryService.findById(product.getCategory().getId()).isPresent()) {
            throw new IllegalArgumentException("Invalid category");
        }
    }
}
