package com.fastcode.ecommerce.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fastcode.ecommerce.model.dto.response.JwtClaims;
import com.fastcode.ecommerce.model.entity.UserAccount;
import com.fastcode.ecommerce.service.JwtService;
import com.fastcode.ecommerce.utils.cache.RedisService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public String generateToken(UserAccount userAccount) {

        List<String> roles = userAccount.getRoles().stream()
                .map(role -> role.getRole().toString())
                .toList();

        String token = JWT.create()
                .withIssuer(appName)
                .withSubject(userAccount.getUsername())
                .withClaim("userAccountId", userAccount.getId())
                .withArrayClaim("roles", roles.toArray(new String[0]))
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .sign(algorithm);

        redisService.saveData(JWT_CACHE_PREFIX + token, token, expirationTime/60);
        return token;
    }

    @Override
    public boolean verifyJwtToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or malformed token");
        }

        String jwtToken = token.substring(7);
        String cachedToken = redisService.getData(JWT_CACHE_PREFIX + jwtToken, String.class);


        if (cachedToken != null) {
            return true;
        }

        try {
            JWT.require(algorithm).withIssuer(appName).build().verify(jwtToken);
            redisService.saveData(JWT_CACHE_PREFIX + jwtToken, jwtToken, expirationTime/60);
            return true;
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Invalid or expired token");
        }
    }

    @Override
    public JwtClaims getClaimsByToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(parseToken(token));
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
