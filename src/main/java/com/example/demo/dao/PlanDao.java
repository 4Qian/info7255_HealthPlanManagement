package com.example.demo.dao;

import com.example.demo.model.Plan;

public interface PlanDao {
    String selectPlanById(String id);
    Plan insertPlan(String plan);
    String deletePlan(String id);
}
