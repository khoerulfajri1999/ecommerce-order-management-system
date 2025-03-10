package com.fastcode.ecommerce.service.impl;

import com.fastcode.ecommerce.constant.UserRole;
import com.fastcode.ecommerce.model.entity.Role;
import com.fastcode.ecommerce.repository.RoleRepository;
import com.fastcode.ecommerce.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getOrSave(UserRole role) {
        Optional<Role> optionalRole = roleRepository.findByRole(role);
        if (optionalRole.isPresent()){
            return optionalRole.get();
        }
        Role currentRole = Role.builder()
                .role(role)
                .build();
        return roleRepository.saveAndFlush(currentRole);
    }
}
