package com.example.demo.dao;

import com.example.demo.model.Plan;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface PlanDao {
    String selectPlanById(String id);
    Plan insertPlan(String plan);
    String deletePlan(String id);
    void addGraph(String jsonString) throws JsonProcessingException;
}
