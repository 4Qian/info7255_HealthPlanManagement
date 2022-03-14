package com.example.demo.redis;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * use this class to interact with redis server
 * https://www.oodlestechnologies.com/blogs/Configure-Connection-Pooling-With-Redis-In-Spring-Boot/
 */
@Component
public class RedisService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("redisUserTemplate")
    private RedisTemplate<String, String> redisUserTemplate;

    public String getDataInRedis(String id) {
        return redisUserTemplate.opsForValue().get(id);
    }

    public String deleteDataInRedis(String id) {
        String deletedItem = redisUserTemplate.opsForValue().getAndDelete(id);
        return deletedItem;
    }

    public void saveDataInRedis(String id, String payload) {
        redisUserTemplate.opsForValue().set(id, payload);
    }
}
