package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Objects;

import java.util.Map;

public class Message {

    private String ID;
    private String title;
    private String summary;
    private Map<String, Boolean> to_users;
    private String date;
    private boolean importantMessage;

    public Message(){}

    public Message(String ID, String title, String summary,
                   Map<String, Boolean> to_users, String date, boolean importantMessage) {
        this.ID = ID;
        this.title = title;
        this.summary = summary;
        this.to_users = to_users;
        this.date = date;
        this.importantMessage = importantMessage;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Map<String, Boolean> getTo_users() {
        return to_users;
    }

    public void setTo_user(Map<String, Boolean> to_user) {
        this.to_users = to_user;
    }

    public boolean isImportantMessage() {
        return importantMessage;
    }

    public void setImportantMessage(boolean importantMessage) {
        this.importantMessage = importantMessage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
