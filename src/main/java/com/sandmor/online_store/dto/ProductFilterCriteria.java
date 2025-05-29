package com.sandmor.online_store.dto;

public class ProductFilterCriteria extends BaseFilterCriteria {
    private Long categoryId;
    private String status; // active, inactive, in-stock, low-stock, out-of-stock
    
    public ProductFilterCriteria() {
        super();
    }
    
    public ProductFilterCriteria(String search, Long categoryId, String status, String sortBy, String sortDirection) {
        super(search, sortBy, sortDirection);
        this.categoryId = categoryId;
        this.status = status;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Product-specific utility methods
    public boolean hasCategory() {
        return categoryId != null;
    }
    
    public boolean hasStatus() {
        return status != null && !status.isEmpty();
    }
}
