package com.fastcode.ecommerce.model.entity;

import com.fastcode.ecommerce.constant.ConstantTable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = ConstantTable.ORDERS_DETAIL)
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Column(name = "product_price", nullable = false)
    private Long productPrice;

    @Column(name = "subtotal", nullable = false)
    private Long subtotal;

}
