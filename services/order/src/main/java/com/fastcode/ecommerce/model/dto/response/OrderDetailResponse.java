package com.fastcode.ecommerce.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderDetailResponse {
    private String id;
    private String productId;
    private Long productPrice;
    private Integer qty;
    private Long subtotal;
}
