package com.example.demo.dao;

import com.example.demo.model.Plan;
import com.example.demo.model.response.ObjectTypeAndIdResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface PlanDao {
    String selectPlanById(String id);

    Plan insertPlan(String plan);
    String getGraphById(String jsonSchemaFilePath, String planObjectKey);
    String deletePlan(String id);
    ObjectTypeAndIdResponse deleteGraph(String planSchemaFile, String id);
    ObjectTypeAndIdResponse addGraph(String jsonString) throws JsonProcessingException;
    String getPlanKey(String patchJsonString) throws JsonProcessingException;
    String patchGraph(String jsonSchemaFilePath, String patchJsonString) throws JsonProcessingException;
}
