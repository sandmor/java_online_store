package com.sandmor.online_store.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandmor.online_store.dto.OrderFilterCriteria;
import com.sandmor.online_store.model.CartItem;
import com.sandmor.online_store.model.Order;
import com.sandmor.online_store.model.OrderStatus;
import com.sandmor.online_store.model.Product;
import com.sandmor.online_store.model.ShoppingCart;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.repository.OrderRepository;
import com.sandmor.online_store.service.OrderService;
import com.sandmor.online_store.service.ProductService;
import com.sandmor.online_store.service.ShoppingCartService;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ShoppingCartService cartService;
    
    @Override
    public Order createOrderFromCart(User customer, ShoppingCart cart, String paymentMethod, String shippingAddress) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from empty cart");
        }
        
        // Validate all items are still available
        for (CartItem item : cart.getItems()) {
            if (!productService.isProductAvailable(item.getProduct().getId(), item.getQuantity())) {
                throw new IllegalArgumentException("Product " + item.getProduct().getName() + " is not available in requested quantity");
            }
        }
        
        Order order = new Order(customer, cart);
        order.setPaymentMethod(paymentMethod);
        order.setShippingAddress(shippingAddress);
        
        return orderRepository.save(order);
    }
    
    @Override
    public Order processPayment(Order order, String creditCardNumber, String cvv, String expiryDate) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in pending status");
        }
        
        // Simulate payment processing
        boolean paymentSuccessful = simulatePayment(creditCardNumber, cvv, expiryDate);
        
        if (!paymentSuccessful) {
            throw new IllegalArgumentException("Payment failed");
        }
        
        // Reduce stock for all items in the order
        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.reduceStock(item.getQuantity());
            productService.updateProduct(product);
        });
        
        order.processOrder();
        Order savedOrder = orderRepository.save(order);
        
        // Clear the customer's cart after successful order
        cartService.clearCart(order.getCustomer());
        
        return savedOrder;
    }
    
    @Override
    public void processOrder(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }
        
        order.get().processOrder();
        orderRepository.save(order.get());
    }
    
    @Override
    public void cancelOrder(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }
        
        Order o = order.get();
        if (o.getStatus() == OrderStatus.PROCESSED) {
            // Restore stock if order was already processed
            o.getItems().forEach(item -> {
                Product product = item.getProduct();
                product.increaseStock(item.getQuantity());
                productService.updateProduct(product);
            });
        }
        
        o.cancelOrder();
        orderRepository.save(o);
    }
     @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order updateOrder(Order order) {
        if (order.getId() == null || !orderRepository.existsById(order.getId())) {
            throw new IllegalArgumentException("Order not found");
        }
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    @Override
    public List<Order> findByCustomer(User customer) {
        return orderRepository.findByCustomer(customer);
    }
    
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
    
    @Override
    public boolean simulatePayment(String creditCardNumber, String cvv, String expiryDate) {
        return true; // Simulate a successful payment
    }
    
    @Override
    public List<Order> findWithFilters(OrderFilterCriteria criteria) {
        return orderRepository.findWithFilters(criteria);
    }
    
    @Override
    public List<Order> findRecentOrders(int limit) {
        return orderRepository.findRecentOrders(limit);
    }
}
