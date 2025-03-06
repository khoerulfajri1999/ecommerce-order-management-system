package com.fastcode.ecommerce.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RegisterResponse {
    private String userId;
    private String fullName;
    private List<String> roles;
}
