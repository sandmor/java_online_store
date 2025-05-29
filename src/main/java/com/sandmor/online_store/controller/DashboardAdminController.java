package com.sandmor.online_store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sandmor.online_store.dto.CategoryStatistics;
import com.sandmor.online_store.model.Category;
import com.sandmor.online_store.model.Order;
import com.sandmor.online_store.model.OrderStatus;
import com.sandmor.online_store.service.CategoryService;
import com.sandmor.online_store.service.OrderService;
import com.sandmor.online_store.service.ProductService;
import com.sandmor.online_store.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class DashboardAdminController extends BaseAdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        // Dashboard statistics
        long totalUsers = userService.findAll().size();
        long totalProducts = productService.findAll().size();
        long totalOrders = orderService.findAll().size();
        long pendingOrders = orderService.findByStatus(OrderStatus.PENDING).size();
        long processedOrders = orderService.findByStatus(OrderStatus.PROCESSED).size();
        long canceledOrders = orderService.findByStatus(OrderStatus.CANCELED).size();
        
        // Calculate total revenue
        List<Order> allOrders = orderService.findAll();
        double totalRevenue = allOrders.stream()
            .filter(order -> order.getStatus() == OrderStatus.PROCESSED)
            .mapToDouble(order -> order.getTotalAmount().doubleValue())
            .sum();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("processedOrders", processedOrders);
        model.addAttribute("canceledOrders", canceledOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        
        // Recent orders - use service method
        List<Order> recentOrders = orderService.findRecentOrders(5);
        model.addAttribute("recentOrders", recentOrders);
        
        // Top categories with product counts
        List<Category> allCategories = categoryService.findAll();
        List<CategoryStatistics> topCategories = allCategories.stream()
            .map(category -> new CategoryStatistics(category, productService.countActiveByCategoryId(category.getId())))
            .filter(stats -> stats.getProductCount() > 0)
            .sorted((a, b) -> Long.compare(b.getProductCount(), a.getProductCount()))
            .limit(5)
            .collect(java.util.stream.Collectors.toList());
        model.addAttribute("topCategories", topCategories);
        
        return "admin/dashboard";
    }
}
