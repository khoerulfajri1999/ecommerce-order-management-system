package com.fastcode.ecommerce.repository;

import com.fastcode.ecommerce.model.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository <UserAccount, String> {
    Optional<UserAccount> findByUsername(String username);
}
