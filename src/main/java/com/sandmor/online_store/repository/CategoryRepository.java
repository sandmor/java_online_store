package com.sandmor.online_store.repository;

import java.util.List;
import java.util.Optional;

import com.sandmor.online_store.model.Category;

public interface CategoryRepository extends BaseRepository<Category, Long> {
    List<Category> findRootCategories();
    List<Category> findByParent(Category parent);
    Optional<Category> findByName(String name);
}