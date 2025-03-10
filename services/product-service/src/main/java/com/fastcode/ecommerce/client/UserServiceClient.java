package com.fastcode.ecommerce.client;

import com.fastcode.ecommerce.model.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{id}")
    UserResponse getUserById(@PathVariable("id") String id);

    @GetMapping("/api/v1/users/me")
    UserResponse getUserByToken(@RequestHeader("Authorization") String token);
}
