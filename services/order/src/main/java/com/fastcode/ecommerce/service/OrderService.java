package com.fastcode.ecommerce.service;

import com.fastcode.ecommerce.model.dto.request.OrderRequest;
import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.response.OrderResponse;
import org.springframework.data.domain.Page;

public interface OrderService {

    OrderResponse create(OrderRequest orderRequest);
    OrderResponse getById(String orderId);
    Page<OrderResponse> getAllByUserId(SearchRequest request);


}
