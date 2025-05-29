package com.sandmor.online_store.dto;

public class UserFilterCriteria {
    private String search;
    private String role;
    private String status; // active, inactive, all
    private String sortBy; // username, email, role, createdDate
    private String sortDirection; // asc, desc
    
    public UserFilterCriteria() {}
    
    public UserFilterCriteria(String search, String role, String sortBy, String sortDirection) {
        this.search = search;
        this.role = role;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
    
    public UserFilterCriteria(String search, String role, String status, String sortBy, String sortDirection) {
        this.search = search;
        this.role = role;
        this.status = status;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
    
    public String getSearch() {
        return search;
    }
    
    public void setSearch(String search) {
        this.search = search;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public boolean hasSearch() {
        return search != null && !search.trim().isEmpty();
    }
    
    public boolean hasRole() {
        return role != null && !role.isEmpty();
    }
    
    public boolean hasStatus() {
        return status != null && !status.isEmpty();
    }
    
    public boolean hasSorting() {
        return sortBy != null && !sortBy.isEmpty();
    }
    
    public boolean isDescending() {
        return "desc".equalsIgnoreCase(sortDirection);
    }
}
