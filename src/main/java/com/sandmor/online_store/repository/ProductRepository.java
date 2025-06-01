package com.sandmor.online_store.repository;

import java.util.List;

import com.sandmor.online_store.dto.ProductFilterCriteria;
import com.sandmor.online_store.model.Category;
import com.sandmor.online_store.model.Product;

public interface ProductRepository extends BaseRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryAndActiveTrue(Category category);
    List<Product> findByCategoryInAndActiveTrue(List<Category> categories);
    
    List<Product> findWithFilters(ProductFilterCriteria criteria);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByActiveFalse();
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
    List<Product> findByStockQuantityBetween(Integer min, Integer max);
    List<Product> findByStockQuantity(Integer quantity);
    
    long countByStockQuantityGreaterThan(Integer quantity);
    long countByStockQuantityBetween(Integer min, Integer max);
    long countByStockQuantity(Integer quantity);
}