package com.example.demo.model;

import com.example.demo.model.EdgeInfoFactory;
import org.junit.jupiter.api.Test;

class EdgeInfoFactoryTest {

    @Test
    void fromJsonSchema() {
        final String jsonSchemaFilePath = "./src/main/resources/plan_schema.json";
        EdgeInfoFactory.fromJsonSchema(jsonSchemaFilePath);
    }

//    @Test
//    void validate() {
//    }
}