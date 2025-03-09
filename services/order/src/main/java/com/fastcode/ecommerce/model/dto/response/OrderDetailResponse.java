package com.fastcode.ecommerce.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
    private String id;
    private String productId;
    private Long productPrice;
    private Integer qty;
    private Long subtotal;
}
