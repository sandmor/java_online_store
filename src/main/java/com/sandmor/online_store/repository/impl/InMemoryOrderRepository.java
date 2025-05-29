package com.sandmor.online_store.repository.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sandmor.online_store.dto.OrderFilterCriteria;
import com.sandmor.online_store.model.Order;
import com.sandmor.online_store.model.OrderStatus;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.repository.OrderRepository;

@Repository
public class InMemoryOrderRepository implements OrderRepository {
    
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
        }
        orders.put(order.getId(), order);
        return order;
    }
    
    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }
    
    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
    
    @Override
    public void deleteById(Long id) {
        orders.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return orders.containsKey(id);
    }
    
    @Override
    public long count() {
        return orders.size();
    }
    
    @Override
    public List<Order> findByCustomer(User customer) {
        return orders.values().stream()
                .filter(order -> order.getCustomer().getId().equals(customer.getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByCustomerAndStatus(User customer, OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getCustomer().getId().equals(customer.getId()) 
                               && order.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orders.values().stream()
                .filter(order -> order.getOrderNumber().equals(orderNumber))
                .findFirst();
    }
    
    @Override
    public List<Order> findWithFilters(OrderFilterCriteria criteria) {
        List<Order> result = new ArrayList<>(orders.values());
        
        // Apply search filter
        if (criteria.hasSearch()) {
            String searchTerm = criteria.getSearch().toLowerCase();
            result = result.stream()
                .filter(order -> 
                    order.getOrderNumber().toLowerCase().contains(searchTerm) ||
                    order.getCustomer().getUsername().toLowerCase().contains(searchTerm) ||
                    order.getCustomer().getEmail().toLowerCase().contains(searchTerm) ||
                    order.getCustomer().getFirstName().toLowerCase().contains(searchTerm) ||
                    order.getCustomer().getLastName().toLowerCase().contains(searchTerm)
                )
                .collect(Collectors.toList());
        }
        
        // Apply status filter
        if (criteria.hasStatus()) {
            try {
                OrderStatus status = OrderStatus.valueOf(criteria.getStatus().toUpperCase());
                result = result.stream()
                    .filter(order -> order.getStatus() == status)
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, return empty list
                return new ArrayList<>();
            }
        }
        
        // Apply date range filter
        if (criteria.hasFromDate()) {
            result = result.stream()
                .filter(order -> !order.getCreatedAt().toLocalDate().isBefore(criteria.getFromDate()))
                .collect(Collectors.toList());
        }
        
        if (criteria.hasToDate()) {
            result = result.stream()
                .filter(order -> !order.getCreatedAt().toLocalDate().isAfter(criteria.getToDate()))
                .collect(Collectors.toList());
        }
        
        // Apply sorting
        if (criteria.hasSorting()) {
            Comparator<Order> comparator = switch (criteria.getSortBy()) {
                case "date" -> Comparator.comparing(Order::getCreatedAt);
                case "total" -> Comparator.comparing(Order::getTotalAmount);
                case "customer" -> Comparator.comparing(o -> o.getCustomer().getUsername(), String.CASE_INSENSITIVE_ORDER);
                case "status" -> Comparator.comparing(o -> o.getStatus().name());
                case "id" -> Comparator.comparing(Order::getId);
                default -> null;
            };
            
            if (comparator != null) {
                if (criteria.isDescending()) {
                    comparator = comparator.reversed();
                }
                result.sort(comparator);
            }
        }
        
        // Apply limit
        if (criteria.hasLimit()) {
            result = result.stream()
                .limit(criteria.getLimit())
                .collect(Collectors.toList());
        }
        
        return result;
    }
    
    @Override
    public List<Order> findRecentOrders(int limit) {
        return orders.values().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}