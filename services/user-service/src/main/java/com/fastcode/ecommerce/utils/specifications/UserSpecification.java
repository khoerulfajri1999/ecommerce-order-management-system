package com.fastcode.ecommerce.utils.specifications;

import com.fastcode.ecommerce.model.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {

    public static Specification<User> getSpecification(String search) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(search)) {
                return cb.conjunction();
            }
            String searchTerm = "%" + search.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("fullName")), searchTerm);
        };
    }
}
