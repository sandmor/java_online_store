package com.sandmor.online_store.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.sandmor.online_store.model.ShoppingCart;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.repository.ShoppingCartRepository;

@Repository
public class InMemoryShoppingCartRepository implements ShoppingCartRepository {
    
    private final Map<Long, ShoppingCart> carts = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public ShoppingCart save(ShoppingCart cart) {
        if (cart.getId() == null) {
            cart.setId(idGenerator.getAndIncrement());
        }
        carts.put(cart.getId(), cart);
        return cart;
    }
    
    @Override
    public Optional<ShoppingCart> findById(Long id) {
        return Optional.ofNullable(carts.get(id));
    }
    
    @Override
    public List<ShoppingCart> findAll() {
        return new ArrayList<>(carts.values());
    }
    
    @Override
    public void deleteById(Long id) {
        carts.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return carts.containsKey(id);
    }
    
    @Override
    public long count() {
        return carts.size();
    }
    
    @Override
    public Optional<ShoppingCart> findByCustomer(User customer) {
        return carts.values().stream()
                .filter(cart -> cart.getCustomer().getId().equals(customer.getId()))
                .findFirst();
    }
    
    @Override
    public void deleteByCustomer(User customer) {
        carts.values().removeIf(cart -> cart.getCustomer().getId().equals(customer.getId()));
    }
}