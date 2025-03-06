package com.fastcode.ecommerce.utils.mapper;

import com.fastcode.ecommerce.model.dto.request.UserRequest;
import com.fastcode.ecommerce.model.dto.response.UserResponse;
import com.fastcode.ecommerce.model.entity.User;

public interface UserMapper {
    UserResponse entityToResponse(User user);
    User requestToEntity(UserRequest userRequest);
    UserRequest entityToRequest(User user);
}
