package com.fastcode.ecommerce.service;

import com.fastcode.ecommerce.model.dto.response.JwtClaims;

public interface JwtService {
    boolean verifyJwtToken(String token);
    JwtClaims getClaimsByToken(String token);
}
