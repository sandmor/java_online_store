package com.sandmor.online_store.service;

import java.util.List;
import java.util.Optional;

import com.sandmor.online_store.dto.UserFilterCriteria;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.model.UserRole;

public interface UserService {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findByRole(UserRole role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User authenticate(String username, String password);
    
    // User activation/deactivation methods
    void activateUser(Long id);
    void deactivateUser(Long id);
    void setUserActiveStatus(Long id, boolean active);
    
    // Enhanced filtering and sorting methods
    List<User> findWithFilters(UserFilterCriteria criteria);
}
