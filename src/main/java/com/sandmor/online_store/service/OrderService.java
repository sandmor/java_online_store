package com.sandmor.online_store.service;

import java.util.List;
import java.util.Optional;

import com.sandmor.online_store.dto.OrderFilterCriteria;
import com.sandmor.online_store.model.Order;
import com.sandmor.online_store.model.OrderStatus;
import com.sandmor.online_store.model.ShoppingCart;
import com.sandmor.online_store.model.User;

public interface OrderService {
    Order createOrderFromCart(User customer, ShoppingCart cart, String paymentMethod, String shippingAddress);
    Order processPayment(Order order, String creditCardNumber, String cvv, String expiryDate);
    void processOrder(Long orderId);
    void cancelOrder(Long orderId);
    Order updateOrder(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomer(User customer);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findAll();
    boolean simulatePayment(String creditCardNumber, String cvv, String expiryDate);
    
    void deleteById(Long id);
    
    List<Order> findWithFilters(OrderFilterCriteria criteria);
    List<Order> findRecentOrders(int limit);
}
