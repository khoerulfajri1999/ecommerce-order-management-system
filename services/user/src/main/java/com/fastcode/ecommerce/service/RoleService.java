package com.fastcode.ecommerce.service;

import com.fastcode.ecommerce.constant.UserRole;
import com.fastcode.ecommerce.model.entity.Role;

public interface RoleService {
    Role getOrSave(UserRole role);
}
