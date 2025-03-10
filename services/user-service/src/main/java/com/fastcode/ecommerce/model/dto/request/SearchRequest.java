package com.fastcode.ecommerce.model.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchRequest {
    private String query;
    private Integer page;
    private Integer size;
    private String direction;
    private String sortBy;
}
