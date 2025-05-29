package com.sandmor.online_store.service;

import java.util.List;
import java.util.Optional;

import com.sandmor.online_store.model.Category;

public interface CategoryService {
    Category createCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(Long id);
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    List<Category> findAll();
    List<Category> findRootCategories();
    List<Category> findByParent(Category parent);
    List<Category> findChildren(Long parentId);
}
