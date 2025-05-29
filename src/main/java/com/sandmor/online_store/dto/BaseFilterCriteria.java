package com.sandmor.online_store.dto;

/**
 * Base class for filter criteria to eliminate duplication between different entity filters
 */
public abstract class BaseFilterCriteria {
    protected String search;
    protected String sortBy;
    protected String sortDirection;
    protected Integer limit;
    
    public BaseFilterCriteria() {}
    
    public BaseFilterCriteria(String search, String sortBy, String sortDirection) {
        this.search = search;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
    
    // Common getters and setters
    public String getSearch() {
        return search;
    }
    
    public void setSearch(String search) {
        this.search = search;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    // Common utility methods
    public boolean hasSearch() {
        return search != null && !search.trim().isEmpty();
    }
    
    public boolean hasSorting() {
        return sortBy != null && !sortBy.isEmpty();
    }
    
    public boolean isDescending() {
        return "desc".equalsIgnoreCase(sortDirection);
    }
    
    public boolean hasLimit() {
        return limit != null && limit > 0;
    }
}
