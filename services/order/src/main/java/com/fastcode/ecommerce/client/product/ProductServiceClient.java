package com.fastcode.ecommerce.client.product;

import com.fastcode.ecommerce.model.dto.request.OrderDetailRequest;
import com.fastcode.ecommerce.model.dto.response.CommonResponse;
import com.fastcode.ecommerce.model.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "${feign.client.config.product-service.url}")
public interface ProductServiceClient {
    @GetMapping("{id}")
    CommonResponse<ProductResponse> getProductById(@PathVariable("id") String id);

    @PutMapping("/reduce-stock")
    void reduceStock(@RequestBody List<OrderDetailRequest> requests);
}
