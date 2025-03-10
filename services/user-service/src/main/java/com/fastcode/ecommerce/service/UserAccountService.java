package com.fastcode.ecommerce.service;

import com.fastcode.ecommerce.model.entity.UserAccount;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAccountService extends UserDetailsService {
    UserAccount loadUserById(String id);
}
