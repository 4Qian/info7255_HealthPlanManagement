package com.example.demo.service;

import com.example.demo.dao.PlanDao;
import com.example.demo.model.Plan;
import com.example.demo.model.response.ObjectTypeAndIdResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<String> getGraph(String jsonSchemaFile, String planKey) {
        try {
            return Optional.of(planDao.getGraphById(jsonSchemaFile, planKey));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public String extractPlanKey(String patchPayload) throws JsonProcessingException {
        return planDao.getPlanKey(patchPayload);
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

    public Optional<ObjectTypeAndIdResponse> deleteGraph(String planSchemaFile, String id) {
        try {
            return Optional.of(planDao.deleteGraph(planSchemaFile, id));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * return plan key
     *
     * @param jsonString
     * @return
     * @throws JsonProcessingException
     */
    public Optional<ObjectTypeAndIdResponse> addGraph(String jsonString) throws JsonProcessingException {
        try {
            return Optional.of(planDao.addGraph(jsonString));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<String> patchGraph(String jsonSchemaFilePath, String patchJsonString) throws JsonProcessingException {
        try {
            return Optional.of(planDao.patchGraph(jsonSchemaFilePath, patchJsonString));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
