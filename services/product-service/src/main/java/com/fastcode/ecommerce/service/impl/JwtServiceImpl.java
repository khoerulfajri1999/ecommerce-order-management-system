package com.fastcode.ecommerce.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fastcode.ecommerce.model.dto.response.JwtClaims;
import com.fastcode.ecommerce.service.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${app.fastcode.jwt.jwt-secret}")
    private String jwtSecret;

    @Value("${app.fastcode.jwt.app-name}")
    private String appName;

    @Value("${app.fastcode.jwt.expired}")
    private long expirationTime;

    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(jwtSecret);
    }

    @Override
    public boolean verifyJwtToken(String token) {
        System.out.println(token);
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or malformed token");
        }

        String jwtToken = token.substring(7);

        try {
            JWT.require(algorithm).withIssuer(appName).build().verify(jwtToken);
            return true;
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Invalid or expired token");
        }
    }

    @Override
    public JwtClaims getClaimsByToken(String token) {
        String jwtToken = parseToken(token);
        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        return JwtClaims.builder()
                .userAccountId(decodedJWT.getClaim("userAccountId").asString())
                .roles(decodedJWT.getClaim("roles").asList(String.class))
                .build();
    }

    private String parseToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
