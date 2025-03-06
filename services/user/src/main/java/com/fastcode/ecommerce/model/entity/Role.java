package com.fastcode.ecommerce.model.entity;

import com.fastcode.ecommerce.constant.ConstantTable;
import com.fastcode.ecommerce.constant.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = ConstantTable.ROLES)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private UserRole role;
}
