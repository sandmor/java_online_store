package com.sandmor.online_store.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sandmor.online_store.model.User;
import com.sandmor.online_store.service.AdminHelperService;

import jakarta.servlet.http.HttpSession;

/**
 * Base controller class with common admin functionality to eliminate duplication
 */
public abstract class BaseAdminController {
    
    /**
     * Check if user is admin (helper method)
     */
    protected boolean isAdmin(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * Get current user from session
     */
    protected Optional<User> getCurrentUser(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        return Optional.ofNullable(currentUser);
    }
    
    /**
     * Generic method to handle bulk operation success/error messages
     */
    protected void handleBulkOperationResult(String operation, int count, String entityType, 
                                           RedirectAttributes redirectAttributes) {
        if (count > 0) {
            String message = AdminHelperService.generateBulkOperationMessage(operation, count, entityType);
            redirectAttributes.addFlashAttribute("success", message);
        } else {
            redirectAttributes.addFlashAttribute("warning", "No " + entityType + "s were " + operation + ".");
        }
    }
    
    /**
     * Generic method to handle bulk operation errors
     */
    protected void handleBulkOperationError(Exception e, String operation, String entityType, 
                                          RedirectAttributes redirectAttributes) {
        String errorMessage = String.format("Error %s %ss: %s", operation, entityType, e.getMessage());
        redirectAttributes.addFlashAttribute("error", errorMessage);
    }
    
    /**
     * Redirect to login if not admin
     */
    protected String redirectIfNotAdmin(HttpSession session) {
        return isAdmin(session) ? null : "redirect:/login";
    }
    
    /**
     * Generic method to handle form validation errors
     */
    protected <T> boolean handleValidationErrors(BindingResult result, Model model, T entity) {
        if (result.hasErrors()) {
            model.addAttribute("entity", entity);
            return true;
        }
        return false;
    }
    
    /**
     * Generic method to handle entity not found
     */
    protected String handleEntityNotFound(String entityType, Long id, String redirectUrl) {
        return redirectUrl + "?error=" + entityType + " with ID " + id + " not found";
    }
    
    /**
     * Generic method to handle entity not found with redirect attributes
     */
    protected boolean handleEntityNotFound(Optional<?> entity, RedirectAttributes redirectAttributes, 
                                         String entityType, String redirectUrl) {
        if (entity.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", 
                entityType.substring(0, 1).toUpperCase() + entityType.substring(1) + " not found!");
            return true;
        }
        return false;
    }
    
    /**
     * Generic method to handle save operations with supplier
     */
    protected String handleSaveOperation(java.util.function.Supplier<Void> saveOperation, 
                                       String successMessage, String redirectUrl, 
                                       RedirectAttributes redirectAttributes) {
        try {
            saveOperation.get();
            redirectAttributes.addFlashAttribute("success", successMessage);
            return redirectUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving: " + e.getMessage());
            return redirectUrl;
        }
    }
    
    /**
     * Generic method to handle save operations with runnable
     */
    protected String handleSaveOperation(Runnable saveOperation, 
                                       String successMessage, String redirectUrl, 
                                       RedirectAttributes redirectAttributes) {
        try {
            saveOperation.run();
            redirectAttributes.addFlashAttribute("success", successMessage);
            return redirectUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving: " + e.getMessage());
            return redirectUrl;
        }
    }
    
    /**
     * Generic method to handle save operations
     */
    protected void handleSaveSuccess(String operation, String entityType, RedirectAttributes redirectAttributes) {
        String message = String.format("%s %s successfully!", 
            entityType.substring(0, 1).toUpperCase() + entityType.substring(1), operation);
        redirectAttributes.addFlashAttribute("success", message);
    }
    
    /**
     * Generic method to handle save errors
     */
    protected void handleSaveError(Exception e, String operation, String entityType, 
                                 Model model, RedirectAttributes redirectAttributes) {
        String errorMessage = String.format("Error %s %s: %s", operation, entityType, e.getMessage());
        if (model != null) {
            model.addAttribute("error", errorMessage);
        } else {
            redirectAttributes.addFlashAttribute("error", errorMessage);
        }
    }
    
    /**
     * Generic CSV export method with supplier functions
     */
    protected <T> ResponseEntity<String> handleCsvExport(java.util.function.Supplier<List<T>> dataSupplier, 
                                                        java.util.function.Function<List<T>, String> csvBuilder, 
                                                        String filenamePrefix) {
        try {
            List<T> data = dataSupplier.get();
            String csvContent = csvBuilder.apply(data);
            return AdminHelperService.createCsvExportResponse(csvContent, filenamePrefix);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating CSV: " + e.getMessage());
        }
    }
    
    /**
     * Generic CSV export method
     */
    protected ResponseEntity<String> handleCsvExport(String csvContent, String filenamePrefix, 
                                                   HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        return AdminHelperService.createCsvExportResponse(csvContent, filenamePrefix);
    }
    
    /**
     * Generic bulk operation method with return value
     */
    protected String performBulkOperation(List<Long> ids, String entityType, String operation,
                                        BulkOperationHandler handler, RedirectAttributes redirectAttributes, 
                                        String redirectUrl) {
        try {
            AdminHelperService.validateBulkSelection(ids, entityType);
            int count = handler.execute(ids);
            handleBulkOperationResult(operation, count, entityType, redirectAttributes);
        } catch (Exception e) {
            handleBulkOperationError(e, operation, entityType, redirectAttributes);
        }
        return redirectUrl;
    }
    
    /**
     * Generic bulk operation method
     */
    protected void performBulkOperation(List<Long> ids, String entityType, String operation,
                                      BulkOperationHandler handler, RedirectAttributes redirectAttributes) {
        try {
            AdminHelperService.validateBulkSelection(ids, entityType);
            int count = handler.execute(ids);
            handleBulkOperationResult(operation, count, entityType, redirectAttributes);
        } catch (Exception e) {
            handleBulkOperationError(e, operation, entityType, redirectAttributes);
        }
    }
    
    /**
     * Functional interface for bulk operations
     */
    @FunctionalInterface
    protected interface BulkOperationHandler {
        int execute(List<Long> ids) throws Exception;
    }
}
