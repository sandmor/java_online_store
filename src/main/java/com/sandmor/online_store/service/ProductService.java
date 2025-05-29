package com.sandmor.online_store.service;

import java.util.List;
import java.util.Optional;

import com.sandmor.online_store.dto.ProductFilterCriteria;
import com.sandmor.online_store.model.Category;
import com.sandmor.online_store.model.Product;

public interface ProductService {
    Product createProduct(Product product);
    Product updateProduct(Product product);
    void deleteProduct(Long id);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(Category category);
    List<Product> findActiveProducts();
    List<Product> findActiveByCategoryId(Long categoryId);
    List<Product> findActiveByCategoryAndSubcategories(Long categoryId);
    List<Product> searchProducts(String searchTerm);
    void updateStock(Long productId, Integer newStock);
    boolean isProductAvailable(Long productId, Integer quantity);
    long countByCategory(Category category);
    long countActiveByCategoryId(Long categoryId);
    
    // Enhanced filtering and sorting methods
    List<Product> findWithFilters(ProductFilterCriteria criteria);
    long countInStock();
    long countLowStock();
    long countOutOfStock();
}
