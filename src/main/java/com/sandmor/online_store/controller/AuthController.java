package com.sandmor.online_store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sandmor.online_store.model.User;
import com.sandmor.online_store.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password, 
                       HttpSession session, 
                       Model model) {
        try {
            User user = userService.authenticate(username, password);
            session.setAttribute("currentUser", user);
            
            if (user.isAdmin()) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid username or password");
            return "auth/login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(User user, Model model) {
        try {
            user.setRole(com.sandmor.online_store.model.UserRole.CUSTOMER);
            userService.createUser(user);
            model.addAttribute("success", "Registration successful! Please login.");
            return "auth/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "auth/register";
        }
    }
}
