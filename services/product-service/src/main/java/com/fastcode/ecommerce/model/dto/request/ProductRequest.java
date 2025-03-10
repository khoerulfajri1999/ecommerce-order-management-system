package com.fastcode.ecommerce.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ProductRequest {
    private String id;
    private String name;
    private String description;
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Long price;
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;
    private String categoryId;
}
