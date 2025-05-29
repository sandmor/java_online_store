package com.sandmor.online_store.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Generic service for common admin operations to eliminate code duplication
 */
public class AdminHelperService {
    
    /**
     * Generic method to create CSV export response
     */
    public static ResponseEntity<String> createCsvExportResponse(String csvContent, String filenamePrefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = filenamePrefix + "_export_" + timestamp + ".csv";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
    }
    
    /**
     * Generic method to escape CSV values
     */
    public static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        // Escape double quotes by doubling them and wrap in quotes if contains comma, quote, or newline
        if (value.contains("\"") || value.contains(",") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
    
    /**
     * Generic method to perform bulk operations and generate success message
     */
    public static String generateBulkOperationMessage(String operation, int count, String entityType) {
        return String.format("Successfully %s %d %s%s!", 
            operation, count, entityType, count != 1 ? "s" : "");
    }
    
    /**
     * Generic method to validate bulk operation selection
     */
    public static void validateBulkSelection(List<Long> ids, String entityType) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("No " + entityType + "s selected for bulk operation");
        }
    }
}
