package com.example.demo.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeModelFactoryFromJsonTest {

    private static String jsonString = "{\n" +
            "   \"planCostShares\":{\n" +
            "      \"deductible\":2000,\n" +
            "      \"_org\":\"example.com\",\n" +
            "      \"copay\":23,\n" +
            "      \"objectId\":\"1234vxc2324sdf-501\",\n" +
            "      \"objectType\":\"membercostshare\"\n" +
            "   },\n" +
            "   \"linkedPlanServices\":[\n" +
            "      {\n" +
            "         \"linkedService\":{\n" +
            "            \"_org\":\"example.com\",\n" +
            "            \"objectId\":\"1234520xvc30asdf-502\",\n" +
            "            \"objectType\":\"service\",\n" +
            "            \"name\":\"Yearly physical\"\n" +
            "         },\n" +
            "         \"planserviceCostShares\":{\n" +
            "            \"deductible\":10,\n" +
            "            \"_org\":\"example.com\",\n" +
            "            \"copay\":0,\n" +
            "            \"objectId\":\"1234512xvc1314asdfs-503\",\n" +
            "            \"objectType\":\"membercostshare\"\n" +
            "         },\n" +
            "         \"_org\":\"example.com\",\n" +
            "         \"objectId\":\"27283xvx9asdff-504\",\n" +
            "         \"objectType\":\"planservice\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"linkedService\":{\n" +
            "            \"_org\":\"example.com\",\n" +
            "            \"objectId\":\"1234520xvc30sfs-505\",\n" +
            "            \"objectType\":\"service\",\n" +
            "            \"name\":\"well baby\"\n" +
            "         },\n" +
            "         \"planserviceCostShares\":{\n" +
            "            \"deductible\":10,\n" +
            "            \"_org\":\"example.com\",\n" +
            "            \"copay\":175,\n" +
            "            \"objectId\":\"1234512xvc1314sdfsd-506\",\n" +
            "            \"objectType\":\"membercostshare\"\n" +
            "         },\n" +
            "         \"_org\":\"example.com\",\n" +
            "         \"objectId\":\"27283xvx9sdf-507\",\n" +
            "         \"objectType\":\"planservice\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"_org\":\"example.com\",\n" +
            "   \"objectType\":\"plan\",\n" +
            "\t \"objectId\":\"abdcde\",\n" +
            "   \"planType\":\"inNetwork\",\n" +
            "   \"creationDate\":\"12-12-2017\"\n" +
            "}";

    @Test
    void parseJson() throws JsonProcessingException {

        NodeModel nodeModel = NodeModelFactoryFromJson.fromJsonString(jsonString);

        RedisData redisData = new RedisData(nodeModel);
        System.out.println(redisData.allNodes);
        System.out.println(redisData.allEdges);

    }
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void isComplexNode() throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        assertTrue(NodeModelFactoryFromJson.isComplexNode(jsonNode));
    }

    @Test
    void isComplexNode_simple() throws JsonProcessingException {
        String json = "{\"abc\": \"abc\"}";
        JsonNode jsonNode = objectMapper.readTree(json);
        JsonNode shouldBeSimple = jsonNode.get("abc");
        assertFalse(NodeModelFactoryFromJson.isComplexNode(shouldBeSimple));
    }

    @Test
    void isComplexNode_simple2() throws JsonProcessingException {
        String json = "[\"abc\", \"abcd\"]";
        JsonNode jsonNode = objectMapper.readTree(json);
        assertFalse(NodeModelFactoryFromJson.isComplexNode(jsonNode));
    }

    @Test
    void isComplexNode_complex2() throws JsonProcessingException {

        String json = "[{\"a\":\"b\"}, {\"ad\":\"s\"}]";
        JsonNode jsonNode = objectMapper.readTree(json);
        assertTrue(NodeModelFactoryFromJson.isComplexNode(jsonNode));
    }
}