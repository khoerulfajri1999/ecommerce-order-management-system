package com.fastcode.ecommerce.repository;

import com.fastcode.ecommerce.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findByUserAccountId(String id);
}
