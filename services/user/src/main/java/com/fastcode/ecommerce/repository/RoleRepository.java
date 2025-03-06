package com.fastcode.ecommerce.repository;

import com.fastcode.ecommerce.constant.UserRole;
import com.fastcode.ecommerce.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByRole(UserRole role);
}
