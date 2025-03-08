package com.fastcode.ecommerce.config;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

    public void setToken(String token) {
        tokenHolder.set(token);
    }

    public String getToken() {
        return tokenHolder.get();
    }

    public void clearToken() {
        tokenHolder.remove();
    }

}
