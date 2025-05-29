package com.sandmor.online_store.model;

public enum OrderStatus {
    PENDING("Pending"),
    PROCESSED("Processed"),
    CANCELED("Canceled");
    
    private final String displayName;
    
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}