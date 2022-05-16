package com.example.demo.model;

import com.example.demo.model.response.ObjectTypeAndIdResponse;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeModel {
    public String objectKey;
    public TreeMap<String, String> simpleProperties;
    public TreeMap<String, NodeModel> complexObjectProperties;
    public TreeMap<String, List<NodeModel>> complexArrayProperties;

    public NodeModel(String objectKey) {
        this.objectKey = objectKey;
        simpleProperties = new TreeMap<>();
        complexObjectProperties = new TreeMap<>();
        complexArrayProperties = new TreeMap<>();
    }

    public ObjectTypeAndIdResponse getObjectTypeAndId() {
        String[] pair = objectKey.split("___");
        return new ObjectTypeAndIdResponse(pair[0], pair[1]);
    }

    public void patch(NodeModel patchData) {
        if (!patchData.objectKey.equals(objectKey)) {
            throw new RuntimeException("");
        }
        for (String key : patchData.simpleProperties.keySet()) {
            simpleProperties.put(key, patchData.simpleProperties.get(key));
        }
        for (String key : patchData.complexObjectProperties.keySet()) {
            NodeModel patchTarget = patchData.complexObjectProperties.get(key);
            if (!complexObjectProperties.containsKey(key)) {
                complexObjectProperties.put(key, patchTarget);
            } else {
                NodeModel existingTarget = complexObjectProperties.get(key);
                existingTarget.patch(patchTarget);
            }
        }
        for (String key : patchData.complexArrayProperties.keySet()) {
            List<NodeModel> patchTargets = patchData.complexArrayProperties.get(key);
            if (!complexArrayProperties.containsKey(key)) {
                complexArrayProperties.put(key, patchTargets);
            } else {
                List<NodeModel> existingTargets = complexArrayProperties.get(key);
                Map<String, NodeModel> existingTargetsMap = existingTargets.stream()
                        .collect(Collectors.toMap(existingTarget -> existingTarget.objectKey, Function.identity()));
                Map<String, NodeModel> patchTargetsMap = patchTargets.stream()
                        .collect(Collectors.toMap(existingTarget -> existingTarget.objectKey, Function.identity()));
                Stream<Map.Entry<String, NodeModel>> combined = Stream.concat(existingTargetsMap.entrySet().stream(), patchTargetsMap.entrySet().stream());
                Map<String, NodeModel> updated = combined.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (v1, v2) -> {
                    v1.patch(v2);
                    return v1;
                }));
                complexArrayProperties.put(key, new ArrayList<>(updated.values()));
            }
        }
    }

    /**
     * This object represents the plan object.
     * This method converts the object to the corresponding elastic search requests.
     *
     * @param joinTypeName
     * @param parentId
     * @return
     */
    public List<ESRequest> getElasticSearchRequests(String joinTypeName, Optional<String> parentId) {
        List<ESRequest> res = new ArrayList<>();

        List<String> entryStringList = new ArrayList<>();
        for (String propertyName : simpleProperties.keySet()) {
            entryStringList.add(String.format("\"%s\": %s", propertyName, simpleProperties.get(propertyName)));
        }
        boolean needRouting = false;
        if (parentId.isEmpty()) {
            entryStringList.add(String.format("\"plan_join_field\": \"%s\"", joinTypeName));
        } else {
            entryStringList.add(String.format("\"plan_join_field\": {" +
                    "\"name\": \"%s\"," +
                    "\"parent\": \"%s\"" +
                    "}", joinTypeName, parentId.get()));
            needRouting = true;
        }
        String requestBody = "{" + entryStringList.stream().collect(Collectors.joining(",")) + "}";
        res.add(new ESRequest("plan", objectKey, requestBody, needRouting));

        for (Map.Entry<String, NodeModel> entry: complexObjectProperties.entrySet()) {
            String edgeName = entry.getKey();
            NodeModel targetNodeModel = entry.getValue();
            List<ESRequest> childrenRequestBodies = targetNodeModel.getElasticSearchRequests(edgeName, Optional.of(objectKey));
            res.addAll(childrenRequestBodies);
        }
        for (Map.Entry<String, List<NodeModel>> entry: complexArrayProperties.entrySet()) {
            String edgeName = entry.getKey();
            List<NodeModel> targetNodeModelList = entry.getValue();
            for (NodeModel targetNodeModel : targetNodeModelList) {
                List<ESRequest> childrenRequestBodies = targetNodeModel.getElasticSearchRequests(edgeName, Optional.of(objectKey));
                res.addAll(childrenRequestBodies);
            }
        }
        return res;
    }

    public String getJsonString() {
        List<String> entryStringList = new ArrayList<>();
        // process complex properties first
        for (Map.Entry<String, NodeModel> entry: complexObjectProperties.entrySet()) {
            String edgeName = entry.getKey();
            NodeModel targetNodeModel = entry.getValue();
            String complexArrayPropertyString = String.format("\"%s\": %s",
                    edgeName,
                    targetNodeModel.getJsonString());
            entryStringList.add(complexArrayPropertyString);
        }
        for (Map.Entry<String, List<NodeModel>> entry: complexArrayProperties.entrySet()) {
            String edgeName = entry.getKey();
            List<NodeModel> targetNodeModelList = entry.getValue();
            String complexArrayPropertyString = String.format("\"%s\": [%s]",
                    edgeName,
                    targetNodeModelList.stream()
                            .map(targetNodeModel -> targetNodeModel.getJsonString())
                            .collect(Collectors.joining(",")));
            entryStringList.add(complexArrayPropertyString);
        }
        // then construct simple properties
        for (String propertyName : simpleProperties.keySet()) {
            entryStringList.add(String.format("\"%s\": %s", propertyName, simpleProperties.get(propertyName)));
        }
        return "{" + entryStringList.stream().collect(Collectors.joining(",")) + "}";
    }

    @Override
    public String toString() {
        return "NodeModel{" +
                "objectKey='" + objectKey + '\'' +
                ", simpleProperties=" + simpleProperties +
                ", complexObjectProperties=" + complexObjectProperties +
                ", complexArrayProperties=" + complexArrayProperties +
                '}';
    }
}
