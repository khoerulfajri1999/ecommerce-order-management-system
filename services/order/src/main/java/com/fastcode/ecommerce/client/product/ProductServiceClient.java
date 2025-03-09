package com.fastcode.ecommerce.client.product;

import com.fastcode.ecommerce.model.dto.request.OrderDetailRequest;
import com.fastcode.ecommerce.model.dto.response.CommonResponse;
import com.fastcode.ecommerce.model.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service", url = "http://localhost:8050")
public interface ProductServiceClient {
    @GetMapping("/api/v1/products/{id}")
    CommonResponse<ProductResponse> getProductById(@PathVariable("id") String id);

    @PutMapping("/api/v1/products/reduce-stock")
    void reduceStock(@RequestBody List<OrderDetailRequest> requests);
}
