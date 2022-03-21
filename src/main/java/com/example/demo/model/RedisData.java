package com.example.demo.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * data to be saved to redis
 */
public class RedisData {
    public Map<String, Map<String, String>> allNodes = new HashMap<>();
    public Map<String, List<String>> allEdges = new HashMap<>();

    public RedisData(NodeModel nodeModel) {
        processNode(nodeModel);
    }

    private void processNode(NodeModel nodeModel) {
        allNodes.put(nodeModel.objectKey, nodeModel.simpleProperties);
        for (Map.Entry<String, List<NodeModel>> entry : nodeModel.complexArrayProperties.entrySet()) {
            String propertyName = entry.getKey();
            List<NodeModel> targetNodeList = entry.getValue();
            allEdges.put(nodeModel.objectKey + "___" + propertyName,
                    targetNodeList.stream().map(targetNode -> targetNode.objectKey).collect(Collectors.toList()));
            targetNodeList.forEach(targetNode -> processNode(targetNode));
        }
        for (Map.Entry<String, NodeModel> entry : nodeModel.complexObjectProperties.entrySet()) {
            String propertyName = entry.getKey();
            NodeModel targetNode = entry.getValue();
            allEdges.put(nodeModel.objectKey + "___" + propertyName, List.of(targetNode.objectKey));
            processNode(targetNode);
        }
    }
}
