package com.fastcode.ecommerce.service;

import com.fastcode.ecommerce.model.dto.response.JwtClaims;
import com.fastcode.ecommerce.model.entity.UserAccount;

public interface JwtService {
    String generateToken(UserAccount userAccount);
    boolean verifyJwtToken(String token);
    JwtClaims getClaimsByToken(String token);
}
