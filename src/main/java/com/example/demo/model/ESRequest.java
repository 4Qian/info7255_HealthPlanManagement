package com.example.demo.model;

public class ESRequest {
    boolean toDelete;
    String indexName;
    String id;
    String requestBody;
    boolean routing;

    public ESRequest() {
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getId() {
        return id;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public boolean isRouting() {
        return routing;
    }

    public ESRequest(String indexName, String id, String requestBody, boolean routing) {
        this.indexName = indexName;
        this.id = id;
        this.requestBody = requestBody;
        this.routing = routing;
        this.toDelete = false;
    }
    @Override
    public String toString() {
        return "ESRequest{" +
                "toDelete=" + toDelete +
                ", indexName='" + indexName + '\'' +
                ", id='" + id + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", routing=" + routing +
                '}';
    }
}
