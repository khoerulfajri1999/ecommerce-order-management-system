package com.fastcode.ecommerce.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class OrderRequest {
    @NotNull(message = "User ID cannot be null")
    private String userId;

    private List<OrderDetailRequest> orderDetails;
}
