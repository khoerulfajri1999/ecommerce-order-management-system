package com.fastcode.ecommerce.model.entity;

import com.fastcode.ecommerce.constant.ConstantTable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = ConstantTable.ORDERS)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "userId", nullable = false)
    private String userId;

    @OneToMany(mappedBy = "order")
    @JsonManagedReference
    private List<OrderDetail> orderDetails;

    @Column(name = "total", nullable = false)
    private Long total;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date", updatable = true)
    private Date orderDate;
}
