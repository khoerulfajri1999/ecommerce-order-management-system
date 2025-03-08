package com.fastcode.ecommerce.model.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchRequest {
    private String userId;
    private Integer page;
    private Integer size;
    private String direction;
    private String sortBy;
}
