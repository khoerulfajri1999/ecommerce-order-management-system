package com.fastcode.ecommerce.utils.mapper;

import com.fastcode.ecommerce.model.dto.request.CategoryRequest;
import com.fastcode.ecommerce.model.dto.response.CategoryResponse;
import com.fastcode.ecommerce.model.entity.Category;

public interface CategoryMapper {
    CategoryResponse entityToResponse(Category category);
    Category requestToEntity(CategoryRequest categoryRequest);
}
