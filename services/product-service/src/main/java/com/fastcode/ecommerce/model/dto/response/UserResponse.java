package com.fastcode.ecommerce.model.dto.response;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
    private List<? extends GrantedAuthority> authorities; // Untuk Spring Security
}
