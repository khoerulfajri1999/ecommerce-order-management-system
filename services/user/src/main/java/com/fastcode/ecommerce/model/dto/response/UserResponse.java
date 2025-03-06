package com.fastcode.ecommerce.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String fullName;
    private String phone;
    private LocalDateTime createdAt;
}
