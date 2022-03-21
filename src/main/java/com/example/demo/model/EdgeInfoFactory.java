package com.example.demo.model;

import com.example.demo.utils.JsonSchemaUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;

import java.util.Iterator;
import java.util.Map;

public class EdgeInfoFactory {
    /**
     *
     * @param jsonSchemaFilePath
     * @return
     */
    public static EdgesInfo fromJsonSchema(final String jsonSchemaFilePath) {
        // step1: get jsonSchema object by reading file
        JsonSchema jsonSchema = JsonSchemaUtil.getJsonSchemaObject(jsonSchemaFilePath);
        JsonNode rootSchema = jsonSchema.getSchemaNode();

        EdgesInfo rootEdgesInfo = getEdgesInfo(rootSchema, rootSchema);
        return rootEdgesInfo;
    }
    /**
     * if schemaNode is simple, return null
     * otherwise, return Node
     * @param curSchemaJsonNode
     * @return
     */
    private static EdgesInfo getEdgesInfo(JsonNode rootSchema, JsonNode curSchemaJsonNode) {
        JsonNode type = curSchemaJsonNode.get("type");
        JsonNode $ref = curSchemaJsonNode.get("$ref"); // link style
        if (type != null) { // regular style
            if ("object".equals(type.asText())) {
                EdgesInfo res = new EdgesInfo();
                res.arrayOrObject = EdgesInfo.ArrayOrObject.OBJECT;
                JsonNode properties = curSchemaJsonNode.get("properties");
                for (Iterator<Map.Entry<String, JsonNode>> it = properties.fields(); it.hasNext(); ) {
                    Map.Entry<String, JsonNode> propertyEntry = it.next();
                    JsonNode propertyValueSchema = propertyEntry.getValue(); //得到冒号后面的部分
                    //recursive
                    EdgesInfo edgesInfo = getEdgesInfo(rootSchema, propertyValueSchema);
                    if (edgesInfo == null) {
                        continue; // filter out simple properties
                    }
                    res.edges.put(propertyEntry.getKey(), edgesInfo);
                }
                return res;
            } else if ("array".equals(type.asText())) {
                JsonNode items = curSchemaJsonNode.get("items");
                // recursive
                EdgesInfo edgesInfo = getEdgesInfo(rootSchema, items);
                edgesInfo.arrayOrObject = EdgesInfo.ArrayOrObject.ARRAY;
                return edgesInfo;
            } else {
                return null;
            }
        } else if ($ref != null) {
            // if the schema node is in the form of "$ref"
            // then we need to locate the actual schama node by searching the path
            String refPath = $ref.asText();
            String[] paths = refPath.split("/");
            if (!paths[0].equals("#")) {
                throw new RuntimeException("path not starting with #");
            }
            JsonNode curNode = rootSchema;
            for (int i = 1; i < paths.length; i++) {
                curNode = curNode.get(paths[i]);
            }
            // now the actual schemaNode is found
            return getEdgesInfo(rootSchema, curNode);
        } else {
            throw new RuntimeException("unexpected");
        }
    }
}
