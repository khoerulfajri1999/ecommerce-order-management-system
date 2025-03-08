package com.fastcode.ecommerce.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor(JwtTokenProvider jwtTokenProvider) {
        return requestTemplate -> {
            String token = jwtTokenProvider.getToken();
            if (token != null) {
                requestTemplate.header("Authorization", token);
            }
        };
    }
}
