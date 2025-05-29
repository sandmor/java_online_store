package com.sandmor.online_store.repository;

import java.util.Optional;

import com.sandmor.online_store.model.ShoppingCart;
import com.sandmor.online_store.model.User;

public interface ShoppingCartRepository extends BaseRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByCustomer(User customer);
    void deleteByCustomer(User customer);
}