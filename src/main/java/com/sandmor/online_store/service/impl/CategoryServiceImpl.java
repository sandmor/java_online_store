package com.sandmor.online_store.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandmor.online_store.model.Category;
import com.sandmor.online_store.repository.CategoryRepository;
import com.sandmor.online_store.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    @Override
    public Category updateCategory(Category category) {
        if (!categoryRepository.existsById(category.getId())) {
            throw new IllegalArgumentException("Category not found");
        }
        return categoryRepository.save(category);
    }
    
    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
    
    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Override
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    
    @Override
    public List<Category> findRootCategories() {
        return categoryRepository.findRootCategories();
    }
    
    @Override
    public List<Category> findByParent(Category parent) {
        return categoryRepository.findByParent(parent);
    }
    
    @Override
    public List<Category> findChildren(Long parentId) {
        Optional<Category> parent = categoryRepository.findById(parentId);
        if (parent.isPresent()) {
            return categoryRepository.findByParent(parent.get());
        }
        return List.of();
    }
}
