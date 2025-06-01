package com.sandmor.online_store.repository.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.sandmor.online_store.dto.UserFilterCriteria;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.model.UserRole;
import com.sandmor.online_store.repository.UserRepository;

@Repository
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public InMemoryUserRepository() {
        // Initialize with default admin user
        User admin = new User("admin", "admin@example.com", "admin123", 
                             "Admin", "User", UserRole.ADMIN);
        admin.setId(idGenerator.getAndIncrement());
        users.put(admin.getId(), admin);
        
        // Initialize with default customer user
        User customer = new User("customer", "customer@example.com", "customer123", 
                                "John", "Doe", UserRole.CUSTOMER);
        customer.setId(idGenerator.getAndIncrement());
        users.put(customer.getId(), customer);
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
    
    @Override
    public long count() {
        return users.size();
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
    
    @Override
    public List<User> findByRole(UserRole role) {
        return users.values().stream()
                .filter(user -> user.getRole() == role)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return users.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
    
    @Override
    public List<User> findWithFilters(UserFilterCriteria criteria) {
        List<User> result = new ArrayList<>(users.values());
        
        // Apply search filter
        if (criteria.hasSearch()) {
            String searchTerm = criteria.getSearch().toLowerCase();
            result = result.stream()
                    .filter(user -> 
                        user.getUsername().toLowerCase().contains(searchTerm) ||
                        user.getEmail().toLowerCase().contains(searchTerm) ||
                        user.getFirstName().toLowerCase().contains(searchTerm) ||
                        user.getLastName().toLowerCase().contains(searchTerm) ||
                        user.getFullName().toLowerCase().contains(searchTerm))
                    .collect(Collectors.toList());
        }
        
        // Apply role filter
        if (criteria.hasRole()) {
            try {
                UserRole roleFilter = UserRole.valueOf(criteria.getRole().toUpperCase());
                result = result.stream()
                        .filter(user -> user.getRole() == roleFilter)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid role, ignore filter
            }
        }
        
        if (criteria.hasStatus()) {
            String statusFilter = criteria.getStatus().toLowerCase();
            switch (statusFilter) {
                case "active":
                    result = result.stream()
                            .filter(User::isActive)
                            .collect(Collectors.toList());
                    break;
                case "inactive":
                    result = result.stream()
                            .filter(user -> !user.isActive())
                            .collect(Collectors.toList());
                    break;
                case "all":
                default:
                    // No filtering by status, show all users
                    break;
            }
        }
        
        if (criteria.hasSorting()) {
            Comparator<User> comparator;
            
            switch (criteria.getSortBy().toLowerCase()) {
                case "username":
                    comparator = Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "email":
                    comparator = Comparator.comparing(User::getEmail, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "firstname":
                case "first_name":
                    comparator = Comparator.comparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "lastname":
                case "last_name":
                    comparator = Comparator.comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "fullname":
                case "full_name":
                    comparator = Comparator.comparing(User::getFullName, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "role":
                    comparator = Comparator.comparing(user -> user.getRole().name());
                    break;
                case "id":
                default:
                    comparator = Comparator.comparing(User::getId);
                    break;
            }
            
            if (criteria.isDescending()) {
                comparator = comparator.reversed();
            }
            
            result.sort(comparator);
        }
        
        return result;
    }
    
    @Override
    public List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email) {
        return users.values().stream()
                .filter(user -> 
                    (username != null && user.getUsername().toLowerCase().contains(username.toLowerCase())) ||
                    (email != null && user.getEmail().toLowerCase().contains(email.toLowerCase())))
                .collect(Collectors.toList());
    }
}