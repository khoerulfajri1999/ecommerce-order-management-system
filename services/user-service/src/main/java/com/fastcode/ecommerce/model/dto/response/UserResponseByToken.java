package com.fastcode.ecommerce.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseByToken {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<? extends GrantedAuthority> authorities;
}
