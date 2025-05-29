package com.sandmor.online_store.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sandmor.online_store.dto.OrderFilterCriteria;
import com.sandmor.online_store.model.Order;
import com.sandmor.online_store.model.OrderStatus;
import com.sandmor.online_store.service.CsvExportService;
import com.sandmor.online_store.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/orders")
public class OrderAdminController extends BaseAdminController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public String listOrders(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String sortBy,
                            @RequestParam(required = false) String sortDir,
                            @RequestParam(required = false) String fromDate,
                            @RequestParam(required = false) String toDate,
                            Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        // Create filter criteria
        OrderFilterCriteria criteria = new OrderFilterCriteria();
        criteria.setSearch(search);
        criteria.setStatus(status);
        criteria.setSortBy(sortBy);
        criteria.setSortDirection(sortDir);
        
        // Parse date parameters
        if (fromDate != null && !fromDate.trim().isEmpty()) {
            try {
                criteria.setFromDate(java.time.LocalDate.parse(fromDate));
            } catch (Exception e) {
                // Invalid date format, ignore
            }
        }
        
        if (toDate != null && !toDate.trim().isEmpty()) {
            try {
                criteria.setToDate(java.time.LocalDate.parse(toDate));
            } catch (Exception e) {
                // Invalid date format, ignore
            }
        }
        
        // Get filtered orders from service
        List<Order> orders = orderService.findWithFilters(criteria);
        
        // Add filter parameters to model for form state
        if (criteria.hasSearch()) {
            model.addAttribute("searchTerm", search);
        }
        if (criteria.hasStatus()) {
            model.addAttribute("selectedStatus", status);
        }
        if (criteria.hasSorting()) {
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
        }
        if (criteria.hasFromDate()) {
            model.addAttribute("fromDate", fromDate);
        }
        if (criteria.hasToDate()) {
            model.addAttribute("toDate", toDate);
        }
        
        // Calculate statistics manually from the orders
        List<Order> allOrders = orderService.findAll();
        long totalOrders = allOrders.size();
        long pendingCount = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long processedCount = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.PROCESSED).count();
        long canceledCount = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELED).count();
        double totalRevenue = allOrders.stream()
            .filter(o -> o.getStatus() == OrderStatus.PROCESSED)
            .mapToDouble(o -> o.getTotalAmount().doubleValue())
            .sum();
        
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("processedCount", processedCount);
        model.addAttribute("canceledCount", canceledCount);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("orderStatuses", OrderStatus.values());
        
        return "admin/orders/list";
    }
    
    @GetMapping("/detail")
    public String viewOrder(@RequestParam Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        Optional<Order> order = orderService.findById(id);
        if (order.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Order not found!");
            return "redirect:/admin/orders";
        }
        
        model.addAttribute("order", order.get());
        model.addAttribute("orderStatuses", OrderStatus.values());
        return "admin/orders/detail";
    }
    
    @GetMapping("/{id}")
    public String viewOrderById(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        return viewOrder(id, model, session, redirectAttributes);
    }
    
    @PostMapping("/status")
    public String updateOrderStatus(@RequestParam Long id, 
                                   @RequestParam String status,
                                   @RequestParam(required = false) String redirectUrl,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Order not found!");
                return "redirect:/admin/orders";
            }
            
            Order order = orderOpt.get();
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            OrderStatus currentStatus = order.getStatus();
            
            // Allow more flexible status transitions
            if (newStatus == OrderStatus.PROCESSED && currentStatus == OrderStatus.PENDING) {
                orderService.processOrder(id);
                redirectAttributes.addFlashAttribute("success", "Order processed successfully!");
            } else if (newStatus == OrderStatus.CANCELED && (currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.PROCESSED)) {
                orderService.cancelOrder(id);
                redirectAttributes.addFlashAttribute("success", "Order cancelled successfully!");
            } else if (newStatus == OrderStatus.PENDING && (currentStatus == OrderStatus.PROCESSED || currentStatus == OrderStatus.CANCELED)) {
                // Direct status update for reverting to pending
                order.setStatus(OrderStatus.PENDING);
                orderService.updateOrder(order);
                redirectAttributes.addFlashAttribute("success", "Order status updated to pending!");
            } else if (newStatus == currentStatus) {
                redirectAttributes.addFlashAttribute("info", "Order status is already " + newStatus.name().toLowerCase() + "!");
            } else {
                redirectAttributes.addFlashAttribute("warning", "Status change from " + currentStatus.name() + " to " + newStatus.name() + " is not allowed!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order status: " + e.getMessage());
        }
        
        // Use provided redirect URL or default to orders list
        String redirectPath = redirectUrl != null && !redirectUrl.trim().isEmpty() ? redirectUrl : "/admin/orders";
        return "redirect:" + redirectPath;
    }
    
    @PostMapping("/process")
    public String processOrder(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            orderService.processOrder(id);
            redirectAttributes.addFlashAttribute("success", "Order processed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing order: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/detail?id=" + id;
    }
    
    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling order: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/detail?id=" + id;
    }
    
    @PostMapping("/bulk-status")
    public String bulkUpdateStatus(@RequestParam("orderIds") List<Long> orderIds,
                                  @RequestParam("status") String status,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return performBulkOperation(
                orderIds,
                "order",
                "updated status for",
                (ids) -> {
                    int count = 0;
                    for (Long orderId : ids) {
                        Optional<Order> orderOpt = orderService.findById(orderId);
                        if (orderOpt.isPresent()) {
                            Order order = orderOpt.get();
                            OrderStatus currentStatus = order.getStatus();
                            
                            try {
                                if (orderStatus == OrderStatus.PROCESSED && currentStatus == OrderStatus.PENDING) {
                                    orderService.processOrder(orderId);
                                    count++;
                                } else if (orderStatus == OrderStatus.CANCELED && (currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.PROCESSED)) {
                                    orderService.cancelOrder(orderId);
                                    count++;
                                } else if (orderStatus == OrderStatus.PENDING && (currentStatus == OrderStatus.PROCESSED || currentStatus == OrderStatus.CANCELED)) {
                                    order.setStatus(OrderStatus.PENDING);
                                    orderService.updateOrder(order);
                                    count++;
                                } else if (orderStatus != currentStatus) {
                                    // For other transitions, just update the status directly
                                    order.setStatus(orderStatus);
                                    orderService.updateOrder(order);
                                    count++;
                                }
                            } catch (Exception e) {
                                // Log error but continue with other orders
                                System.err.println("Error updating order " + orderId + ": " + e.getMessage());
                            }
                        }
                    }
                    return count;
                },
                redirectAttributes,
                "redirect:/admin/orders"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating order status: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/bulk-delete")
    public String bulkDeleteOrders(@RequestParam("orderIds") List<Long> orderIds,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        return performBulkOperation(
            orderIds,
            "order",
            "deleted",
            (ids) -> {
                int count = 0;
                for (Long id : ids) {
                    try {
                        if (orderService.findById(id).isPresent()) {
                            orderService.deleteById(id);
                            count++;
                        }
                    } catch (Exception e) {
                        // Log error but continue with other orders
                        System.err.println("Error deleting order " + id + ": " + e.getMessage());
                    }
                }
                return count;
            },
            redirectAttributes,
            "redirect:/admin/orders"
        );
    }
    
    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            Optional<Order> order = orderService.findById(id);
            if (order.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Order not found!");
                return "redirect:/admin/orders";
            }
            
            orderService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Order deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting order: " + e.getMessage());
        }
        
        return "redirect:/admin/orders";
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportOrders(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        
        return handleCsvExport(
            () -> orderService.findAll(),
            (orders) -> CsvExportService.buildOrdersCsv(orders),
            "orders"
        );
    }
}
