package com.example.demo.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NodeModelFactoryFromJson {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static NodeModel fromJsonString(String jsonString) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        NodeModel nodeModel = fromJsonNode(jsonNode);
        return nodeModel;
    }
    /**
     * convert a json object to a NodeModel
     * jsonNode must be a json object
     *
     * @param jsonNode
     * @return
     */
    private static NodeModel fromJsonNode(JsonNode jsonNode) {
        String currentNodeKey = jsonNode.get("objectType").asText() + "___" + jsonNode.get("objectId").asText();
        NodeModel nodeModel = new NodeModel(currentNodeKey); // node model for current jsonNode
        ObjectNode objectJsonNode = (ObjectNode) jsonNode;
        Iterator<Map.Entry<String, JsonNode>> jsonNodePropertiesIterator = objectJsonNode.fields();
        while (jsonNodePropertiesIterator.hasNext()) {
            Map.Entry<String, JsonNode> propertyEntry = jsonNodePropertiesIterator.next();
            Pair<String, Object> entryModel = processEntry(propertyEntry);
            if (entryModel.getRight() instanceof String) {
//                    simpleProperties.put(entryModel.getLeft(), entryModel.getRight());
                nodeModel.simpleProperties.put(entryModel.getLeft(), (String) (entryModel.getRight()));
            } else if (entryModel.getRight() instanceof List) {
                nodeModel.complexArrayProperties.put(entryModel.getLeft(), (List<NodeModel>) (entryModel.getRight()));
            } else {
                nodeModel.complexObjectProperties.put(entryModel.getLeft(), (NodeModel) (entryModel.getRight()));
            }
        }
        return nodeModel;
    }

    static boolean isComplexNode(JsonNode jsonNode) {
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

    private static Pair<String, Object> processEntry(Map.Entry<String, JsonNode> propertyEntry) {
        String propertyName = propertyEntry.getKey();
        JsonNode propertyValueJsonNode = propertyEntry.getValue();
        if (!isComplexNode(propertyValueJsonNode)) {
            if (propertyValueJsonNode.isTextual()) {
                return Pair.of(propertyName, "\"" + propertyValueJsonNode.asText() + "\"" );
            }
            return Pair.of(propertyName, propertyValueJsonNode.asText());
        } else if (propertyValueJsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) propertyValueJsonNode;
            List<NodeModel> allModelList = new ArrayList<>();
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode jsonNodeInList = arrayNode.get(i);
                //TODO:
                NodeModel nodeModel = fromJsonNode(jsonNodeInList);
                allModelList.add(nodeModel);
            }
            return Pair.of(propertyName, allModelList);
        } else {
            NodeModel nodeModel = fromJsonNode(propertyValueJsonNode);
            return Pair.of(propertyName, nodeModel);
        }
    }
}
