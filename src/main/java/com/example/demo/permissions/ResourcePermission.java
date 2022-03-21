package com.example.demo.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResourcePermission {

    public static final ResourcePermission NONE_PERMISSION_INSTANCE = new ResourcePermission("", new HashMap<>());
    public enum Operation {
        UPDATE,
        DELETE,
        READ,
        ADD
    }
    public String planKey; // resource
    public Map<String, Set<Operation>> ownerAllowedOperations = new HashMap<>();

    public ResourcePermission(String planKey, Map<String, Set<Operation>> ownerAllowedOperations) {
        this.planKey = planKey;
        this.ownerAllowedOperations = ownerAllowedOperations;
    }
}
