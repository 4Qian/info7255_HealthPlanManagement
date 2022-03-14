package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonObjectProcessTest {

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

        JsonObjectProcess jsonObjectProcess = new JsonObjectProcess();
        jsonObjectProcess.parseJson(jsonString);

        System.out.println(jsonObjectProcess.getAllEdges());
        System.out.println(jsonObjectProcess.getAllEdges());

    }
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void isComplexNode() throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        JsonObjectProcess jsonObjectProcess = new JsonObjectProcess();
        assertTrue(jsonObjectProcess.isComplexNode(jsonNode));
    }

    @Test
    void isComplexNode_simple() throws JsonProcessingException {
        String json = "{\"abc\": \"abc\"}";
        JsonNode jsonNode = objectMapper.readTree(json);
        JsonNode shouldBeSimple = jsonNode.get("abc");
        JsonObjectProcess jsonObjectProcess = new JsonObjectProcess();
        assertFalse(jsonObjectProcess.isComplexNode(shouldBeSimple));
    }

    @Test
    void isComplexNode_simple2() throws JsonProcessingException {
        String json = "[\"abc\", \"abcd\"]";
        JsonNode jsonNode = objectMapper.readTree(json);
        JsonObjectProcess jsonObjectProcess = new JsonObjectProcess();
        assertFalse(jsonObjectProcess.isComplexNode(jsonNode));
    }

    @Test
    void isComplexNode_complex2() throws JsonProcessingException {

        String json = "[{\"a\":\"b\"}, {\"ad\":\"s\"}]";
        JsonNode jsonNode = objectMapper.readTree(json);
        JsonObjectProcess jsonObjectProcess = new JsonObjectProcess();
        assertTrue(jsonObjectProcess.isComplexNode(jsonNode));
    }
}