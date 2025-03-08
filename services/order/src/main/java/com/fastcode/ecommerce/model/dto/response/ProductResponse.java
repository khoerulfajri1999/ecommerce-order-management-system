package com.fastcode.ecommerce.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Integer stock;
    private Long price;
    private CategoryResponse category;
}
