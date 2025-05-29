package com.sandmor.online_store.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sandmor.online_store.dto.UserFilterCriteria;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.model.UserRole;
import com.sandmor.online_store.repository.UserRepository;
import com.sandmor.online_store.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User createUser(User user) {
        // Check if username or email already exists
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Check if username or email is being changed to an existing one
        User existing = existingUser.get();
        if (!existing.getUsername().equals(user.getUsername()) && existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (!existing.getEmail().equals(user.getEmail()) && existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // If password is null or empty, preserve the existing password (no password change)
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(existing.getPassword());
        }
        
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public User authenticate(String username, String password) {
        Optional<User> user = findByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            if (!user.get().isActive()) {
                throw new IllegalArgumentException("User account is deactivated");
            }
            return user.get();
        }
        throw new IllegalArgumentException("Invalid username or password");
    }
    
    @Override
    public void activateUser(Long id) {
        setUserActiveStatus(id, true);
    }
    
    @Override
    public void deactivateUser(Long id) {
        setUserActiveStatus(id, false);
    }
    
    @Override
    public void setUserActiveStatus(Long id, boolean active) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = userOpt.get();
        user.setActive(active);
        userRepository.save(user);
    }
    
    @Override
    public List<User> findWithFilters(UserFilterCriteria criteria) {
        return userRepository.findWithFilters(criteria);
    }
}
