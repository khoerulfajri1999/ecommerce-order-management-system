package com.fastcode.ecommerce.utils.mapper.impl;

import com.fastcode.ecommerce.model.dto.request.UserRequest;
import com.fastcode.ecommerce.model.dto.response.UserResponse;
import com.fastcode.ecommerce.model.entity.User;
import com.fastcode.ecommerce.utils.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserResponse entityToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public User requestToEntity(UserRequest userRequest) {
        return User.builder()
                .id(userRequest.getId())
                .email(userRequest.getEmail())
                .fullName(userRequest.getFullName())
                .phone(userRequest.getPhone())
                .userAccount(userRequest.getUserAccount())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public UserRequest entityToRequest(User user) {
        return UserRequest.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .userAccount(user.getUserAccount())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
