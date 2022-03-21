package com.example.demo.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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

    /**
     * get value by key
     * @param id
     * @return
     */
    public String getDataInRedis(String id) {
        return redisUserTemplate.opsForValue().get(id);
    }

    /**
     * delete key-value pair by key
     * @param id
     * @return
     */
    public String deleteDataInRedis(String id) {
        String deletedItem = redisUserTemplate.opsForValue().getAndDelete(id);
        return deletedItem;
    }

    /**
     * add a key-value pair
     * @param id
     * @param payload
     */
    public void saveDataInRedis(String id, String payload) {
        redisUserTemplate.opsForValue().set(id, payload);
    }

    /**
     * save node
     * "key_1" : {"hashKey1":"value1", "hashKey2": "value2"}
     * @param key           "key_1"
     * @param mapAsValue    {"hashKey1":"value1", "hashKey2": "value2"}
     */
    public void addKeyMapPair(final String key, Map<String, String> mapAsValue) {
        deleteKeyMapPair(key);
        redisUserTemplate.opsForHash().putAll(key, mapAsValue);
    }

    public void deleteKeyMapPair(final String key) {
        HashOperations<String, String, String> hashOperations = redisUserTemplate.opsForHash();
        Map<String, String> existingEntries = hashOperations.entries(key);
        String[] existingKeys = existingEntries.keySet().toArray(new String[0]);
        if (existingKeys.length > 0) {
            hashOperations.delete(key, existingKeys);
        }
    }

    public Map<String, String> getNodeSimpleProperties(final String key) {
        Map<String, String> entries = redisUserTemplate.<String, String>opsForHash().entries(key);
        return entries;
    }

    /**
     * save edge
     *
     * @param key
     * @param list
     */
    public void addKeyListPair(final String key, List<String> list) {
        deleteKeyListPair(key);
        redisUserTemplate.opsForList().rightPushAll(key, list);
    }

    public void deleteKeyListPair(final String key) {
        ListOperations<String, String> listOperations = redisUserTemplate.opsForList();
        Long size = listOperations.size(key);
        if (size > 0) {
            listOperations.leftPop(key, size);
        }
    }

    public List<String> getEdgeTargetNodeKeys(final String key) {
        ListOperations<String, String> listOperations = redisUserTemplate.opsForList();
        Long size = listOperations.size(key);
        List<String> res = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            res.add(listOperations.index(key, i));
        }
        System.out.println(res);
        return res;
    }
}
