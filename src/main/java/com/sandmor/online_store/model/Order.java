package com.sandmor.online_store.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Long id;
    private String orderNumber;
    private User customer;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String paymentMethod;
    private String shippingAddress;
    private String notes;
    
    // Constructors
    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Order(User customer, ShoppingCart cart) {
        this();
        this.customer = customer;
        this.orderNumber = generateOrderNumber();
        
        // Convert cart items to order items
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                cartItem.getProduct(),
                cartItem.getQuantity(),
                cartItem.getUnitPrice()
            );
            this.items.add(orderItem);
        }
        
        this.totalAmount = cart.getTotalAmount();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public User getCustomer() {
        return customer;
    }
    
    public void setCustomer(User customer) {
        this.customer = customer;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
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
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Business methods
    public void processOrder() {
        if (status == OrderStatus.PENDING) {
            this.status = OrderStatus.PROCESSED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Only pending orders can be processed");
        }
    }
    
    public void cancelOrder() {
        if (status == OrderStatus.PENDING || status == OrderStatus.PROCESSED) {
            this.status = OrderStatus.CANCELED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Only pending or processed orders can be canceled");
        }
    }
    
    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }
    
    public boolean isProcessed() {
        return status == OrderStatus.PROCESSED;
    }
    
    public boolean isCanceled() {
        return status == OrderStatus.CANCELED;
    }
    
    public Integer getTotalItems() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
}