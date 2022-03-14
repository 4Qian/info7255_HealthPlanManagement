package com.example.demo.service;

import com.example.demo.dao.PlanDao;
import com.example.demo.model.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
