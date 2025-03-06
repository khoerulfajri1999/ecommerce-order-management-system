package com.fastcode.ecommerce.model.dto.request;

import com.fastcode.ecommerce.model.entity.UserAccount;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserRequest {
    private String id;
    @Email(message = "Invalid email format")
    private String email;
    private String fullName;
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phone;
    private UserAccount userAccount;
    private LocalDateTime createdAt;
}
