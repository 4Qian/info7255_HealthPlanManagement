package com.example.demo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisDataConfig {

    public static Logger LOGGER = LoggerFactory.getLogger(RedisDataConfig.class);

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean(name = "redisUserTemplate")
    public RedisTemplate<String, String> redisTemplateUser(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    //    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory());
//        return template;
//    }

    //	@Bean
    //	JedisConnectionFactory jedisConnectionFactory() {
    //		JedisConnectionFactory jedisConFactory
    //				= new JedisConnectionFactory();
    //		jedisConFactory.setHostName("localhost");
    //		jedisConFactory.setPort(6379);
    //		return jedisConFactory;
    //	}
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() throws UnknownHostException {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(20);
//        poolConfig.setMinIdle(2);
//        poolConfig.setMaxIdle(5);
//
//        JedisConnectionFactory factory = new JedisConnectionFactory(poolConfig);
//        factory.setHostName("192.168.1.1");
//        factory.setUsePool(true);
//        factory.setPort(6379);
//        return factory;
//    }
}