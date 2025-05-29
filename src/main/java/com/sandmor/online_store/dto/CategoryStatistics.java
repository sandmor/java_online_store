package com.sandmor.online_store.dto;

import com.sandmor.online_store.model.Category;

public class CategoryStatistics {
    private Category category;
    private long productCount;
    
    public CategoryStatistics() {}
    
    public CategoryStatistics(Category category, long productCount) {
        this.category = category;
        this.productCount = productCount;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public long getProductCount() {
        return productCount;
    }
    
    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }
    
    public String getName() {
        return category != null ? category.getName() : "";
    }
    
    public Long getId() {
        return category != null ? category.getId() : null;
    }
}
