package com.fastcode.ecommerce.utils.mapper.impl;

import com.fastcode.ecommerce.model.dto.request.CategoryRequest;
import com.fastcode.ecommerce.model.dto.response.CategoryResponse;
import com.fastcode.ecommerce.model.entity.Category;
import com.fastcode.ecommerce.utils.mapper.CategoryMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapperImpl implements CategoryMapper {
    @Override
    public CategoryResponse entityToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    public Category requestToEntity(CategoryRequest categoryRequest) {
        return Category.builder()
                .id(categoryRequest.getId())
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
    }
}
