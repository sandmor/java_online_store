package com.sandmor.online_store.dto;

import java.time.LocalDate;

public class OrderFilterCriteria extends BaseFilterCriteria {
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;
    
    public OrderFilterCriteria() {
        super();
    }
    
    public OrderFilterCriteria(String status, String sortBy, String sortDirection, Integer limit) {
        super(null, sortBy, sortDirection);
        this.status = status;
        this.limit = limit;
    }
    
    public OrderFilterCriteria(String status, String sortBy, String sortDirection, Integer limit, 
                              String search, LocalDate fromDate, LocalDate toDate) {
        super(search, sortBy, sortDirection);
        this.status = status;
        this.limit = limit;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDate getFromDate() {
        return fromDate;
    }
    
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }
    
    public LocalDate getToDate() {
        return toDate;
    }
    
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
    
    // Order-specific utility methods
    public boolean hasStatus() {
        return status != null && !status.isEmpty();
    }
    
    public boolean hasDateRange() {
        return fromDate != null || toDate != null;
    }
    
    public boolean hasFromDate() {
        return fromDate != null;
    }
    
    public boolean hasToDate() {
        return toDate != null;
    }
}
