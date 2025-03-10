package com.fastcode.ecommerce.service;

import com.fastcode.ecommerce.model.dto.request.ProductRequest;
import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.request.StockUpdateRequest;
import com.fastcode.ecommerce.model.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest productRequest);
    Page<ProductResponse> getAll(SearchRequest request);
    ProductResponse getById(String id);
    ProductResponse updatePut(ProductRequest productRequest);
    void deleteById(String id);
    void reduceStock(List<StockUpdateRequest> requests);
}
