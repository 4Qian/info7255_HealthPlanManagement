package com.example.demo.model;
import java.util.Objects;

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
        if (toDelete) {
            return "\tindexName='" + indexName + '\'' +
                    ",\n\tid='" + id + '\'';
        }
        return "\tindexName='" + indexName + '\'' +
                    ",\n\tid='" + id + '\'' +
                    ",\n\trequestBody='" + requestBody + '\'' +
                    ",\n\trouting=" + routing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ESRequest esRequest = (ESRequest) o;
        return toDelete == esRequest.toDelete && routing == esRequest.routing && Objects.equals(indexName, esRequest.indexName) && Objects.equals(id, esRequest.id) && Objects.equals(requestBody, esRequest.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toDelete, indexName, id, requestBody, routing);
    }
}
