package com.example.demo.controller;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class PlanControllerTest {

//    @Test
// https://mkyong.com/java/json-simple-example-read-and-write-json/
//    public void testWriteJson_simpleJson() {
//        JSONObject obj = new JSONObject();
//        obj.put("name", "mkyong.com");
//        obj.put("age", 100);
//
//        JSONArray list = new JSONArray();
//        list.add("msg 1");
//        list.add("msg 2");
//        list.add("msg 3");
//
//        obj.put("messages", list);
//
//        try (FileWriter file = new FileWriter("./output.json")) {
//            file.write(obj.toJSONString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.print(obj);
//        assertEquals("{\"name\":\"mkyong.com\",\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"],\"age\":100}",
//                obj.toJSONString());
//    }

//    @Test
//    public void testReadJson_simpleJson() {
//        JSONParser parser = new JSONParser();
//
//        try (Reader reader = new FileReader("./output.json")) {
//
//            JSONObject jsonObject = (JSONObject) parser.parse(reader);
//            System.out.println(jsonObject);
//
//            String name = (String) jsonObject.get("name");
//            System.out.println(name);
//
//            long age = (Long) jsonObject.get("age");
//            System.out.println(age);
//
//            // loop array
//            JSONArray msg = (JSONArray) jsonObject.get("messages");
//            Iterator<String> iterator = msg.iterator();
//            while (iterator.hasNext()) {
//                System.out.println(iterator.next());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }
    @Test
    public void testOrgJson() {
//        org.json.JSONObject root = new org.json.JSONObject();
//        root.put("message", "Hi");
//        JSONObject place = new JSONObject();
//        place.put("name", "World!");
//        root.put("place", place);
//        String json = root.toJSONString();
//        System.out.println(json);
//
//        System.out.println();
//        // convert json to Java
//        JSONObject obj = (JSONObject) JSONValue.parse(json);
//        String message = (String) obj.get("message");
//        place = (JSONObject) obj.get("place");
//        String name = (String) place.get("name");
//        System.out.println(message + " " + name);
//        final String filePath = "./src/main/resources/plan_schema.json";
//
//        try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
//            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
//            Schema schema = SchemaLoader.load(rawSchema);
//            schema.validate(new JSONObject("{\"hello\" : \"world\"}")); // throws a ValidationException if this object is invalid
//        } catch (Exception e) {
//
//        }
//
//        final String resourceName = "./src/main/resources/plan_schema.json";
//        InputStream is = getClass().getResourceAsStream(resourceName);
//        if (is == null) {
//            throw new NullPointerException("Cannot find resource file " + resourceName);
//        }
//
//        JSONTokener tokener = new JSONTokener(is);
//        JSONObject object = new JSONObject(tokener);
//        System.out.println("Id  : " + object.getLong("id"));
//        System.out.println("Name: " + object.getString("name"));
//        System.out.println("Age : " + object.getInt("age"));
//
//        System.out.println("Courses: ");
//        JSONArray courses = object.getJSONArray("courses");
//        for (int i = 0; i < courses.length(); i++) {
//            System.out.println("  - " + courses.get(i));
//        }
    }

}