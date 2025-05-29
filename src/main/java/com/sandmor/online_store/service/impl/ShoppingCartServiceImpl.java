package com.sandmor.online_store.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandmor.online_store.model.Product;
import com.sandmor.online_store.model.ShoppingCart;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.repository.ShoppingCartRepository;
import com.sandmor.online_store.service.ProductService;
import com.sandmor.online_store.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    
    @Autowired
    private ShoppingCartRepository cartRepository;
    
    @Autowired
    private ProductService productService;
    
    @Override
    public ShoppingCart getOrCreateCartForUser(User user) {
        Optional<ShoppingCart> existingCart = cartRepository.findByCustomer(user);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        
        ShoppingCart newCart = new ShoppingCart(user);
        return cartRepository.save(newCart);
    }
    
    @Override
    public Optional<ShoppingCart> findByCustomer(User customer) {
        return cartRepository.findByCustomer(customer);
    }
    
    @Override
    public void addProductToCart(User customer, Long productId, Integer quantity) {
        Optional<Product> product = productService.findById(productId);
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product not found");
        }
        
        if (!productService.isProductAvailable(productId, quantity)) {
            throw new IllegalArgumentException("Product not available in requested quantity");
        }
        
        ShoppingCart cart = getOrCreateCartForUser(customer);
        cart.addItem(product.get(), quantity);
        cartRepository.save(cart);
    }
    
    @Override
    public void removeProductFromCart(User customer, Long productId) {
        Optional<ShoppingCart> cart = cartRepository.findByCustomer(customer);
        if (cart.isPresent()) {
            cart.get().removeItem(productId);
            cartRepository.save(cart.get());
        }
    }
    
    @Override
    public void updateProductQuantity(User customer, Long productId, Integer quantity) {
        if (quantity <= 0) {
            removeProductFromCart(customer, productId);
            return;
        }
        
        if (!productService.isProductAvailable(productId, quantity)) {
            throw new IllegalArgumentException("Product not available in requested quantity");
        }
        
        Optional<ShoppingCart> cart = cartRepository.findByCustomer(customer);
        if (cart.isPresent()) {
            cart.get().updateItemQuantity(productId, quantity);
            cartRepository.save(cart.get());
        }
    }
    
    @Override
    public void clearCart(User customer) {
        Optional<ShoppingCart> cart = cartRepository.findByCustomer(customer);
        if (cart.isPresent()) {
            cart.get().clearCart();
            cartRepository.save(cart.get());
        }
    }
    
    @Override
    public ShoppingCart saveCart(ShoppingCart cart) {
        return cartRepository.save(cart);
    }
}
