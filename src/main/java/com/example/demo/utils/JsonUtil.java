package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.util.Pair;

import java.util.UUID;

/**
 * Helper class for processing json text
 */
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Try to extract the "objectId" property from the json string.
     * If "objectId" does not exist in the json string, insert a random objectId in the string
     *
     * @param jsonStr
     * @return  a pair of objectId and the json string with the objectId
     */
    public static Pair<String, String> insertObjectIdIfNotExist(String jsonStr) {
        JsonNode payloadJsonNode = null;
        try {
            payloadJsonNode = objectMapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        JsonNode objectId = payloadJsonNode.get("objectId");
        if (objectId == null) {
            String id = UUID.randomUUID().toString();
            // insert the newly created object id back to the json node
            ((ObjectNode) payloadJsonNode).put("objectId", id);
            return Pair.of(id, payloadJsonNode.toString());
        }
        return Pair.of(objectId.asText(), jsonStr);
    }
}
