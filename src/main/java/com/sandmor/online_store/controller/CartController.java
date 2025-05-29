package com.sandmor.online_store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sandmor.online_store.model.Order;
import com.sandmor.online_store.model.ShoppingCart;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.service.OrderService;
import com.sandmor.online_store.service.ShoppingCartService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private ShoppingCartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        ShoppingCart cart = cartService.getOrCreateCartForUser(currentUser);
        model.addAttribute("cart", cart);
        model.addAttribute("cartItemCount", cart.getTotalItems());
        
        return "cart/view";
    }
    
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, 
                           @RequestParam(defaultValue = "1") Integer quantity,
                           HttpSession session, 
                           RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.addProductToCart(currentUser, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Product added to cart!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/products/" + productId;
    }
    
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId, 
                                @RequestParam Integer quantity,
                                HttpSession session, 
                                RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            cartService.updateProductQuantity(currentUser, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Cart updated!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId, 
                                HttpSession session, 
                                RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        cartService.removeProductFromCart(currentUser, productId);
        redirectAttributes.addFlashAttribute("success", "Product removed from cart!");
        
        return "redirect:/cart";
    }
    
    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        cartService.clearCart(currentUser);
        redirectAttributes.addFlashAttribute("success", "Cart cleared successfully!");
        
        return "redirect:/cart";
    }
    
    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        ShoppingCart cart = cartService.getOrCreateCartForUser(currentUser);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        
        model.addAttribute("cart", cart);
        model.addAttribute("cartItemCount", cart.getTotalItems());
        model.addAttribute("currentUser", currentUser);
        
        return "cart/checkout";
    }
    
    @PostMapping("/place-order")
    public String placeOrder(@RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam String email,
                            @RequestParam(required = false) String phone,
                            @RequestParam String address,
                            @RequestParam String city,
                            @RequestParam String state,
                            @RequestParam String zipCode,
                            @RequestParam String cardNumber,
                            @RequestParam String expiryMonth,
                            @RequestParam String expiryYear,
                            @RequestParam String cvv,
                            @RequestParam String cardName,
                            @RequestParam(required = false) String notes,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            ShoppingCart cart = cartService.getOrCreateCartForUser(currentUser);
            if (cart.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cart is empty!");
                return "redirect:/cart";
            }
            
            // Build shipping address from form fields
            String shippingAddress = String.format("%s, %s, %s %s", address, city, state, zipCode);
            
            // Build expiry date from month and year
            String expiryDate = expiryMonth + "/" + expiryYear;
            
            // Create order
            Order order = orderService.createOrderFromCart(currentUser, cart, "Credit Card", shippingAddress);
            
            // Process payment
            Order processedOrder = orderService.processPayment(order, cardNumber, cvv, expiryDate);
            
            redirectAttributes.addFlashAttribute("success", "Order placed successfully! Order number: " + processedOrder.getOrderNumber());
            return "redirect:/orders";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to place order: " + e.getMessage());
            return "redirect:/cart/checkout";
        }
    }
}
