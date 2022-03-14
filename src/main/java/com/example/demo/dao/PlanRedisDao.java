package com.example.demo.dao;

import com.example.demo.model.Plan;
import com.example.demo.redis.RedisService;
import com.example.demo.utils.JsonObjectProcess;
import com.example.demo.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PlanRedisDao implements PlanDao {

    @Autowired
    private RedisService redisService;

    @Override
    public String selectPlanById(String id) {
        return redisService.getDataInRedis(id);
    }

    @Override
    public Plan insertPlan(String planPayload) {
        // additonal feature
        Pair<String, String> idAndPayload = JsonUtil.insertObjectIdIfNotExist(planPayload);
        String planId = idAndPayload.getFirst();
        String updatedPlanPayload = idAndPayload.getSecond();
        String dataInRedis = redisService.getDataInRedis(planId);
        // data already exist meaning there is conflict
        if (dataInRedis != null) {
            return null;
        }
        redisService.saveDataInRedis(planId, updatedPlanPayload);
        return new Plan(planId, updatedPlanPayload);
    }

    @Override
    public String deletePlan(String id) {
        return redisService.deleteDataInRedis(id) == null? null: id;
    }

    @Override
    public void addGraph(String jsonString) throws JsonProcessingException {
        JsonObjectProcess process = new JsonObjectProcess();
        process.parseJson(jsonString);
        for (Map.Entry<String, Map<String, Object>> entry: process.getAllNodes().entrySet()) {
            redisService.addKeyMapPair(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, List<String>> entry: process.getAllEdges().entrySet()) {
            redisService.addKeyListPair(entry.getKey(), entry.getValue());
        }
    }
}
