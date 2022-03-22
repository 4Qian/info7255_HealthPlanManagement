package com.example.demo.controller;

import com.example.demo.exception.*;
import com.example.demo.model.response.ObjectTypeAndIdResponse;
import com.example.demo.permissions.ResourcePermission;
import com.example.demo.service.AuthorizationService;
import com.example.demo.service.PlanService;
import com.example.demo.utils.DigestUtil;
import com.example.demo.utils.JsonSchemaUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    private AuthorizationService authorizationService;

    @Autowired
    public PlanController(PlanService studentService) {
        this.planService = studentService;
    }

    @GetMapping(path="{id}", produces = "application/plan+json;planVersion=1.0")
    // https://stackoverflow.com/questions/41278484/springs-support-for-if-match-header //  @RequestHeader("If-None-Match") String ifNonMatch
    // https://asbnotebook.com/etags-in-restful-services-spring-boot/
    public ResponseEntity<String> getGraph(@PathVariable("id") String objectId,
                           @RequestHeader(value = "Authorization", required = false) String idToken,
                           @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatchHeader) {

        String key = "plan___" + objectId;
        boolean authorized = authorizationService.authorizeIdToken(idToken, ResourcePermission.Operation.READ, key);
        if (!authorized) {
            throw new UnauthorizedException("not authorized");
        }
        Optional<String> plan = planService.getGraph(planSchemaFile, key);
        if (plan.isEmpty()) { // not exist
            throw new ResourceNotExistException("Plan with the specified id does not exist");
        }
        if (ifNoneMatchHeader != null && ifNoneMatchHeader.equals(DigestUtil.getEtagQuoted(plan.get()))) {
            throw new ResourceNotModifiedException("resource not modified");
        }
//        https://stackoverflow.com/questions/18584196/etag-support-in-spring-for-versioned-entity
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Etag", DigestUtil.getEtag(plan.get()));
        // https://stackoverflow.com/questions/45152484/how-to-set-respond-header-values-in-spring-boot-rest-service-method
        return new ResponseEntity<>(plan.get(), responseHeaders, HttpStatus.OK);
    }

    @PostMapping(produces = "application/plan+json;planVersion=1.0")
    @ResponseStatus(code = HttpStatus.CREATED)//201
    public ResponseEntity<ObjectTypeAndIdResponse> addGraph(@RequestBody String planPayload,
                                                            @RequestHeader(value = "Authorization", required = false) String idToken) throws JsonProcessingException {
        boolean authorized = authorizationService.authorizeIdToken(idToken, ResourcePermission.Operation.ADD, null);
        if (!authorized) {
            throw new UnauthorizedException("not authorized");
        }
        boolean isValidPayload = JsonSchemaUtil.validate(planSchemaFile, planPayload);
        if (!isValidPayload) {
            throw new PayloadValidationException("The plan payload is invalid");
        }
        Optional<ObjectTypeAndIdResponse> objectTypeAndId = planService.addGraph(planPayload);
        if (objectTypeAndId.isEmpty()) {
            throw new ResourceAlreadyExistException("plan already existed");
        }
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Etag", DigestUtil.getDigest(planService.getGraph(planSchemaFile, planKey)));
//        // https://stackoverflow.com/questions/45152484/how-to-set-respond-header-values-in-spring-boot-rest-service-method
        return new ResponseEntity<>(objectTypeAndId.get(), HttpStatus.CREATED);
    }

    @PatchMapping(produces = "application/plan+json;planVersion=1.0")
    public ResponseEntity<String> patchGraph(@RequestBody String planPatchPayload,
                             @RequestHeader(value = "Authorization", required = false) String idToken,
                             @RequestHeader(value = "If-Match", required = false) String ifMatchHeader) throws JsonProcessingException {
        boolean authorized = authorizationService.authorizeIdToken(idToken, ResourcePermission.Operation.UPDATE, null);
        if (!authorized) {
            return new ResponseEntity<>("not authorized", HttpStatus.UNAUTHORIZED);
        }

        String planKey = planService.extractPlanKey(planPatchPayload);
        Optional<String> existingData = planService.getGraph(planSchemaFile, planKey);
        if (existingData.isEmpty()) {
            throw new ResourceNotExistException("resource does not exist");
        }
        if (ifMatchHeader != null && !ifMatchHeader.equals(DigestUtil.getEtagQuoted(existingData.get()))) {
            throw new PreconditionFailedException("modified since last GET request");
        }
        String updatedData = planService.patchGraph(planSchemaFile, planPatchPayload);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping(path="{id}")
    // localhost:8080/api/student/12345
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ObjectTypeAndIdResponse deleteGraph(@PathVariable("id") String objectId,
                                               @RequestHeader(value = "Authorization", required = false) String idToken) {
        String key =  "plan___" + objectId;
        boolean authorized = authorizationService.authorizeIdToken(idToken, ResourcePermission.Operation.DELETE, key);
        if (!authorized) {
            throw new UnauthorizedException("not authorized");
        }
        Optional<ObjectTypeAndIdResponse> removedObjectTypeAndId = planService.deleteGraph(planSchemaFile, key);
        if (removedObjectTypeAndId.isEmpty()) {
            throw new ResourceNotExistException("The plan with the specified id does not exist");
        }
        return removedObjectTypeAndId.get();
    }
}
