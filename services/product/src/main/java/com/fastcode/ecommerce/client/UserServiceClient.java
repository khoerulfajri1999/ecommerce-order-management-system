package com.fastcode.ecommerce.client;

import com.fastcode.ecommerce.model.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "${feign.client.config.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") String id);

    @GetMapping("/me")
    UserResponse getUserByToken(@RequestHeader("Authorization") String token);
}
