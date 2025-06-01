package com.sandmor.online_store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sandmor.online_store.repository.impl.InMemoryProductRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private InMemoryProductRepository productRepository;
    
    @Override
    public void run(String... args) throws Exception {
        productRepository.initializeDefaultProducts();
    }
}
