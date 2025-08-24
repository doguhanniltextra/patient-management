package com.project.auth_service.repository;

import com.project.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByName(String name);
    Optional<User> findByName(String name);
}
