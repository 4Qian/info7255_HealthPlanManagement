package com.example.demo.controller;

import com.example.demo.exception.PayloadValidationException;
import com.example.demo.exception.ResourceAlreadyExistException;
import com.example.demo.exception.ResourceNotExistException;
import com.example.demo.model.*;
import com.example.demo.model.response.ObjectIdResponse;
import com.example.demo.redis.RedisService;
import com.example.demo.service.PlanService;
import com.example.demo.utils.DataValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("plan") //http://localhost:8080/plan/
public class PlanController<main> {
    // https://json-schema.org/learn/getting-started-step-by-step.html
    // https://json-schema.org/learn/miscellaneous-examples.html
    // https://github.com/json-schema-org/json-schema-spec
    // https://stackoverflow.com/questions/27224310/json-schema-validation-do-not-allow-fields-other-than-those-declared-in-schema
    @Value("${planSchema.path}")
    private String planSchemaFile;

    private PlanService planService;
    @Autowired
    public PlanController(PlanService studentService) {
        this.planService = studentService;
    }

    // https://www.springboottutorial.com/spring-boot-versioning-for-rest-services
    // version our RESTful API?
    // approach4: Media type versioning (a.k.a “content negotiation” or “accept header”)
    // GET http://localhost:8080/plan/{id}
    @GetMapping(path="{id}", produces = "application/plan+json;planVersion=1.0")
    // https://stackoverflow.com/questions/41278484/springs-support-for-if-match-header //  @RequestHeader("If-None-Match") String ifNonMatch
    // https://asbnotebook.com/etags-in-restful-services-spring-boot/
    public String getPlan(@PathVariable("id") String id) {
        String plan = planService.getPlan(id);
        if (plan == null) { // not exist
            throw new ResourceNotExistException("Plan with the specified id does not exist");
        }
//        https://stackoverflow.com/questions/18584196/etag-support-in-spring-for-versioned-entity
        return plan;
    }

//    //GET http://localhost:8080/plan/map/{id}
//    @GetMapping(path="addGraph", produces = "application/plan+json;planVersion=1.0")
//    public String testAddingMap(@PathVariable("key") String key) {
////        planService.addNode(key, Map.of("name", "zun", "email", "fsdf@gmail.com", "age", 30));
////        planService.addEdge(key, List.of("planservice__27283xvx9asdff-504__linkedService", "planservice__27283xvx9asdff-504__planserviceCostShares"));
//
//        return "";
//    }
    @PostMapping(path="addGraph", produces = "application/plan+json;planVersion=1.0")
    @ResponseStatus(code = HttpStatus.CREATED)//201
    public ObjectIdResponse testAddingGraph(@RequestBody String planPayload) throws JsonProcessingException {
        boolean isValidPayload = DataValidator.validate(planSchemaFile, planPayload);
        if (!isValidPayload) {
            throw new PayloadValidationException("The plan payload is invalid");
        }
        planService.addGraph(planPayload);
        return new ObjectIdResponse("add success");
    }

    /**
     * why not return Etag in POST API?
     * https://en.wikipedia.org/wiki/HTTP_ETag
     * In typical usage, when a URL is retrieved, the Web server will return the resource's current representation along with its corresponding ETag value, which is placed in an HTTP response header "ETag" field:
     *
     * A good discussion: https://stackoverflow.com/questions/42246577/why-responses-to-put-requests-must-not-provide-an-etag
     * The key point is that the server may, or may not, alter the representation before storing it
     * @param planPayload
     * @return
     */
    // POST http://localhost:8080/plan
    @PostMapping(produces = "application/plan+json;planVersion=1.0")
    @ResponseStatus(code = HttpStatus.CREATED)//201
    public ObjectIdResponse addPlan(@RequestBody String planPayload) {
        boolean isValidPayload = DataValidator.validate(planSchemaFile, planPayload);
        if (!isValidPayload) {
            throw new PayloadValidationException("The plan payload is invalid");
        }
        Plan insertedPlan = planService.addPlan(planPayload);
        if (insertedPlan == null) { // meaning that the object already exist
            // https://stackoverflow.com/questions/3825990/http-response-code-for-post-when-resource-already-exists#:~:text=It%20seems%20that%20409%20Conflict,incompatible%20with%20its%20current%20state.
            // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/409
            //  indicates a request conflict with the current state of the target resource
            throw new ResourceAlreadyExistException("The plan with the specified id already exists");
        }
        // https://stackoverflow.com/questions/68993031/spring-boot-mvc-how-to-generate-etag-value-of-an-entity-programatically
        return new ObjectIdResponse(insertedPlan.getId());
    }

    // DELETE http://localhost:8080/plan/这里换成id
    @DeleteMapping(path="{id}")
    // localhost:8080/api/student/12345
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ObjectIdResponse deletePlan(@PathVariable("id") String id) {
        String removedPlanId = planService.deletePlan(id);
        if (removedPlanId == null) {
            throw new ResourceNotExistException("The plan with the specified id does not exist");
        }
        return new ObjectIdResponse(removedPlanId);
    }
}
