package com.fastcode.ecommerce.utils.specifications;

import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductSpecification {
    public static Specification<Product> getSpecification(SearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getQuery() != null && !request.getQuery().isEmpty()) {
                if (Objects.equals(request.getSortBy(), "name")) {
                    predicates.add(cb.like(root.get("name"), "%" + request.getQuery() + "%"));
                }

                if (Objects.equals(request.getSortBy(), "description")) {
                    predicates.add(cb.like(root.get("description"), "%" + request.getQuery() + "%"));
                }
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
