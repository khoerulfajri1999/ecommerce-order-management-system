package com.fastcode.ecommerce.service.impl;

import com.fastcode.ecommerce.model.entity.UserAccount;
import com.fastcode.ecommerce.repository.UserAccountRepository;
import com.fastcode.ecommerce.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccount loadUserById(String id) {
        return userAccountRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
    }

}
