package com.fastcode.ecommerce.controller;

import com.fastcode.ecommerce.constant.APIUrl;
import com.fastcode.ecommerce.model.dto.request.CategoryRequest;
import com.fastcode.ecommerce.model.dto.response.CategoryResponse;
import com.fastcode.ecommerce.model.dto.response.CommonResponse;
import com.fastcode.ecommerce.service.CategoryService;
import com.fastcode.ecommerce.utils.cache.RedisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final RedisService redisService;

    private static final String CATEGORY_CACHE_PREFIX = "CATEGORY_";
    private static final String CATEGORY_LIST_CACHE = "CATEGORY_LIST";
    private static final int CACHE_TTL = 60;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<CategoryResponse>> addNewCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.create(request);

        clearCategoryCache();

        CommonResponse<CategoryResponse> response = CommonResponse.<CategoryResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("New category added")
                .data(category)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getAllCategory() {

        List<CategoryResponse> categories = categoryService.getAll();

        CommonResponse<List<CategoryResponse>> response = CommonResponse.<List<CategoryResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All categories retrieved")
                .data(categories)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }


    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<CategoryResponse>> updateCategory(@Valid @RequestBody CategoryRequest payload) {
        CategoryResponse category = categoryService.updatePut(payload);
        clearCategoryCache();
        CommonResponse<CategoryResponse> response = CommonResponse.<CategoryResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Category with ID " + payload.getId() + " updated successfully.")
                .data(category)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponse>> getCategoryById (@PathVariable String id) {
        String cacheKey = CATEGORY_CACHE_PREFIX + id;

        CategoryResponse cachedCategory = (CategoryResponse) redisService.getData(cacheKey);
        if (cachedCategory != null) {
            return ResponseEntity.ok(CommonResponse.<CategoryResponse>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Category retrieved (from cache)")
                    .data(cachedCategory)
                    .build());
        }

        CategoryResponse category = categoryService.getById(id);
        redisService.saveData(cacheKey, category, CACHE_TTL);

        CommonResponse<CategoryResponse> response = CommonResponse.<CategoryResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Category retrieved")
                .data(category)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<String>> deleteById(@PathVariable String id){
        categoryService.deleteById(id);
        clearCategoryCache();

        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Category deleted successfully")
                .data("Category with ID: " + id + " has been deleted")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    private void clearCategoryCache() {
        redisService.deleteData(CATEGORY_LIST_CACHE);
    }
}
