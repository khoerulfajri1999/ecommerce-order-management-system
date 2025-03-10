package com.fastcode.ecommerce.service.impl;

import com.fastcode.ecommerce.model.dto.request.CategoryRequest;
import com.fastcode.ecommerce.model.dto.response.CategoryResponse;
import com.fastcode.ecommerce.model.entity.Category;
import com.fastcode.ecommerce.repository.CategoryRepository;
import com.fastcode.ecommerce.service.CategoryService;
import com.fastcode.ecommerce.utils.cache.RedisService;
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
    private final RedisService redisService;

    private static final String CATEGORY_CACHE_PREFIX = "CATEGORY_";
    private static final String CATEGORY_LIST_CACHE = "CATEGORY_LIST";
    private static final int CACHE_TTL = 60;

    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        Category category = categoryMapper.requestToEntity(categoryRequest);
        category = categoryRepository.save(category);

        clearCategoryCache();

        return categoryMapper.entityToResponse(category);
    }

    @Override
    public List<CategoryResponse> getAll() {

        List<CategoryResponse> cachedCategories = (List<CategoryResponse>) redisService.getData(CATEGORY_LIST_CACHE);
        if (cachedCategories != null) {
            return cachedCategories;
        }

        List<Category> categoriesEntity = categoryRepository.findAll();
        List<CategoryResponse> categories = categoriesEntity.stream()
                .map(categoryMapper::entityToResponse)
                .collect(Collectors.toList());

        redisService.saveData(CATEGORY_LIST_CACHE, categories, CACHE_TTL);
        return categories;
    }

    @Override
    public CategoryResponse getById(String id) {
        String cacheKey = CATEGORY_CACHE_PREFIX + id;

        CategoryResponse cachedCategory = (CategoryResponse) redisService.getData(cacheKey);
        if (cachedCategory != null) {
            return cachedCategory;
        }

        Category category = findBidOrThrowNotFound(id);
        CategoryResponse response = categoryMapper.entityToResponse(category);

        redisService.saveData(cacheKey, response, CACHE_TTL);
        return response;
    }

    @Override
    public CategoryResponse updatePut(CategoryRequest categoryRequest) {
        Category existingCategory = findBidOrThrowNotFound(categoryRequest.getId());
        if (categoryRequest.getName() != null) existingCategory.setName(categoryRequest.getName());
        if (categoryRequest.getDescription() != null) existingCategory.setDescription(categoryRequest.getDescription());
        existingCategory = categoryRepository.save(existingCategory);

        clearCategoryCache();

        return categoryMapper.entityToResponse(existingCategory);
    }

    @Override
    public void deleteById(String id) {
        Category category = findBidOrThrowNotFound(id);
        categoryRepository.delete(category);

        clearCategoryCache();
    }

    private Category findBidOrThrowNotFound(String id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category not found")
        );
    }

    private void clearCategoryCache() {
        redisService.deleteData(CATEGORY_LIST_CACHE);
    }
}
