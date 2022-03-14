package com.example.demo.model.response;

/**
 * The response object for post and delete API
 */
public class ObjectIdResponse {
    private String objectId;

    public ObjectIdResponse(String objectId) {
        this.objectId = objectId;
    }

    // https://stackoverflow.com/questions/37841373/java-lang-illegalargumentexception-no-converter-found-for-return-value-of-type/39087282
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

}
