package com.fastcode.ecommerce.model.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String userId;
    private String username;
    private String token;
    private List<String> role;
}
