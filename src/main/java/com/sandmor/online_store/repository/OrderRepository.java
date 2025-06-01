package com.sandmor.online_store.repository;

import java.util.List;
import java.util.Optional;

import com.sandmor.online_store.dto.OrderFilterCriteria;
import com.sandmor.online_store.model.Order;
import com.sandmor.online_store.model.OrderStatus;
import com.sandmor.online_store.model.User;

public interface OrderRepository extends BaseRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerAndStatus(User customer, OrderStatus status);
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findWithFilters(OrderFilterCriteria criteria);
    List<Order> findRecentOrders(int limit);
}