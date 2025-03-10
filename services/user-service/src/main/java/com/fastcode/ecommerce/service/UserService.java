package com.fastcode.ecommerce.service;

import com.fastcode.ecommerce.model.dto.request.SearchRequest;
import com.fastcode.ecommerce.model.dto.request.UserRequest;
import com.fastcode.ecommerce.model.dto.response.UserResponse;
import com.fastcode.ecommerce.model.dto.response.UserResponseByToken;
import org.springframework.data.domain.Page;

public interface UserService {
    UserResponse create(UserRequest userRequest);
    Page<UserResponse> getAll(SearchRequest request);
    UserResponse getById(String id);
    UserResponse updatePut(UserRequest userRequest);
    UserResponseByToken getUserByToken(String token);
    void deleteById(String id);
    UserResponse deactivateUser(String id);
    UserResponse activateUser(String id);
}
