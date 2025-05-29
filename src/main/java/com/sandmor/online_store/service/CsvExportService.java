package com.sandmor.online_store.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sandmor.online_store.model.Order;
import com.sandmor.online_store.model.Product;

/**
 * Service class for building CSV exports for different entity types
 */
public class CsvExportService {
    
    /**
     * Build CSV content for products
     */
    public static String buildProductsCsv(List<Product> products) {
        StringBuilder csvBuilder = new StringBuilder();
        
        // CSV Headers
        csvBuilder.append("ID,Name,Description,Price,Stock Quantity,Category,Active,Image URL,Specifications\n");
        
        // CSV Data
        for (Product product : products) {
            csvBuilder.append(AdminHelperService.escapeCSV(String.valueOf(product.getId()))).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(product.getName())).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(product.getDescription())).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(product.getPrice().toString())).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(String.valueOf(product.getStockQuantity()))).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(product.getCategory().getName())).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(String.valueOf(product.isActive()))).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(product.getImageUrl() != null ? product.getImageUrl() : "")).append(",");
            
            // Specifications as JSON-like string
            StringBuilder specBuilder = new StringBuilder();
            if (product.getSpecifications() != null && !product.getSpecifications().isEmpty()) {
                specBuilder.append("{");
                boolean first = true;
                for (java.util.Map.Entry<String, String> spec : product.getSpecifications().entrySet()) {
                    if (!first) specBuilder.append("; ");
                    specBuilder.append(spec.getKey()).append(": \"").append(spec.getValue()).append("\"");
                    first = false;
                }
                specBuilder.append("}");
            }
            csvBuilder.append(AdminHelperService.escapeCSV(specBuilder.toString()));
            csvBuilder.append("\n");
        }
        
        return csvBuilder.toString();
    }
    
    /**
     * Build CSV content for orders
     */
    public static String buildOrdersCsv(List<Order> orders) {
        StringBuilder csvBuilder = new StringBuilder();
        
        // CSV Headers
        csvBuilder.append("Order ID,Order Number,Customer,Email,Status,Total Amount,Items,Order Date,Payment Method,Shipping Address\n");
        
        // CSV Data
        for (Order order : orders) {
            csvBuilder.append(AdminHelperService.escapeCSV(String.valueOf(order.getId()))).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(order.getOrderNumber())).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(order.getCustomer().getUsername())).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(order.getCustomer().getEmail())).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(order.getStatus().name())).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(order.getTotalAmount().toString())).append(",");
            
            // Items summary
            StringBuilder itemsSummary = new StringBuilder();
            for (int i = 0; i < order.getItems().size(); i++) {
                var item = order.getItems().get(i);
                if (i > 0) itemsSummary.append("; ");
                itemsSummary.append(item.getProduct().getName()).append(" x").append(item.getQuantity());
            }
            csvBuilder.append(AdminHelperService.escapeCSV(itemsSummary.toString())).append(",");
            
            csvBuilder.append(AdminHelperService.escapeCSV(order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A")).append(",");
            csvBuilder.append(AdminHelperService.escapeCSV(order.getShippingAddress() != null ? order.getShippingAddress() : "N/A")).append("\n");
        }
        
        return csvBuilder.toString();
    }
}
