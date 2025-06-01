package com.sandmor.online_store.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sandmor.online_store.model.Order;
import com.sandmor.online_store.model.ShoppingCart;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.service.OrderService;
import com.sandmor.online_store.service.ShoppingCartService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ShoppingCartService cartService;
    
    @GetMapping
    public String listOrders(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        List<Order> orders = orderService.findByCustomer(currentUser);
        model.addAttribute("orders", orders);
        
        long pendingCount = orders.stream().filter(order -> order.getStatus().name().equals("PENDING")).count();
        long processedCount = orders.stream().filter(order -> order.getStatus().name().equals("PROCESSED")).count();
        long canceledCount = orders.stream().filter(order -> order.getStatus().name().equals("CANCELED")).count();
        
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("processedCount", processedCount);
        model.addAttribute("canceledCount", canceledCount);
        
        Optional<ShoppingCart> cart = cartService.findByCustomer(currentUser);
        if (cart.isPresent()) {
            model.addAttribute("cartItemCount", cart.get().getTotalItems());
        }
        
        return "orders/list";
    }
    
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        Optional<Order> order = orderService.findById(id);
        if (order.isEmpty() || !order.get().getCustomer().getId().equals(currentUser.getId())) {
            return "redirect:/orders";
        }
        
        model.addAttribute("order", order.get());
        
        Optional<ShoppingCart> cart = cartService.findByCustomer(currentUser);
        if (cart.isPresent()) {
            model.addAttribute("cartItemCount", cart.get().getTotalItems());
        }
        
        return "orders/detail";
    }
}
