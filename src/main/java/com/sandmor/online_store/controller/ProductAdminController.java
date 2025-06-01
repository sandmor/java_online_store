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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sandmor.online_store.dto.ProductFilterCriteria;
import com.sandmor.online_store.model.Category;
import com.sandmor.online_store.model.Product;
import com.sandmor.online_store.service.CategoryService;
import com.sandmor.online_store.service.CsvExportService;
import com.sandmor.online_store.service.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/products")
public class ProductAdminController extends BaseAdminController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public String listProducts(@RequestParam(required = false) String search,
                              @RequestParam(required = false) Long category,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String sortBy,
                              @RequestParam(required = false) String sortDir,
                              Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        ProductFilterCriteria criteria = new ProductFilterCriteria();
        criteria.setSearch(search);
        criteria.setCategoryId(category);
        criteria.setStatus(status);
        criteria.setSortBy(sortBy);
        criteria.setSortDirection(sortDir);
        
        List<Product> products = productService.findWithFilters(criteria);
        List<Category> categories = categoryService.findAll();
        
        if (criteria.hasSearch()) {
            model.addAttribute("searchTerm", search);
        }
        if (criteria.hasCategory()) {
            model.addAttribute("selectedCategory", category);
        }
        if (criteria.hasStatus()) {
            model.addAttribute("selectedStatus", status);
        }
        if (criteria.hasSorting()) {
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
        }
        
        long totalProductsCount = productService.findAll().size();
        long inStockCount = productService.countInStock();
        long lowStockCount = productService.countLowStock();
        long outOfStockCount = productService.countOutOfStock();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalProductsCount", totalProductsCount);
        model.addAttribute("inStockCount", inStockCount);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("outOfStockCount", outOfStockCount);
        
        return "admin/products/list";
    }
    
    @GetMapping("/new")
    public String newProductForm(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/form";
    }
    
    @GetMapping("/edit")
    public String editProductForm(@RequestParam Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/admin/products";
        }
        
        model.addAttribute("product", product.get());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/products/form";
    }
    
    @GetMapping("/view")
    public String viewProduct(@RequestParam Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Product not found!");
            return "redirect:/admin/products";
        }
        
        model.addAttribute("product", product.get());
        return "admin/products/detail";
    }
    
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute Product product, 
                             BindingResult result, 
                             Model model, 
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        // Handle category lookup if category.id is provided but category object is not complete
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Optional<Category> categoryOpt = categoryService.findById(product.getCategory().getId());
            if (categoryOpt.isPresent()) {
                product.setCategory(categoryOpt.get());
            } else {
                result.rejectValue("category", "invalid.category", "Invalid category selected");
            }
        }
        
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "admin/products/form";
        }
        
        try {
            boolean isCreate = product.getId() == null;
            if (isCreate) {
                productService.createProduct(product);
                redirectAttributes.addFlashAttribute("success", "Product created successfully!");
            } else {
                productService.updateProduct(product);
                redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
            }
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Error saving product: " + e.getMessage());
            model.addAttribute("categories", categoryService.findAll());
            return "admin/products/form";
        }
    }
    
    @PostMapping("/delete")
    public String deleteProduct(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        
        return "redirect:/admin/products";
    }
    
    @PostMapping("/bulk-update-stock")
    public String bulkUpdateStock(@RequestParam("productIds") List<Long> productIds,
                                 @RequestParam("newStock") Integer newStock,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        return performBulkOperation(
            productIds,
            "product",
            "updated stock for",
            (ids) -> {
                int count = 0;
                for (Long productId : ids) {
                    productService.updateStock(productId, newStock);
                    count++;
                }
                return count;
            },
            redirectAttributes,
            "redirect:/admin/products"
        );
    }
    
    @PostMapping("/bulk-update-category")
    public String bulkUpdateCategory(@RequestParam("productIds") List<Long> productIds,
                                   @RequestParam("categoryId") Long categoryId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        Optional<Category> category = categoryService.findById(categoryId);
        if (category.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Invalid category selected!");
            return "redirect:/admin/products";
        }
        
        return performBulkOperation(
            productIds,
            "product",
            "updated category for",
            (ids) -> {
                int count = 0;
                for (Long productId : ids) {
                    Optional<Product> product = productService.findById(productId);
                    if (product.isPresent()) {
                        Product p = product.get();
                        p.setCategory(category.get());
                        productService.updateProduct(p);
                        count++;
                    }
                }
                return count;
            },
            redirectAttributes,
            "redirect:/admin/products"
        );
    }
    
    @PostMapping("/bulk-delete")
    public String bulkDeleteProducts(@RequestParam("productIds") List<Long> productIds,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        return performBulkOperation(
            productIds,
            "product",
            "deleted",
            (ids) -> {
                int count = 0;
                for (Long productId : ids) {
                    productService.deleteProduct(productId);
                    count++;
                }
                return count;
            },
            redirectAttributes,
            "redirect:/admin/products"
        );
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportProducts(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).body("Access denied");
        }
        
        return handleCsvExport(
            () -> productService.findAll(),
            (products) -> CsvExportService.buildProductsCsv(products),
            "products"
        );
    }
}
