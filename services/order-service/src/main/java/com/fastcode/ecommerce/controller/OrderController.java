package com.fastcode.ecommerce.controller;

import com.fastcode.ecommerce.config.JwtTokenProvider;
import com.fastcode.ecommerce.constant.APIUrl;
import com.fastcode.ecommerce.model.dto.request.OrderRequest;
import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.response.CommonResponse;
import com.fastcode.ecommerce.model.dto.response.OrderResponse;
import com.fastcode.ecommerce.model.dto.response.PagingResponse;
import com.fastcode.ecommerce.service.OrderService;
import com.fastcode.ecommerce.utils.validation.PagingUtil;
import com.fastcode.ecommerce.utils.validation.SortingUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping(path = APIUrl.ORDER_API)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<CommonResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        jwtTokenProvider.setToken(token);

        OrderResponse orderResponse = orderService.create(orderRequest);

        jwtTokenProvider.clearToken();

        CommonResponse<OrderResponse> commonResponse = CommonResponse.<OrderResponse>builder()
                .data(orderResponse)
                .message("Order Created")
                .statusCode(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<OrderResponse>> getOrderById(@PathVariable String id){
        OrderResponse orderResponse = orderService.getById(id);
        CommonResponse<OrderResponse> commonResponse = CommonResponse.<OrderResponse>builder()
                .data(orderResponse)
                .message("Order Found")
                .statusCode(HttpStatus.OK.value())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<List<OrderResponse>>> getAllOrderByUserId(
            @PathVariable (name = "userId") String userId,
            @RequestParam(required = false,defaultValue = "1") String page,
            @RequestParam(required = false,defaultValue = "10") String size,
            @RequestParam(required = false,defaultValue = "asc") String direction,
            @RequestParam(required = false,defaultValue = "orderDate") String sortBy,
            HttpServletRequest request
    ) {

        String token = request.getHeader("Authorization");
        jwtTokenProvider.setToken(token);

        Integer safePage = PagingUtil.validatePage(page);
        Integer safeSize = PagingUtil.validateSize(size);
        direction = PagingUtil.validateDirection(direction);
        sortBy = SortingUtil.sortByValidation(OrderResponse.class, sortBy, "orderDate");
        SearchRequest searchRequest = SearchRequest.builder()
                .userId(userId)
                .page(Math.max(safePage-1,0))
                .size(safeSize)
                .direction(direction)
                .sortBy(sortBy)
                .build();

        Page<OrderResponse> orders = orderService.getAllByUserId(searchRequest);
        jwtTokenProvider.clearToken();
        PagingResponse paging = PagingResponse.builder()
                .totalPages(orders.getTotalPages())
                .totalElement(orders.getTotalElements())
                .page(searchRequest.getPage()+1)
                .size(searchRequest.getSize())
                .hashNext(orders.hasNext())
                .hashPrevious(orders.hasPrevious())
                .build();

        CommonResponse<List<OrderResponse>> response = CommonResponse.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All orders retrieved")
                .data(orders.getContent())
                .paging(paging)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<CommonResponse<List<OrderResponse>>> getAllOrderByUserLogin(
            @RequestParam(required = false, defaultValue = "1") String page,
            @RequestParam(required = false, defaultValue = "10") String size,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(required = false, defaultValue = "orderDate") String sortBy,
            HttpServletRequest request
    ) {

        String token = request.getHeader("Authorization");

        Integer safePage = PagingUtil.validatePage(page);
        Integer safeSize = PagingUtil.validateSize(size);
        direction = PagingUtil.validateDirection(direction);
        sortBy = SortingUtil.sortByValidation(OrderResponse.class, sortBy, "orderDate");

        SearchRequest searchRequest = SearchRequest.builder()
                .page(Math.max(safePage - 1, 0))
                .size(safeSize)
                .direction(direction)
                .sortBy(sortBy)
                .build();

        Page<OrderResponse> orders = orderService.getAllByUserToken(token, searchRequest);

        PagingResponse paging = PagingResponse.builder()
                .totalPages(orders.getTotalPages())
                .totalElement(orders.getTotalElements())
                .page(searchRequest.getPage() + 1)
                .size(searchRequest.getSize())
                .hashNext(orders.hasNext())
                .hashPrevious(orders.hasPrevious())
                .build();

        CommonResponse<List<OrderResponse>> response = CommonResponse.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All orders retrieved")
                .data(orders.getContent())
                .paging(paging)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(response);
    }
}
