package com.sandmor.online_store.model;

public enum UserRole {
    ADMIN("Administrator"),
    CUSTOMER("Customer");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}