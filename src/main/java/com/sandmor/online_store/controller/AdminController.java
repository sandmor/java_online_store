package com.sandmor.online_store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseAdminController {
    
    /**
     * Redirect admin root to dashboard
     */
    @GetMapping
    public String adminRoot(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        return "redirect:/admin/dashboard";
    }
}
