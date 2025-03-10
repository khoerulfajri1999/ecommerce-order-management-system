package com.fastcode.ecommerce.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private Integer stock;
    private Long price;
    private CategoryResponse category;
}
