package com.example.demo.service;

import com.example.demo.dao.PlanDao;
import com.example.demo.model.Plan;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlanService {

    private PlanDao planDao;

    @Autowired()
    public PlanService(PlanDao planDao) {
        this.planDao = planDao;
    }

    /**
     * if the plan object eixist in redis, return the object payload
     * otherwise, return null
     * @param id
     * @return   item or null
     */
    public String getPlan(String id) {
        return planDao.selectPlanById(id);
    }

    public String getGraph(String jsonSchemaFile, String planKey) {
        return planDao.getGraphById(jsonSchemaFile, planKey);
    }

    /**
     *
     * @param planPayload
     * @return id if return null, meaning the object already exist. or the objectId conflcts with the existing object Ids in redis
     */
    public Plan addPlan(String planPayload) {
        return planDao.insertPlan(planPayload);
    }

    /**
     * if return null, it means the plan does not exist
     * @param id
     * @return removed item's id or null
     */
    public String deletePlan(String id) {
        return planDao.deletePlan(id);
    }

    public String deleteGraph(String planSchemaFile, String id) {
        return planDao.deleteGraph(planSchemaFile, id);
    }

    public void addGraph(String jsonString) throws JsonProcessingException {
        planDao.addGraph(jsonString);
    }

    public String patchGraph(String jsonSchemaFilePath, String patchJsonString) throws JsonProcessingException {
        return planDao.patchGraph(jsonSchemaFilePath, patchJsonString);
    }
}
