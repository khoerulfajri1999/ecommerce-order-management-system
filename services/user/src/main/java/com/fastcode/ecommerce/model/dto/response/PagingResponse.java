package com.fastcode.ecommerce.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponse {
    private Integer totalPages;
    private Long totalElement;
    private Integer page;
    private Integer size;
    private Boolean hashNext;
    private Boolean hashPrevious;
}
