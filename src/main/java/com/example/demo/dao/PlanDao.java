package com.example.demo.dao;

import com.example.demo.model.Plan;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface PlanDao {
    String selectPlanById(String id);

    Plan insertPlan(String plan);
    String getGraphById(String jsonSchemaFilePath, String planObjectKey);
    String deletePlan(String id);
    String deleteGraph(String planSchemaFile, String id);
    void addGraph(String jsonString) throws JsonProcessingException;
    String patchGraph(String jsonSchemaFilePath, String patchJsonString) throws JsonProcessingException;
}
