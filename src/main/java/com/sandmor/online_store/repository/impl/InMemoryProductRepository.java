package com.sandmor.online_store.repository.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sandmor.online_store.dto.ProductFilterCriteria;
import com.sandmor.online_store.model.Category;
import com.sandmor.online_store.model.Product;
import com.sandmor.online_store.repository.CategoryRepository;
import com.sandmor.online_store.repository.ProductRepository;

@Repository
public class InMemoryProductRepository implements ProductRepository {
    
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public InMemoryProductRepository() {
        // Products will be initialized after category repository is available
    }
    
    public void initializeDefaultProducts() {
        if (products.isEmpty() && categoryRepository != null) {
            // Find categories
            Optional<Category> smartphonesOpt = categoryRepository.findByName("Smartphones");
            Optional<Category> laptopsOpt = categoryRepository.findByName("Laptops");
            Optional<Category> mensClothingOpt = categoryRepository.findByName("Men's Clothing");
            Optional<Category> furnitureOpt = categoryRepository.findByName("Furniture");
            
            // Create sample products
            if (smartphonesOpt.isPresent()) {
                Category smartphones = smartphonesOpt.get();
                
                Product iphone = new Product("iPhone 15 Pro", "Latest iPhone with advanced features", 
                                           new BigDecimal("999.99"), 50, smartphones);
                iphone.addSpecification("Brand", "Apple");
                iphone.addSpecification("Screen Size", "6.1 inches");
                iphone.addSpecification("Storage", "128GB");
                iphone.addSpecification("RAM", "8GB");
                iphone.addSpecification("Camera", "48MP");
                save(iphone);
                
                Product samsung = new Product("Samsung Galaxy S24", "Premium Android smartphone", 
                                            new BigDecimal("899.99"), 30, smartphones);
                samsung.addSpecification("Brand", "Samsung");
                samsung.addSpecification("Screen Size", "6.2 inches");
                samsung.addSpecification("Storage", "256GB");
                samsung.addSpecification("RAM", "12GB");
                samsung.addSpecification("Camera", "50MP");
                save(samsung);
            }
            
            if (laptopsOpt.isPresent()) {
                Category laptops = laptopsOpt.get();
                
                Product macbook = new Product("MacBook Pro 16\"", "Powerful laptop for professionals", 
                                            new BigDecimal("2499.99"), 20, laptops);
                macbook.addSpecification("Brand", "Apple");
                macbook.addSpecification("Processor", "M3 Pro");
                macbook.addSpecification("RAM", "18GB");
                macbook.addSpecification("Storage", "512GB SSD");
                macbook.addSpecification("Screen Size", "16 inches");
                save(macbook);
                
                Product dell = new Product("Dell XPS 13", "Compact and powerful ultrabook", 
                                         new BigDecimal("1299.99"), 25, laptops);
                dell.addSpecification("Brand", "Dell");
                dell.addSpecification("Processor", "Intel i7");
                dell.addSpecification("RAM", "16GB");
                dell.addSpecification("Storage", "512GB SSD");
                dell.addSpecification("Screen Size", "13.4 inches");
                save(dell);
            }
            
            if (mensClothingOpt.isPresent()) {
                Category mensClothing = mensClothingOpt.get();
                
                Product shirt = new Product("Classic White Shirt", "Elegant white dress shirt", 
                                          new BigDecimal("49.99"), 100, mensClothing);
                shirt.addSpecification("Size", "L");
                shirt.addSpecification("Color", "White");
                shirt.addSpecification("Material", "100% Cotton");
                shirt.addSpecification("Chest Size", "42 inches");
                save(shirt);
                
                Product jeans = new Product("Blue Denim Jeans", "Comfortable straight-fit jeans", 
                                          new BigDecimal("79.99"), 75, mensClothing);
                jeans.addSpecification("Size", "32x34");
                jeans.addSpecification("Color", "Dark Blue");
                jeans.addSpecification("Material", "98% Cotton, 2% Elastane");
                jeans.addSpecification("Waist Size", "32 inches");
                save(jeans);
            }
            
            if (furnitureOpt.isPresent()) {
                Category furniture = furnitureOpt.get();
                
                Product sofa = new Product("3-Seat Sofa", "Comfortable living room sofa", 
                                         new BigDecimal("799.99"), 15, furniture);
                sofa.addSpecification("Dimensions", "84\" W x 36\" D x 32\" H");
                sofa.addSpecification("Weight", "120 lbs");
                sofa.addSpecification("Material", "Fabric");
                sofa.addSpecification("Assembly Required", "Yes");
                sofa.addSpecification("Style", "Modern");
                save(sofa);
            }
        }
    }
    
    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
        }
        products.put(product.getId(), product);
        return product;
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }
    
    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
    
    @Override
    public void deleteById(Long id) {
        products.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return products.containsKey(id);
    }
    
    @Override
    public long count() {
        return products.size();
    }
    
    @Override
    public List<Product> findByCategory(Category category) {
        return products.values().stream()
                .filter(product -> product.getCategory().equals(category))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByNameContainingIgnoreCase(String name) {
        return products.values().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByActiveTrue() {
        return products.values().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategoryAndActiveTrue(Category category) {
        return products.values().stream()
                .filter(product -> product.getCategory().equals(category) && product.isActive())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategoryInAndActiveTrue(List<Category> categories) {
        return products.values().stream()
                .filter(product -> categories.contains(product.getCategory()) && product.isActive())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findWithFilters(ProductFilterCriteria criteria) {
        List<Product> result = new ArrayList<>(products.values());
        
        // Apply search filter
        if (criteria.hasSearch()) {
            result = result.stream()
                .filter(p -> p.getName().toLowerCase().contains(criteria.getSearch().toLowerCase()) ||
                           (p.getDescription() != null && p.getDescription().toLowerCase().contains(criteria.getSearch().toLowerCase())))
                .collect(Collectors.toList());
        }
        
        // Apply category filter (including subcategories)
        if (criteria.hasCategory()) {
            Optional<Category> category = categoryRepository.findById(criteria.getCategoryId());
            if (category.isPresent()) {
                List<Category> allCategories = category.get().getAllDescendants();
                result = result.stream()
                    .filter(p -> p.getCategory() != null && allCategories.contains(p.getCategory()))
                    .collect(Collectors.toList());
            } else {
                // If category not found, return empty result
                result = new ArrayList<>();
            }
        }
        
        // Apply status filter
        if (criteria.hasStatus()) {
            switch (criteria.getStatus()) {
                case "active":
                    result = result.stream().filter(Product::isActive).collect(Collectors.toList());
                    break;
                case "inactive":
                    result = result.stream().filter(p -> !p.isActive()).collect(Collectors.toList());
                    break;
                case "in-stock":
                    result = result.stream().filter(p -> p.getStockQuantity() > 0).collect(Collectors.toList());
                    break;
                case "low-stock":
                    result = result.stream().filter(p -> p.getStockQuantity() > 0 && p.getStockQuantity() <= 5).collect(Collectors.toList());
                    break;
                case "out-of-stock":
                    result = result.stream().filter(p -> p.getStockQuantity() == 0).collect(Collectors.toList());
                    break;
            }
        }
        
        // Apply sorting
        if (criteria.hasSorting()) {
            Comparator<Product> comparator = null;
            switch (criteria.getSortBy()) {
                case "name":
                    comparator = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "price":
                    comparator = Comparator.comparing(Product::getPrice);
                    break;
                case "stock":
                    comparator = Comparator.comparing(Product::getStockQuantity);
                    break;
                case "category":
                    comparator = Comparator.comparing(p -> p.getCategory() != null ? p.getCategory().getName() : "", String.CASE_INSENSITIVE_ORDER);
                    break;
            }
            
            if (comparator != null) {
                if (criteria.isDescending()) {
                    comparator = comparator.reversed();
                }
                result.sort(comparator);
            }
        }
        
        return result;
    }
    
    @Override
    public List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description) {
        return products.values().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()) ||
                                 (product.getDescription() != null && product.getDescription().toLowerCase().contains(description.toLowerCase())))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return products.values().stream()
                .filter(product -> product.getCategory() != null && product.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByActiveFalse() {
        return products.values().stream()
                .filter(product -> !product.isActive())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByStockQuantityGreaterThan(Integer quantity) {
        return products.values().stream()
                .filter(product -> product.getStockQuantity() > quantity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByStockQuantityBetween(Integer min, Integer max) {
        return products.values().stream()
                .filter(product -> product.getStockQuantity() >= min && product.getStockQuantity() <= max)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByStockQuantity(Integer quantity) {
        return products.values().stream()
                .filter(product -> product.getStockQuantity().equals(quantity))
                .collect(Collectors.toList());
    }
    
    @Override
    public long countByStockQuantityGreaterThan(Integer quantity) {
        return products.values().stream()
                .filter(product -> product.getStockQuantity() > quantity)
                .count();
    }
    
    @Override
    public long countByStockQuantityBetween(Integer min, Integer max) {
        return products.values().stream()
                .filter(product -> product.getStockQuantity() >= min && product.getStockQuantity() <= max)
                .count();
    }
    
    @Override
    public long countByStockQuantity(Integer quantity) {
        return products.values().stream()
                .filter(product -> product.getStockQuantity().equals(quantity))
                .count();
    }
}