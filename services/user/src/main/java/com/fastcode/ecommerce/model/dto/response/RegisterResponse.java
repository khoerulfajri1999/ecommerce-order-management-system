package com.fastcode.ecommerce.model.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String userId;
    private String fullName;
    private List<String> roles;
}
