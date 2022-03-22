package com.example.demo.model.response;

/**
 * The response object for post and delete API
 */
public class ObjectTypeAndIdResponse {
    private String objectType;
    private String objectId;

    public ObjectTypeAndIdResponse(String objectType, String objectId) {
        this.objectType = objectType;
        this.objectId = objectId;
    }

    // https://stackoverflow.com/questions/37841373/java-lang-illegalargumentexception-no-converter-found-for-return-value-of-type/39087282


    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
