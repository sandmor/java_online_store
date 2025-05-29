package com.sandmor.online_store.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private Long id;
    private String name;
    private String description;
    private Category parent;
    private List<Category> children;
    private List<String> specifications; // Custom specifications for this category
    
    // Constructors
    public Category() {
        this.children = new ArrayList<>();
        this.specifications = new ArrayList<>();
    }
    
    public Category(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    
    public Category(String name, String description, Category parent) {
        this(name, description);
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Category getParent() {
        return parent;
    }
    
    public void setParent(Category parent) {
        this.parent = parent;
    }
    
    public List<Category> getChildren() {
        return children;
    }
    
    public void setChildren(List<Category> children) {
        this.children = children;
    }
    
    public List<String> getSpecifications() {
        return specifications;
    }
    
    public void setSpecifications(List<String> specifications) {
        this.specifications = specifications;
    }
    
    // Helper methods
    public void addChild(Category child) {
        if (!children.contains(child)) {
            children.add(child);
            child.setParent(this);
        }
    }
    
    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
    }
    
    public boolean isRoot() {
        return parent == null;
    }
    
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " > " + name;
    }
    
    public List<String> getAllSpecifications() {
        List<String> allSpecs = new ArrayList<>(specifications);
        if (parent != null) {
            allSpecs.addAll(parent.getAllSpecifications());
        }
        return allSpecs;
    }
    
    /**
     * Get all descendant categories (subcategories at all levels)
     * @return List of all descendant categories including this category
     */
    public List<Category> getAllDescendants() {
        List<Category> descendants = new ArrayList<>();
        descendants.add(this); // Include the category itself
        for (Category child : children) {
            descendants.addAll(child.getAllDescendants());
        }
        return descendants;
    }
}