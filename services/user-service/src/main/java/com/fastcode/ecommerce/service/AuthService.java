package com.fastcode.ecommerce.service;

import com.fastcode.ecommerce.model.dto.request.AuthRequest;
import com.fastcode.ecommerce.model.dto.request.RegisterRequest;
import com.fastcode.ecommerce.model.dto.response.LoginResponse;
import com.fastcode.ecommerce.model.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    RegisterResponse registerAdmin(RegisterRequest request);
    LoginResponse login(AuthRequest request);
}
