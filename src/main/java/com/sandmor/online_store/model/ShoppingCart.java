package com.sandmor.online_store.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShoppingCart {
    private Long id;
    private User customer;
    private List<CartItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public ShoppingCart(User customer) {
        this();
        this.customer = customer;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getCustomer() {
        return customer;
    }
    
    public void setCustomer(User customer) {
        this.customer = customer;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business methods
    public void addItem(Product product, Integer quantity) {
        Optional<CartItem> existingItem = findItemByProduct(product);
        
        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(quantity);
        } else {
            CartItem newItem = new CartItem(product, quantity);
            newItem.setId(generateNextItemId());
            items.add(newItem);
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateItemQuantity(Long productId, Integer newQuantity) {
        Optional<CartItem> item = items.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst();
        
        if (item.isPresent()) {
            if (newQuantity <= 0) {
                removeItem(productId);
            } else {
                item.get().setQuantity(newQuantity);
                this.updatedAt = LocalDateTime.now();
            }
        }
    }
    
    public void clearCart() {
        items.clear();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public Integer getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    private Optional<CartItem> findItemByProduct(Product product) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();
    }
    
    private Long generateNextItemId() {
        return items.stream()
                .mapToLong(item -> item.getId() != null ? item.getId() : 0L)
                .max()
                .orElse(0L) + 1L;
    }
}