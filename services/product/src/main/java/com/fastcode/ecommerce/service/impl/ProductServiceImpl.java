package com.fastcode.ecommerce.service.impl;

import com.fastcode.ecommerce.model.dto.request.ProductRequest;
import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.request.StockUpdateRequest;
import com.fastcode.ecommerce.model.dto.response.CategoryResponse;
import com.fastcode.ecommerce.model.dto.response.ProductResponse;
import com.fastcode.ecommerce.model.entity.Category;
import com.fastcode.ecommerce.model.entity.Product;
import com.fastcode.ecommerce.repository.CategoryRepository;
import com.fastcode.ecommerce.repository.ProductRepository;
import com.fastcode.ecommerce.service.ProductService;
import com.fastcode.ecommerce.utils.cache.RedisService;
import com.fastcode.ecommerce.utils.exceptions.RequestValidationException;
import com.fastcode.ecommerce.utils.exceptions.ResourceNotFoundException;
import com.fastcode.ecommerce.utils.mapper.CategoryMapper;
import com.fastcode.ecommerce.utils.specifications.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final RedisService redisService;

    private static final String PRODUCT_CACHE_PREFIX = "PRODUCT_";

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
        clearCache(product.getId());

        return buildProductResponse(product, categoryResponse);
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

        return productRepository.findAll(specification, pageable).map(this::buildProductResponse);
    }

    @Override
    public ProductResponse getById(String id) {
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        ProductResponse cachedProduct = (ProductResponse) redisService.getData(cacheKey);

        if (cachedProduct != null) {
            return cachedProduct;
        }

        Product product = findByIdOrThrowNotFound(id);
        ProductResponse productResponse = buildProductResponse(product);

        redisService.saveData(cacheKey, productResponse, 10);

        return productResponse;
    }

    @Override
    public ProductResponse updatePut(ProductRequest productRequest) {
        Product existingProduct = findByIdOrThrowNotFound(productRequest.getId());
        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(
                () -> new ResourceNotFoundException("Category not found")
        );
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setStock(productRequest.getStock());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setCategory(category);
        existingProduct = productRepository.save(existingProduct);

        clearCache(existingProduct.getId());

        return buildProductResponse(existingProduct);
    }

    @Override
    public void deleteById(String id) {
        Product product = findByIdOrThrowNotFound(id);
        productRepository.delete(product);
        clearCache(id); // Hapus cache setelah delete
    }

    @Override
    public void reduceStock(List<StockUpdateRequest> requests) {
        for (StockUpdateRequest request : requests) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + request.getProductId() + " not found"));

            if (product.getStock() < request.getQty()) {
                throw new RequestValidationException("Not enough stock for product: " + request.getProductId());
            }
            product.setStock(product.getStock() - request.getQty());
            productRepository.save(product);

            clearCache(product.getId());
        }
    }

    private Product findByIdOrThrowNotFound(String id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found")
        );
    }

    private ProductResponse buildProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .category(categoryMapper.entityToResponse(product.getCategory()))
                .build();
    }

    private ProductResponse buildProductResponse(Product product, CategoryResponse categoryResponse) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .stock(product.getStock())
                .price(product.getPrice())
                .category(categoryResponse)
                .build();
    }

    private void clearCache(String productId) {
        redisService.deleteData(PRODUCT_CACHE_PREFIX + productId);
    }
}
