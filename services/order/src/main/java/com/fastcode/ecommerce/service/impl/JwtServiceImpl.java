package com.fastcode.ecommerce.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fastcode.ecommerce.model.dto.response.JwtClaims;
import com.fastcode.ecommerce.service.JwtService;
import com.fastcode.ecommerce.utils.cache.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private long expirationTime; // dalam milidetik

    private final RedisService redisService;
    private Algorithm algorithm;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String JWT_CACHE_PREFIX = "JWT_";
    private static final String CLAIMS_CACHE_PREFIX = "CLAIMS_";

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(jwtSecret);
    }

    @Override
    public boolean verifyJwtToken(String token) {
        System.out.println("Token Received: " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or malformed token");
        }

        String jwtToken = token.substring(7);
        String cacheKey = JWT_CACHE_PREFIX + jwtToken;

        try {
            Object cachedResult = redisService.getData(cacheKey);
            System.out.println("Cache result: " + cachedResult);

            if (cachedResult instanceof String) {
                boolean isValid = Boolean.parseBoolean((String) cachedResult);
                System.out.println("Token Valid from Cache: " + isValid);
                return isValid;
            }

            JWTVerifier verifier = JWT.require(algorithm).withIssuer(appName).build();
            verifier.verify(jwtToken);

            redisService.saveData(cacheKey, Boolean.TRUE.toString(), expirationTime / 1000);
            System.out.println("Token Verified & Cached");

            return true;
        } catch (JWTVerificationException e) {
            redisService.deleteData(cacheKey);
            System.out.println("Token Invalid: " + e.getMessage());
            throw new JWTVerificationException("Invalid or expired token");
        }
    }


    @Override
    public JwtClaims getClaimsByToken(String token) {
        String jwtToken = parseToken(token);
        String cacheKey = CLAIMS_CACHE_PREFIX + jwtToken;

        try {
            Object cachedClaimsObj = redisService.getData(cacheKey);
            if (cachedClaimsObj instanceof String) {
                return objectMapper.readValue((String) cachedClaimsObj, JwtClaims.class);
            }
        } catch (JsonProcessingException e) {
            System.out.println("Failed to parse cached claims: " + e.getMessage());
        }

        DecodedJWT decodedJWT = JWT.decode(jwtToken);
        JwtClaims claims = JwtClaims.builder()
                .userAccountId(decodedJWT.getClaim("userAccountId").asString())
                .roles(decodedJWT.getClaim("roles").asList(String.class))
                .build();

        try {
            String claimsJson = objectMapper.writeValueAsString(claims);
            redisService.saveData(cacheKey, claimsJson, expirationTime / 1000);
        } catch (JsonProcessingException e) {
            System.out.println("Failed to serialize claims: " + e.getMessage());
        }

        return claims;
    }

    private String parseToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}

