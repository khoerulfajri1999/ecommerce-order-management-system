package com.fastcode.ecommerce.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryResponse {
    private String id;
    private String name;
    private String description;
}
