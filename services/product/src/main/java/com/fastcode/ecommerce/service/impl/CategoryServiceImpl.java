package com.fastcode.ecommerce.service.impl;

import com.fastcode.ecommerce.model.dto.request.CategoryRequest;
import com.fastcode.ecommerce.model.dto.response.CategoryResponse;
import com.fastcode.ecommerce.model.entity.Category;
import com.fastcode.ecommerce.repository.CategoryRepository;
import com.fastcode.ecommerce.service.CategoryService;
import com.fastcode.ecommerce.utils.exceptions.ResourceNotFoundException;
import com.fastcode.ecommerce.utils.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        Category category = categoryMapper.requestToEntity(categoryRequest);
        category = categoryRepository.save(category);
        return categoryMapper.entityToResponse(category);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::entityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getById(String id) {
        Category category = findBidOrThrowNotFound(id);
        return categoryMapper.entityToResponse(category);
    }

    @Override
    public CategoryResponse updatePut(CategoryRequest categoryRequest) {
        Category existingCategory = findBidOrThrowNotFound(categoryRequest.getId());
        existingCategory.setName(categoryRequest.getName());
        existingCategory.setDescription(categoryRequest.getDescription());
        existingCategory = categoryRepository.save(existingCategory);
        return categoryMapper.entityToResponse(existingCategory);

    }

    @Override
    public void deleteById(String id) {
        Category category = findBidOrThrowNotFound(id);
        categoryRepository.delete(category);
    }

    private Category findBidOrThrowNotFound(String id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category not found")
        );
    }
}
