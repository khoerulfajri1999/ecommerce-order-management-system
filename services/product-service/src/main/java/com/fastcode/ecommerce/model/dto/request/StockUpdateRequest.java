package com.fastcode.ecommerce.model.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StockUpdateRequest {
    private String productId;
    private Integer qty;
}
