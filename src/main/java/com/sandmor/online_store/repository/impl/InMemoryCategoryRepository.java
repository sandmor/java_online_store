package com.sandmor.online_store.repository.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sandmor.online_store.model.Category;
import com.sandmor.online_store.repository.CategoryRepository;

@Repository
public class InMemoryCategoryRepository implements CategoryRepository {
    
    private final Map<Long, Category> categories = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public InMemoryCategoryRepository() {
        initializeDefaultCategories();
    }
    
    private void initializeDefaultCategories() {
        // Electronics root category
        Category electronics = new Category("Electronics", "Electronic devices and gadgets");
        electronics.getSpecifications().addAll(Arrays.asList("Brand", "Model", "Warranty"));
        save(electronics);
        
        // Electronics subcategories
        Category smartphones = new Category("Smartphones", "Mobile phones and accessories", electronics);
        smartphones.getSpecifications().addAll(Arrays.asList("Screen Size", "Storage", "RAM", "Camera"));
        save(smartphones);
        
        Category laptops = new Category("Laptops", "Portable computers", electronics);
        laptops.getSpecifications().addAll(Arrays.asList("Processor", "RAM", "Storage", "Screen Size"));
        save(laptops);
        
        // Clothing root category
        Category clothing = new Category("Clothing", "Fashion and apparel");
        clothing.getSpecifications().addAll(Arrays.asList("Size", "Color", "Material"));
        save(clothing);
        
        // Clothing subcategories
        Category mensClothing = new Category("Men's Clothing", "Clothing for men", clothing);
        mensClothing.getSpecifications().addAll(Arrays.asList("Chest Size", "Waist Size"));
        save(mensClothing);
        
        Category womensClothing = new Category("Women's Clothing", "Clothing for women", clothing);
        womensClothing.getSpecifications().addAll(Arrays.asList("Bust Size", "Hip Size"));
        save(womensClothing);
        
        // Home & Garden root category
        Category homeGarden = new Category("Home & Garden", "Home improvement and garden supplies");
        homeGarden.getSpecifications().addAll(Arrays.asList("Dimensions", "Weight", "Material"));
        save(homeGarden);
        
        Category furniture = new Category("Furniture", "Home furniture", homeGarden);
        furniture.getSpecifications().addAll(Arrays.asList("Assembly Required", "Style"));
        save(furniture);
    }
    
    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            category.setId(idGenerator.getAndIncrement());
        }
        categories.put(category.getId(), category);
        return category;
    }
    
    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(categories.get(id));
    }
    
    @Override
    public List<Category> findAll() {
        return new ArrayList<>(categories.values());
    }
    
    @Override
    public void deleteById(Long id) {
        categories.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return categories.containsKey(id);
    }
    
    @Override
    public long count() {
        return categories.size();
    }
    
    @Override
    public List<Category> findRootCategories() {
        return categories.values().stream()
                .filter(Category::isRoot)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Category> findByParent(Category parent) {
        return categories.values().stream()
                .filter(category -> Objects.equals(category.getParent(), parent))
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Category> findByName(String name) {
        return categories.values().stream()
                .filter(category -> category.getName().equals(name))
                .findFirst();
    }
}