package com.sandmor.online_store.repository;

import java.util.List;
import java.util.Optional;

import com.sandmor.online_store.dto.UserFilterCriteria;
import com.sandmor.online_store.model.User;
import com.sandmor.online_store.model.UserRole;

public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    List<User> findWithFilters(UserFilterCriteria criteria);
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
}