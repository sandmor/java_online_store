package com.sandmor.online_store.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.sandmor.online_store.model.Category;
import com.sandmor.online_store.model.Product;
import com.sandmor.online_store.model.ShoppingCart;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.service.CategoryService;
import com.sandmor.online_store.service.ProductService;
import com.sandmor.online_store.service.ShoppingCartService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ShoppingCartService cartService;
    
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Product> featuredProducts = productService.findActiveProducts();
        List<Category> rootCategories = categoryService.findRootCategories();
        
        model.addAttribute("products", featuredProducts);
        model.addAttribute("categories", rootCategories);
        
        // Add cart info if user is logged in
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            Optional<ShoppingCart> cart = cartService.findByCustomer(currentUser);
            if (cart.isPresent()) {
                model.addAttribute("cartItemCount", cart.get().getTotalItems());
            }
        }
        
        return "home";
    }
    
    @GetMapping("/products")
    public String products(@RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) String search,
                          Model model, HttpSession session) {
        List<Product> products;
        
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search.trim());
            model.addAttribute("searchTerm", search);
        } else if (categoryId != null) {
            products = productService.findActiveByCategoryAndSubcategories(categoryId);
            Optional<Category> category = categoryService.findById(categoryId);
            category.ifPresent(cat -> model.addAttribute("selectedCategory", cat));
        } else {
            products = productService.findActiveProducts();
        }
        
        List<Category> rootCategories = categoryService.findRootCategories();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", rootCategories);
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            Optional<ShoppingCart> cart = cartService.findByCustomer(currentUser);
            if (cart.isPresent()) {
                model.addAttribute("cartItemCount", cart.get().getTotalItems());
            }
        }
        
        return "products/list";
    }
    
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return "redirect:/products";
        }
        
        model.addAttribute("product", product.get());
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            Optional<ShoppingCart> cart = cartService.findByCustomer(currentUser);
            if (cart.isPresent()) {
                model.addAttribute("cartItemCount", cart.get().getTotalItems());
            }
        }
        
        return "products/detail";
    }
}
