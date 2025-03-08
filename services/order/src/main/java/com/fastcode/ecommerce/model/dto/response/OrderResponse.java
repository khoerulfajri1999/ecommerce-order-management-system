package com.fastcode.ecommerce.model.dto.response;

import com.fastcode.ecommerce.model.entity.OrderDetail;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponse {

    private String id;
    private String userId;
    private List<OrderDetailResponse> orderDetails;
    private Long total;
    private Date orderDate;
}
