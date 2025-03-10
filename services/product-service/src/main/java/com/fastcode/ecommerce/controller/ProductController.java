package com.fastcode.ecommerce.controller;

import com.fastcode.ecommerce.constant.APIUrl;
import com.fastcode.ecommerce.model.dto.request.ProductRequest;
import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.request.StockUpdateRequest;
import com.fastcode.ecommerce.model.dto.response.CommonResponse;
import com.fastcode.ecommerce.model.dto.response.PagingResponse;
import com.fastcode.ecommerce.model.dto.response.ProductResponse;
import com.fastcode.ecommerce.service.ProductService;
import com.fastcode.ecommerce.utils.cache.RedisService;
import com.fastcode.ecommerce.utils.validation.PagingUtil;
import com.fastcode.ecommerce.utils.validation.SortingUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = APIUrl.PRODUCT_API)
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final RedisService redisService;

    private static final String PRODUCT_CACHE_PREFIX = "PRODUCT_";
    private static final String PRODUCT_LIST_CACHE = "PRODUCT_LIST";
    private static final int CACHE_TTL = 60;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<ProductResponse>> addNewProduct(@Valid @RequestBody ProductRequest payload) {
        ProductResponse product = productService.create(payload);

        clearProductCache();

        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("New product added!")
                .data(product)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductResponse>>> getAllProduct(
            @RequestParam (name = "search",required = false) String search,
            @RequestParam(required = false,defaultValue = "1") String page,
            @RequestParam(required = false,defaultValue = "10") String size,
            @RequestParam(required = false,defaultValue = "asc") String direction,
            @RequestParam(required = false,defaultValue = "name") String sortBy
    ) {
        Integer safePage = PagingUtil.validatePage(page);
        Integer safeSize = PagingUtil.validateSize(size);
        direction = PagingUtil.validateDirection(direction);
        sortBy = SortingUtil.sortByValidation(ProductResponse.class, sortBy, "name");
        SearchRequest request = SearchRequest.builder()
                .query(search)
                .page(Math.max(safePage-1,0))
                .size(safeSize)
                .direction(direction)
                .sortBy(sortBy)
                .build();

        List<ProductResponse> cachedProducts = (List<ProductResponse>) redisService.getData(PRODUCT_LIST_CACHE);
        if (cachedProducts != null) {
            return ResponseEntity.ok(CommonResponse.<List<ProductResponse>>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("All products retrieved (from cache)")
                    .data(cachedProducts)
                    .build());
        }

        Page<ProductResponse> products = productService.getAll(request);
        PagingResponse paging = PagingResponse.builder()
                .totalPages(products.getTotalPages())
                .totalElement(products.getTotalElements())
                .page(request.getPage()+1)
                .size(request.getSize())
                .hashNext(products.hasNext())
                .hashPrevious(products.hasPrevious())
                .build();

        redisService.saveData(PRODUCT_LIST_CACHE, products.getContent(), CACHE_TTL);

        CommonResponse<List<ProductResponse>> response = CommonResponse.<List<ProductResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All products retrieved")
                .data(products.getContent())
                .paging(paging)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<ProductResponse>> getProductById(@PathVariable String id) {
        String cacheKey = PRODUCT_CACHE_PREFIX + id;

        ProductResponse cachedProduct = (ProductResponse) redisService.getData(cacheKey);
        if (cachedProduct != null) {
            return ResponseEntity.ok(CommonResponse.<ProductResponse>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Product ID " + cachedProduct.getId() + " found! (from cache)")
                    .data(cachedProduct)
                    .build());
        }

        ProductResponse product = productService.getById(id);

        redisService.saveData(cacheKey, product, CACHE_TTL);

        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product ID " + product.getId() + " found!")
                .data(product)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(@Valid @RequestBody ProductRequest payload) {
        ProductResponse product = productService.updatePut(payload);
        CommonResponse<ProductResponse> response = CommonResponse.<ProductResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product with ID " + payload.getId() + " updated successfully.")
                .data(product)
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<String>> deleteById(@PathVariable String id) {
        productService.deleteById(id);

        clearProductCache();

        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Product with ID " + id + " deleted successfully.")
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @PutMapping("/reduce-stock")
    public ResponseEntity<Void> reduceStock(@RequestBody List<StockUpdateRequest> requests) {
        productService.reduceStock(requests);
        return ResponseEntity.ok().build();
    }

    private void clearProductCache() {
        redisService.deleteData(PRODUCT_LIST_CACHE);
    }
}
