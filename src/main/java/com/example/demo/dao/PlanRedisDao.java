package com.example.demo.dao;

import com.example.demo.model.*;
import com.example.demo.redis.RedisService;
import com.example.demo.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        NodeModel nodeModel = NodeModelFactoryFromJson.fromJsonString(jsonString);
        RedisData redisData = new RedisData(nodeModel);
        System.out.println("=== added");
        for (Map.Entry<String, Map<String, String>> entry: redisData.allNodes.entrySet()) {
            redisService.addKeyMapPair(entry.getKey(), entry.getValue());
            System.out.println(String.format("HGETALL \"%s\"", entry.getKey()));
        }
        for (Map.Entry<String, List<String>> entry: redisData.allEdges.entrySet()) {
            redisService.addKeyListPair(entry.getKey(), entry.getValue());
            System.out.println(String.format("LRANGE \"%s\" 0 -1", entry.getKey()));
        }
    }

    @Override
    public String deleteGraph(String jsonSchemaFilePath, String planObjectKey) {
        NodeModel planData = getPlanData(jsonSchemaFilePath, planObjectKey);
        RedisData redisData = new RedisData(planData);
        System.out.println("=== deleted");
        for (Map.Entry<String, Map<String, String>> entry: redisData.allNodes.entrySet()) {
            redisService.deleteKeyMapPair(entry.getKey());
            System.out.println(String.format("HGETALL \"%s\"", entry.getKey()));
        }
        for (Map.Entry<String, List<String>> entry: redisData.allEdges.entrySet()) {
            redisService.deleteKeyListPair(entry.getKey());
            System.out.println(String.format("LRANGE \"%s\" 0 -1", entry.getKey()));
        }
        return "";
    }

    @Override
    public String getGraphById(String jsonSchemaFilePath, String planObjectKey) {
        return getPlanData(jsonSchemaFilePath, planObjectKey).getJsonString();
    }

    public NodeModel getPlanData(String jsonSchemaFilePath, String planObjectKey) {
        EdgesInfo edgesInfo = EdgeInfoFactory.fromJsonSchema(jsonSchemaFilePath);
        return NodeModelFactoryFromRedis.fromRedis(redisService, edgesInfo, planObjectKey);
    }

    @Override
    public String patchGraph(String jsonSchemaFilePath, String patchJsonString) throws JsonProcessingException {
        NodeModel patchNodeModel = NodeModelFactoryFromJson.fromJsonString(patchJsonString);
        NodeModel existingPlan = getPlanData(jsonSchemaFilePath, patchNodeModel.objectKey);
        existingPlan.patch(patchNodeModel);
        boolean isPatchedPlanValid = JsonSchemaUtil.validate(jsonSchemaFilePath, existingPlan.getJsonString());
        if (!isPatchedPlanValid) {
            return "invalid patch";
        }
        deleteGraph(jsonSchemaFilePath, existingPlan.objectKey);
        addGraph(existingPlan.getJsonString());
        return existingPlan.getJsonString();
    }
}
