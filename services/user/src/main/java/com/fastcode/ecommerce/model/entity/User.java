package com.fastcode.ecommerce.model.entity;

import com.fastcode.ecommerce.constant.ConstantTable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = ConstantTable.USERS)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_account_id", unique = true)
    private UserAccount userAccount;


}
