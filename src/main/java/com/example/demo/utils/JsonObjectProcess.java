package com.example.demo.utils;

import com.example.demo.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class JsonObjectProcess {

    private static ObjectMapper objectMapper = new ObjectMapper();
//    public static void convertT(String jsonPayload) throws JsonProcessingException {
//        JsonNode jsonNode = objectMapper.readTree(jsonPayload);
//        Map<String, String> map = new HashMap<>();
//        addKeys("", jsonNode, map, new ArrayList<>());
//
//        map.entrySet()
//                .forEach(System.out::println);
//    }

    boolean isComplexNode(JsonNode jsonNode) {
        if (jsonNode.isValueNode()) {
            return false;
        }
        if (jsonNode.isObject()) { // entry is complex
            return true;
        }
        if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            boolean isComplex = false;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode subNode = arrayNode.get(i);
                if (isComplexNode(subNode)) {
                    isComplex = true;
                    break;
                }
            }
            return isComplex;
        }
        throw new RuntimeException("json node is missing type");
    }

    public Map<String, Map<String, Object>> getAllNodes() {
        return allNodes;
    }

    public Map<String, List<String>> getAllEdges() {
        return allEdges;
    }

    private Map<String, Map<String, Object>> allNodes = new HashMap<>();
    private Map<String, List<String>> allEdges = new HashMap<>();

    private Pair<String, Object> processEntry(String objectKey, Map.Entry<String, JsonNode> entry) {
        String propertyName = entry.getKey();
        JsonNode propertyValue = entry.getValue();
        if (!isComplexNode(propertyValue)) {
            return Pair.of(propertyName, propertyValue.asText());
        }
        List<String> objectTypeAndIdList = processNode(propertyValue);
        allEdges.put(objectKey + "___" + propertyName, objectTypeAndIdList);
        return null;
    }

    public void parseJson(String jsonString) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        processNode(jsonNode);
    }

    /**
     *
     * @param jsonNode
     * @return
     */
    private List<String> processNode(JsonNode jsonNode) {
        if (jsonNode.isObject()) { // starting with {
            String currentNodeIdAndType = jsonNode.get("objectType") + "___" + jsonNode.get("objectId");
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
//            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "___";
            Map<String, Object> simpleProperties = new HashMap<>(); // will go into redis later
            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                Pair<String, Object> simpleProperty = processEntry(currentNodeIdAndType, entry);
                if (simpleProperty != null) {
                    simpleProperties.put(simpleProperty.getLeft(), simpleProperty.getRight());
                }
            }
            // add current node to global variables
            allNodes.put(currentNodeIdAndType, simpleProperties);
            return Arrays.asList(currentNodeIdAndType);
        } else if (jsonNode.isArray()) { // starting with [
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            List<String> allObjectKeys = new ArrayList<>();
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode subNode = arrayNode.get(i);
                List<String> objectKeys = processNode(subNode);
                allObjectKeys.addAll(objectKeys);
            }
            return allObjectKeys;
        } else {
            throw new RuntimeException("can not call processNode method on simple node");
        }
    }

//    private static void addKeys(String currentPath, JsonNode jsonNode, Map<String, String> map, List<Integer> suffix) {
//        if (jsonNode.isObject()) { // starting with {
//            ObjectNode objectNode = (ObjectNode) jsonNode;
//            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
//            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "-";
//
//            while (iter.hasNext()) {
//                Map.Entry<String, JsonNode> entry = iter.next();
//                addKeys(pathPrefix + entry.getKey(), entry.getValue(), map, suffix);
//            }
//        } else if (jsonNode.isArray()) { // starting with [
//            ArrayNode arrayNode = (ArrayNode) jsonNode;
//
//            for (int i = 0; i < arrayNode.size(); i++) {
//                suffix.add(i + 1);
//                addKeys(currentPath, arrayNode.get(i), map, suffix);
//
//                if (i + 1 < arrayNode.size()){
//                    suffix.remove(arrayNode.size() - 1); //backtracking
//                }
//            }
//        } else if (jsonNode.isValueNode()) { // neither { nor [
//            if (currentPath.contains("-")) {
//                for (int i = 0; i < suffix.size(); i++) {
//                    currentPath += "-" + suffix.get(i);
//                }
//                suffix = new ArrayList<>();
//            }
//            ValueNode valueNode = (ValueNode) jsonNode;
//            map.put(currentPath, valueNode.asText());
//        }
//    }
}
