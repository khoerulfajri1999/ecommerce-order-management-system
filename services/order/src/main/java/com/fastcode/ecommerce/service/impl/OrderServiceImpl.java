package com.fastcode.ecommerce.service.impl;

import com.fastcode.ecommerce.client.product.ProductServiceClient;
import com.fastcode.ecommerce.client.user.UserServiceClient;
import com.fastcode.ecommerce.model.dto.request.OrderDetailRequest;
import com.fastcode.ecommerce.model.dto.request.OrderRequest;
import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.response.CommonResponse;
import com.fastcode.ecommerce.model.dto.response.OrderResponse;
import com.fastcode.ecommerce.model.dto.response.ProductResponse;
import com.fastcode.ecommerce.model.dto.response.UserResponse;
import com.fastcode.ecommerce.model.entity.Order;
import com.fastcode.ecommerce.model.entity.OrderDetail;
import com.fastcode.ecommerce.repository.OrderDetailRepository;
import com.fastcode.ecommerce.repository.OrderRepository;
import com.fastcode.ecommerce.service.OrderService;
import com.fastcode.ecommerce.utils.exceptions.RequestValidationException;
import com.fastcode.ecommerce.utils.exceptions.ResourceNotFoundException;
import com.fastcode.ecommerce.utils.mapper.OrderMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest orderRequest) {

        UserResponse user = userServiceClient.getUserById(orderRequest.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException("User not found!");
        }

        List <String> productId = orderRequest.getOrderDetails().stream().map(d -> d.getProductId()).collect(Collectors.toList());

        productId.stream()
                .map(id -> {
                    try {
                        return productServiceClient.getProductById(id);
                    } catch (FeignException.NotFound e) {
                        throw new ResourceNotFoundException("Product with ID " + id + " not found");
                    }
                })
                .collect(Collectors.toList());

        Date currentDate = new Date();
        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        order.setOrderDate(currentDate);

        List<OrderDetail> orderDetails = orderRequest.getOrderDetails().stream().map(detail -> {
            CommonResponse<ProductResponse> response = productServiceClient.getProductById(detail.getProductId());
            ProductResponse product = response.getData();
            if (product == null) {
                throw new ResourceNotFoundException("Product with ID " + detail.getProductId() + " not found");
            }
            if (product.getStock() < detail.getQty()) {
                throw new RequestValidationException("Stock Product is not enough");
            }

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .productId(detail.getProductId())
                    .qty(detail.getQty())
                    .productPrice(product.getPrice())
                    .subtotal(product.getPrice() * detail.getQty())
                    .build();
            orderDetailRepository.save(orderDetail);
            return orderDetail;
        }).collect(Collectors.toList());

        order.setOrderDetails(orderDetails);
        order.setTotal(orderDetails.stream().mapToLong(OrderDetail::getSubtotal).sum());
        orderRepository.save(order);

        List<OrderDetailRequest> stockUpdates = orderDetails.stream()
                .map(od -> new OrderDetailRequest(od.getProductId(), od.getQty()))
                .collect(Collectors.toList());
        productServiceClient.reduceStock(stockUpdates);

        return orderMapper.mapOrderToResponse(order);
    }

    @Override
    public OrderResponse getById(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order not found"));
        return orderMapper.mapOrderToResponse(order);
    }

    @Override
    public Page<OrderResponse> getAllByUserId(SearchRequest request) {
        UserResponse user = userServiceClient.getUserById(request.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException("User not found!");
        }

        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy())
        );
        return orderRepository.findByUserId(request.getUserId(), pageRequest).map(orderMapper::mapOrderToResponse);
    }
}
