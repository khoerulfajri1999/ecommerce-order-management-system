package com.fastcode.ecommerce.service.impl;

import com.fastcode.ecommerce.model.dto.request.ProductRequest;
import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.response.CategoryResponse;
import com.fastcode.ecommerce.model.dto.response.ProductResponse;
import com.fastcode.ecommerce.model.entity.Category;
import com.fastcode.ecommerce.model.entity.Product;
import com.fastcode.ecommerce.repository.CategoryRepository;
import com.fastcode.ecommerce.repository.ProductRepository;
import com.fastcode.ecommerce.service.ProductService;
import com.fastcode.ecommerce.utils.exceptions.ResourceNotFoundException;
import com.fastcode.ecommerce.utils.mapper.CategoryMapper;
import com.fastcode.ecommerce.utils.specifications.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse create(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category not found")
        );
        CategoryResponse categoryResponse = categoryMapper.entityToResponse(category);
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .stock(productRequest.getStock())
                .price(productRequest.getPrice())
                .category(category)
                .build();

        product = productRepository.save(product);
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .category(categoryResponse)
                .build();
    }

    @Override
    public Page<ProductResponse> getAll(SearchRequest request) {
        Specification<Product> specification = ProductSpecification.getSpecification(request);
        Sort.Direction direction = Sort.Direction.fromString(request.getDirection());
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                direction,
                request.getSortBy()
        );

        return productRepository.findAll(specification, pageable).map(
                product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .stock(product.getStock())
                        .price(product.getPrice())
                        .category(categoryMapper.entityToResponse(product.getCategory()))
                        .build()
        );
    }

    @Override
    public ProductResponse getById(String id) {
        Product product = findBidOrThrowNotFound(id);
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .category(categoryMapper.entityToResponse(product.getCategory()))
                .build();
    }

    @Override
    public ProductResponse updatePut(ProductRequest productRequest) {
        Product existingProduct = findBidOrThrowNotFound(productRequest.getId());
        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category not found")
        );
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setStock(productRequest.getStock());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setCategory(category);
        existingProduct = productRepository.save(existingProduct);
        return ProductResponse.builder()
                .id(existingProduct.getId())
                .name(existingProduct.getName())
                .description(existingProduct.getDescription())
                .stock(existingProduct.getStock())
                .price(existingProduct.getPrice())
                .category(categoryMapper.entityToResponse(existingProduct.getCategory()))
                .build();
    }

    @Override
    public void deleteById(String id) {
        Product product = findBidOrThrowNotFound(id);
        productRepository.delete(product);
    }

    private Product findBidOrThrowNotFound(String id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found")
        );
    }
}
