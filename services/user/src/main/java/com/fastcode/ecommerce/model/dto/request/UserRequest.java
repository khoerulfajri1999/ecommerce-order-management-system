package com.fastcode.ecommerce.model.dto.request;

import com.fastcode.ecommerce.model.entity.UserAccount;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserRequest {
    private String id;
    private String email;
    private String fullName;
    private String phone;
    private UserAccount userAccount;
    private LocalDateTime createdAt;
}
