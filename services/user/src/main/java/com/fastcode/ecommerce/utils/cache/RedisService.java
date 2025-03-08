package com.fastcode.ecommerce.utils.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final StringRedisTemplate redisTemplates;
    private final ObjectMapper objectMapper;

    public <T> void saveData(String key, T data, long expirationMinutes) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            redisTemplates.opsForValue().set(key, jsonData, expirationMinutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing data to Redis", e);
        }
    }

    public <T> T getData(String key, Class<T> clazz) {
        try {
            String jsonData = redisTemplates.opsForValue().get(key);
            if (jsonData == null) return null;
            return objectMapper.readValue(jsonData, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing data from Redis", e);
        }
    }
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
