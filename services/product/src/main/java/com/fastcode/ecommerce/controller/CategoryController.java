package com.fastcode.ecommerce.controller;

import com.fastcode.ecommerce.constant.APIUrl;
import com.fastcode.ecommerce.model.dto.request.CategoryRequest;
import com.fastcode.ecommerce.model.dto.response.CategoryResponse;
import com.fastcode.ecommerce.model.dto.response.CommonResponse;
import com.fastcode.ecommerce.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = APIUrl.CATEGORY_API)
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<CategoryResponse>> addNewCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.create(request);

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
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
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
    public ResponseEntity<CommonResponse<CategoryResponse>> updateCategory(@Valid @RequestBody CategoryRequest payload) {
        CategoryResponse category = categoryService.updatePut(payload);
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
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'CUSTOMER')")
    public ResponseEntity<CommonResponse<CategoryResponse>> getCategoryById (@PathVariable String id) {

        CategoryResponse category = categoryService.getById(id);

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
//    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<String>> deleteById(@PathVariable String id){
        categoryService.deleteById(id);

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
}
