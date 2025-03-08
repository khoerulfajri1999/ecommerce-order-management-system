package com.fastcode.ecommerce.utils.mapper;

import com.fastcode.ecommerce.model.dto.response.OrderResponse;
import com.fastcode.ecommerce.model.entity.Order;

public interface OrderMapper {
    OrderResponse mapOrderToResponse(Order order);
}
