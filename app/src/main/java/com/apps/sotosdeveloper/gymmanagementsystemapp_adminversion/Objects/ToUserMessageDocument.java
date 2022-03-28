package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects;

public class ToUserMessageDocument {

    private String userID;
    private Boolean isTrue;

    public ToUserMessageDocument(String userID, Boolean isTrue) {
        this.userID = userID;
        this.isTrue = isTrue;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Boolean getTrue() {
        return isTrue;
    }

    public void setTrue(Boolean aTrue) {
        isTrue = aTrue;
    }
}

