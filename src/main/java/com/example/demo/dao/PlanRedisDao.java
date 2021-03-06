package com.example.demo.dao;

import com.example.demo.model.*;
import com.example.demo.model.response.ObjectTypeAndIdResponse;
import com.example.demo.rabbitMQ.RabbitMQPublisher;
import com.example.demo.redis.RedisService;
import com.example.demo.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PlanRedisDao implements PlanDao {
    ObjectMapper objectMapper = new ObjectMapper();
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
        return redisService.deleteDataInRedis(id) == null ? null : id;
    }

    // =========>addGraph
    @Override
    public ObjectTypeAndIdResponse addGraph(String jsonString) throws JsonProcessingException {
        NodeModel nodeModel = NodeModelFactoryFromJson.fromJsonString(jsonString);
        // check if the plan already exist
        String planKey = nodeModel.objectKey;
        Map<String, String> planSimpleProperties = redisService.getNodeSimpleProperties(planKey);
//        System.out.println(planSimpleProperties);
        if (!planSimpleProperties.isEmpty()) {
            throw new RuntimeException("plan already exist");
        }
        // !push to queue
        System.out.println("========== Starting publishing PUT messages to queue ========== ");
        List<ESRequest> requests = nodeModel.getElasticSearchRequests("plan", Optional.empty());
        publishESRequestsToQueue(requests);
        System.out.println("========== Completed publishing PUT messages to queue ======= ");

        RedisData redisData = new RedisData(nodeModel);
        for (Map.Entry<String, Map<String, String>> entry : redisData.allNodes.entrySet()) {
            redisService.addKeyMapPair(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, List<String>> entry : redisData.allEdges.entrySet()) {
            redisService.addKeyListPair(entry.getKey(), entry.getValue());
        }
        return nodeModel.getObjectTypeAndId();
    }

    @Override
    public String patchGraph(String jsonSchemaFilePath, String patchJsonString) throws JsonProcessingException {
        NodeModel patchNodeModel = NodeModelFactoryFromJson.fromJsonString(patchJsonString);
        NodeModel patchedPlan = getPlanData(jsonSchemaFilePath, patchNodeModel.objectKey);
        patchedPlan.patch(patchNodeModel);
        boolean isPatchedPlanValid = JsonSchemaUtil.validate(jsonSchemaFilePath, patchedPlan.getJsonString());
        if (!isPatchedPlanValid) {
            throw new RuntimeException("Invalid patch");
        }
        NodeModel existingPlan = getPlanData(jsonSchemaFilePath, patchNodeModel.objectKey);
        List<ESRequest> existingRequests = existingPlan.getElasticSearchRequests("plan", Optional.empty());
        List<ESRequest> patchedRequests = patchedPlan.getElasticSearchRequests("plan", Optional.empty());

        patchedRequests.removeAll(existingRequests);
        System.out.println("========== Starting publishing PUT messages to queue ========== ");
        publishESRequestsToQueue(patchedRequests);
        System.out.println("========== Completed publishing PUT messages to queue ========== ");

        RedisData patchedRedisData = new RedisData(patchedPlan);
        for (Map.Entry<String, Map<String, String>> entry : patchedRedisData.allNodes.entrySet()) {
            redisService.addKeyMapPair(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, List<String>> entry : patchedRedisData.allEdges.entrySet()) {
            redisService.addKeyListPair(entry.getKey(), entry.getValue());
        }
        return patchedPlan.getJsonString();
    }

    @Override
    public ObjectTypeAndIdResponse deleteGraph(String jsonSchemaFilePath, String planObjectKey) {
        NodeModel planData = getPlanData(jsonSchemaFilePath, planObjectKey);
        RedisData redisData = new RedisData(planData);
        // push to queue
        System.out.println("========== Starting publishing DELETE messages to queue ======= ");
        List<ESRequest> requests = planData.getElasticSearchRequests("plan", Optional.empty());
        requests.forEach(request -> request.setToDelete(true));
        publishESRequestsToQueue(requests);
        System.out.println("========== Completed publishing DELETE messages to queue =======");

        for (Map.Entry<String, Map<String, String>> entry: redisData.allNodes.entrySet()) {
            redisService.deleteKeyMapPair(entry.getKey());
//            System.out.println(String.format("HGETALL \"%s\"", entry.getKey()));
        }
        for (Map.Entry<String, List<String>> entry: redisData.allEdges.entrySet()) {
            redisService.deleteKeyListPair(entry.getKey());
//            System.out.println(String.format("LRANGE \"%s\" 0 -1", entry.getKey()));
        }
        return planData.getObjectTypeAndId();
    }

    private void publishESRequestsToQueue(List<ESRequest> requests) {
        for (ESRequest request : requests) {
            try {
                String requestAsString = objectMapper.writeValueAsString(request);
                RabbitMQPublisher.publishToQueue(requestAsString);
            } catch (Exception e) {
                System.out.println("--- caught publish error");
                e.printStackTrace();
                break;
            }
        }
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
    public String getPlanKey(String patchJsonString) throws JsonProcessingException {
        NodeModel patchNodeModel = NodeModelFactoryFromJson.fromJsonString(patchJsonString);
        return patchNodeModel.objectKey;
    }

//    @Override
//    public String patchGraph(String jsonSchemaFilePath, String patchJsonString) throws JsonProcessingException {
//        NodeModel patchNodeModel = NodeModelFactoryFromJson.fromJsonString(patchJsonString);
//        NodeModel existingPlan = getPlanData(jsonSchemaFilePath, patchNodeModel.objectKey);
//        existingPlan.patch(patchNodeModel);
//        boolean isPatchedPlanValid = JsonSchemaUtil.validate(jsonSchemaFilePath, existingPlan.getJsonString());
//        if (!isPatchedPlanValid) {
//            throw new RuntimeException("Invalid patch");
//        }
//        deleteGraph(jsonSchemaFilePath, existingPlan.objectKey);
//        addGraph(existingPlan.getJsonString());
//        return existingPlan.getJsonString();
//    }
}
