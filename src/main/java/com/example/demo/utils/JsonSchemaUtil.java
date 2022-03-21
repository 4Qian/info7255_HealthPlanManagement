package com.example.demo.utils;

import com.example.demo.model.EdgesInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JsonSchemaUtil {
    // https://github.com/networknt/json-schema-validator
    private static JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
    private static ObjectMapper objectMapper = new ObjectMapper();





    /**
     * validate json payload with json schema defined in a local file.
     *
     * @param jsonSchemaFilePath   file containing the json schema
     * @param jsonPayload    the json payload to be validated
     * @return
     */
    public static boolean validate(final String jsonSchemaFilePath, final String jsonPayload) {
        // step1: get jsonSchema object by reading file
        JsonSchema jsonSchema = getJsonSchemaObject(jsonSchemaFilePath);
        // step2: validate payload using jsonSchema object
        return validatePayloadAgainstSchema(jsonSchema, jsonPayload);
    }

    public static JsonSchema getJsonSchemaObject(String jsonSchemaFilePath) {
//        https://www.baeldung.com/introduction-to-json-schema-in-java
//        https://stackoverflow.com/questions/64568986/validation-of-json-schema-in-java
        String schemaStr = FileReader.readFile(jsonSchemaFilePath);
        JsonSchema jsonSchema = null;
        try {
            jsonSchema = factory.getSchema(schemaStr);
        } catch (JsonSchemaException e) {
            System.out.println("json schema invalid: \n" + e);
        }
        return jsonSchema;
    }

    private static boolean validatePayloadAgainstSchema(JsonSchema jsonSchema, String jsonPayload ) {
        // validate
        // convert pauload string to JsonNode
        // http://tutorials.jenkov.com/java-json/jackson-jsonnode.html
        JsonNode payloadJsonNode = null;
        try {
            payloadJsonNode = objectMapper.readTree(jsonPayload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Set<ValidationMessage> validateMessage = jsonSchema.validate(payloadJsonNode);

        if (!validateMessage.isEmpty()) {
            System.out.println(validateMessage);
        }
        return validateMessage.isEmpty();
    }
}
