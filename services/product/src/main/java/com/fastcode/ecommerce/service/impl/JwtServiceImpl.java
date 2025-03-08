package com.fastcode.ecommerce.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fastcode.ecommerce.model.dto.response.JwtClaims;
import com.fastcode.ecommerce.service.JwtService;
import com.fastcode.ecommerce.utils.cache.RedisService;
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

    private final RedisService redisService;
    private Algorithm algorithm;

    private static final String JWT_CACHE_PREFIX = "JWT_";

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(jwtSecret);
    }

    @Override
    public boolean verifyJwtToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or malformed token");
        }

        String jwtToken = token.substring(7);
        String cacheKey = JWT_CACHE_PREFIX + jwtToken;

        Boolean cachedResult = (Boolean) redisService.getData(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        try {
            JWT.require(algorithm).withIssuer(appName).build().verify(jwtToken);
            redisService.saveData(cacheKey, true, 15);
            return true;
        } catch (JWTVerificationException e) {
            redisService.deleteData(cacheKey);
            throw new JWTVerificationException("Invalid or expired token");
        }
    }

    @Override
    public JwtClaims getClaimsByToken(String token) {
        String jwtToken = parseToken(token);
        String cacheKey = JWT_CACHE_PREFIX + "CLAIMS_" + jwtToken;

        JwtClaims cachedClaims = (JwtClaims) redisService.getData(cacheKey);
        if (cachedClaims != null) {
            return cachedClaims;
        }

        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        JwtClaims claims = JwtClaims.builder()
                .userAccountId(decodedJWT.getClaim("userAccountId").asString())
                .roles(decodedJWT.getClaim("roles").asList(String.class))
                .build();

        redisService.saveData(cacheKey, claims, expirationTime / 60);
        return claims;
    }

    private String parseToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
