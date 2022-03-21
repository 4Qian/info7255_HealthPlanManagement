package com.example.demo.model;

import com.example.demo.redis.RedisService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeModelFactoryFromRedis {

    public static NodeModel fromRedis(RedisService redisService, EdgesInfo edgesInfo, String nodeKey) {
        NodeModel nodeModel = new NodeModel(nodeKey);
        // construct complex properties first
        for (Map.Entry<String, EdgesInfo> edgeEntry: edgesInfo.edges.entrySet()) {
            String edgeName = edgeEntry.getKey();
            EdgesInfo targetEdgesInfo = edgeEntry.getValue();
            List<String> targetNodeKeys = redisService.getEdgeTargetNodeKeys(nodeKey + "___"  + edgeName);
            List<NodeModel> targetNodeModelList = targetNodeKeys.stream()
                    .map(targetNodeKey -> fromRedis(redisService, targetEdgesInfo, targetNodeKey))
                    .collect(Collectors.toList());
            switch(targetEdgesInfo.arrayOrObject) {
                case OBJECT:
                    if (targetNodeModelList.isEmpty()) {
                        throw new RuntimeException("property's value should be an object, but found none");
                    }
                    if (targetNodeModelList.size() > 1) {
                        throw new RuntimeException("property's value should be an object, but found an array");
                    }
                    nodeModel.complexObjectProperties.put(edgeName, targetNodeModelList.get(0));
                    break;
                case ARRAY:
                    nodeModel.complexArrayProperties.put(edgeName, targetNodeModelList);
                    break;
                default:
                    throw new RuntimeException("");
            }
        }
        // then construct simple properties
        Map<String, String> nodeSimpleProperties = redisService.getNodeSimpleProperties(nodeKey);
        nodeModel.simpleProperties = new HashMap<>(nodeSimpleProperties);
        return nodeModel;
    }
}
