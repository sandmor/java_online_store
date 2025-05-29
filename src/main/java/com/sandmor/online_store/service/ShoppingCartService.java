package com.sandmor.online_store.service;

import java.util.Optional;

import com.sandmor.online_store.model.ShoppingCart;
import com.sandmor.online_store.model.User;

public interface ShoppingCartService {
    ShoppingCart getOrCreateCartForUser(User user);
    Optional<ShoppingCart> findByCustomer(User customer);
    void addProductToCart(User customer, Long productId, Integer quantity);
    void removeProductFromCart(User customer, Long productId);
    void updateProductQuantity(User customer, Long productId, Integer quantity);
    void clearCart(User customer);
    ShoppingCart saveCart(ShoppingCart cart);
}
