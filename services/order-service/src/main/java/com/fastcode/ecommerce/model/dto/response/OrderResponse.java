package com.fastcode.ecommerce.model.dto.response;

import com.fastcode.ecommerce.model.entity.OrderDetail;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private String id;
    private String userId;
    private List<OrderDetailResponse> orderDetails;
    private Long total;
    private Date orderDate;
}
