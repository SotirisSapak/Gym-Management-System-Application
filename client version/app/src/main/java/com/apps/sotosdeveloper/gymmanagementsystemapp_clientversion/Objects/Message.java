package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Objects;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Message {

    private String ID;
    private String title;
    private String summary;
    private Map<String, Boolean> to_users;
    private String date;
    private boolean importantMessage;

    public Message(){}

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

    @NotNull
    @Override
    public String toString() {
        return "Message{" +
                "ID='" + ID + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", to_users=" + to_users +
                ", date='" + date + '\'' +
                ", importantMessage=" + importantMessage +
                '}';
    }
}
