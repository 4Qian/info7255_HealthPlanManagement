package com.example.demo.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储了edges的Node的模型。（省略了simple properties)
 */
public class EdgesInfo {
    public enum ArrayOrObject {
        ARRAY,
        OBJECT
    }
    public ArrayOrObject arrayOrObject;
    public Map<String, EdgesInfo> edges = new HashMap<>();

    @Override
    public String toString() {
        return arrayOrObject + "," + edges.toString();
    }
}
