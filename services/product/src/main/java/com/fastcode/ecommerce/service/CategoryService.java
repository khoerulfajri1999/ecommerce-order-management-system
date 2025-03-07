package com.fastcode.ecommerce.service;

import com.fastcode.ecommerce.model.dto.request.CategoryRequest;
import com.fastcode.ecommerce.model.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest categoryRequest);
    List<CategoryResponse> getAll();
    CategoryResponse getById(String id);
    CategoryResponse updatePut(CategoryRequest categoryRequest);
    void deleteById(String id);
}
