package com.sandmor.online_store.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sandmor.online_store.dto.UserFilterCriteria;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.model.UserRole;
import com.sandmor.online_store.service.AdminHelperService;
import com.sandmor.online_store.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController extends BaseAdminController {

    @Autowired private UserService userService;

    @GetMapping("")
    public String listUsers(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String role,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String sortBy,
                            @RequestParam(required = false) String sortDir,
                            Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // Create filter criteria
        UserFilterCriteria criteria = new UserFilterCriteria();
        criteria.setSearch(search);
        criteria.setRole(role);
        criteria.setStatus(status);
        criteria.setSortBy(sortBy);
        criteria.setSortDirection(sortDir);

        // Get filtered users from service
        List<User> users = userService.findWithFilters(criteria);

        // Add filter parameters to model for form state
        if (criteria.hasSearch()) {
            model.addAttribute("search", search);
        }
        if (criteria.hasRole()) {
            model.addAttribute("selectedRole", role);
        }
        if (criteria.hasStatus()) {
            model.addAttribute("selectedStatus", status);
        }
        if (criteria.hasSorting()) {
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
        }

        // Get statistics
        long totalUsersCount = userService.findAll().size();
        long adminCount = userService.findByRole(UserRole.ADMIN).size();
        long customerCount = userService.findByRole(UserRole.CUSTOMER).size();

        model.addAttribute("users", users);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("totalUsersCount", totalUsersCount);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("customerCount", customerCount);

        // Add current user to model for template access
        User currentUser = (User)session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);

        return "admin/users/list";
    }

    @GetMapping("/new")
    public String newUserForm(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("user", new User());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("isEdit", false);
        return "admin/users/form";
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model,
                               HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        Optional<User> user = userService.findById(id);
        if (handleEntityNotFound(user, null, "user", "/admin/users")) {
            return "redirect:/admin/users";
        }

        User originalUser = user.get();
        
        User userToEdit = new User();
        userToEdit.setId(originalUser.getId());
        userToEdit.setUsername(originalUser.getUsername());
        userToEdit.setEmail(originalUser.getEmail());
        userToEdit.setFirstName(originalUser.getFirstName());
        userToEdit.setLastName(originalUser.getLastName());
        userToEdit.setRole(originalUser.getRole());
        userToEdit.setCreatedAt(originalUser.getCreatedAt());
        userToEdit.setUpdatedAt(originalUser.getUpdatedAt());
        // Set password to empty string for edit form
        userToEdit.setPassword("");

        // Check if user is editing their own account
        User currentUser = (User) session.getAttribute("currentUser");
        boolean isEditingOwnAccount = currentUser != null && currentUser.getId() != null && 
                                    currentUser.getId().equals(id);

        model.addAttribute("user", userToEdit);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("isEditingOwnAccount", isEditingOwnAccount);
        return "admin/users/form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute User user, BindingResult result,
                           Model model, HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // Get current user to prevent role changes to self
        User currentUser = (User) session.getAttribute("currentUser");
        boolean isEditingOwnAccount = currentUser != null && currentUser.getId() != null && 
                                    currentUser.getId().equals(user.getId());

        // Prevent admin from changing their own role
        if (isEditingOwnAccount && currentUser != null && currentUser.isAdmin() && 
            (user.getRole() == null || !user.getRole().equals(UserRole.ADMIN))) {
            handleSaveError(new IllegalArgumentException("You cannot change your own admin role"), 
                          "updating", "user", model, null);
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("isEditingOwnAccount", true);
            return "admin/users/form";
        }

        // Custom validation for edit mode - password is optional
        boolean isEditMode = user.getId() != null;
        boolean isPasswordEmpty =
            user.getPassword() == null || user.getPassword().trim().isEmpty();

        // For edit mode with empty password, we need to check validation errors
        // excluding password errors
        if (isEditMode && isPasswordEmpty) {
            // Check if there are any validation errors other than password
            boolean hasNonPasswordErrors = result.getFieldErrors().stream().anyMatch(
                error -> !"password".equals(error.getField()));

            if (hasNonPasswordErrors) {
                // There are other validation errors, handle them normally
                if (handleValidationErrors(result, model, user)) {
                    model.addAttribute("roles", UserRole.values());
                    model.addAttribute("isEdit", true);
                    model.addAttribute("isEditingOwnAccount", isEditingOwnAccount);
                    return "admin/users/form";
                }
            }
            // If only password errors exist, we can ignore them for edit mode
        } else {
            // For new users or when password is provided, check all validation errors
            // normally
            if (handleValidationErrors(result, model, user)) {
                model.addAttribute("roles", UserRole.values());
                model.addAttribute("isEdit", user.getId() != null);
                model.addAttribute("isEditingOwnAccount", isEditingOwnAccount);
                return "admin/users/form";
            }
        }

        try {
            if (user.getId() == null) {
                userService.createUser(user);
                handleSaveSuccess("created", "user", redirectAttributes);
            } else {
                // For editing, handle password carefully
                if (user.getPassword() == null ||
                    user.getPassword().trim().isEmpty()) {
                    // If password is empty, preserve the existing password
                    user.setPassword(null);
                }
                userService.updateUser(user);
                handleSaveSuccess("updated", "user", redirectAttributes);
            }
        } catch (IllegalArgumentException e) {
            handleSaveError(e, user.getId() == null ? "creating" : "updating",
                            "user", model, null);
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("isEdit", user.getId() != null);
            model.addAttribute("isEditingOwnAccount", isEditingOwnAccount);
            return "admin/users/form";
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // Get current user to prevent self-deletion
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null && currentUser.getId().equals(id)) {
            handleSaveError(new IllegalArgumentException("You cannot delete your own account"), 
                          "deleting", "user", null, redirectAttributes);
            return "redirect:/admin/users";
        }

        try {
            userService.deleteUser(id);
            handleSaveSuccess("deleted", "user", redirectAttributes);
        } catch (IllegalArgumentException e) {
            handleSaveError(e, "deleting", "user", null, redirectAttributes);
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/activate")
    public String activateUser(@PathVariable Long id, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            userService.activateUser(id);
            redirectAttributes.addFlashAttribute("success", "User activated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to activate user: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id, HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        // Check if trying to deactivate self
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null && currentUser.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "You cannot deactivate your own account!");
            return "redirect:/admin/users";
        }
        
        try {
            userService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("success", "User deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to deactivate user: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportUsers(HttpSession session) {
        List<User> users = userService.findAll();
        String csvContent = buildUsersCsv(users);
        return handleCsvExport(csvContent, "users", session);
    }

    /**
     * Build CSV content for users
     */
    private String buildUsersCsv(List<User> users) {
        StringBuilder csvBuilder = new StringBuilder();

        // CSV Headers
        csvBuilder.append("ID,Username,Email,First Name,Last Name,Full " +
                          "Name,Role,Active,Created Date\n");

        // CSV Data
        for (User user : users) {
            csvBuilder.append(escapeCSV(String.valueOf(user.getId()))).append(",");
            csvBuilder.append(escapeCSV(user.getUsername())).append(",");
            csvBuilder.append(escapeCSV(user.getEmail())).append(",");
            csvBuilder.append(escapeCSV(user.getFirstName())).append(",");
            csvBuilder.append(escapeCSV(user.getLastName())).append(",");
            csvBuilder.append(escapeCSV(user.getFullName())).append(",");
            csvBuilder.append(escapeCSV(user.getRole().getDisplayName()))
                .append(",");
            csvBuilder.append(escapeCSV(user.isActive() ? "Yes" : "No"))
                .append(",");
            csvBuilder
                .append(escapeCSV(user.getCreatedAt() != null
                                      ? user.getCreatedAt().toString()
                                      : "N/A"))
                .append("\n");
        }

        return csvBuilder.toString();
    }

    private String escapeCSV(String value) {
        return AdminHelperService.escapeCSV(value);
    }
}
