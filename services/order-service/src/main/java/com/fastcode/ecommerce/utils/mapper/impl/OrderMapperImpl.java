package com.fastcode.ecommerce.utils.mapper.impl;

import com.fastcode.ecommerce.model.dto.response.OrderDetailResponse;
import com.fastcode.ecommerce.model.dto.response.OrderResponse;
import com.fastcode.ecommerce.model.entity.Order;
import com.fastcode.ecommerce.utils.mapper.OrderMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapperImpl implements OrderMapper {
    @Override
    public OrderResponse mapOrderToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderDetails(order.getOrderDetails().stream()
                        .map(od -> OrderDetailResponse.builder()
                                .id(od.getId())
                                .productId(od.getProductId())
                                .qty(od.getQty())
                                .productPrice(od.getProductPrice())
                                .subtotal(od.getSubtotal())
                                .build())
                        .collect(Collectors.toList()))
                .total(order.getTotal())
                .orderDate(order.getOrderDate())
                .build();
    }
}
