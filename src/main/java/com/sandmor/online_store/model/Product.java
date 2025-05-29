package com.sandmor.online_store.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Product {
    private Long id;
    
    @NotBlank(message = "Product name is required")
    private String name;
    
    @NotBlank(message = "Product description is required")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
    
    @NotNull(message = "Category is required")
    private Category category;
    
    private String imageUrl;
    private boolean active;
    
    // Custom specifications based on category
    private Map<String, String> specifications;
    
    // Constructors
    public Product() {
        this.specifications = new HashMap<>();
        this.active = true;
    }
    
    public Product(String name, String description, BigDecimal price, Integer stockQuantity, Category category) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = (imageUrl != null && imageUrl.trim().isEmpty()) ? null : imageUrl;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Map<String, String> getSpecifications() {
        return specifications;
    }
    
    public void setSpecifications(Map<String, String> specifications) {
        this.specifications = specifications;
    }
    
    // Helper methods
    public void addSpecification(String key, String value) {
        specifications.put(key, value);
    }
    
    public String getSpecification(String key) {
        return specifications.get(key);
    }
    
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }
    
    public boolean isInStock() {
        return stockQuantity > 0;
    }
    
    public boolean isAvailable() {
        return active && isInStock();
    }
    
    public void reduceStock(int quantity) {
        if (stockQuantity >= quantity) {
            stockQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("Not enough stock available");
        }
    }
    
    public void increaseStock(int quantity) {
        stockQuantity += quantity;
    }
}