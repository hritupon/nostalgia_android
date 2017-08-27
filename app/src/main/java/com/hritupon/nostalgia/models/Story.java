package com.hritupon.nostalgia.models;

/**
 * Created by hritupon on 26/8/17.
 */

public class Story {
    String id;
    String description;
    String timeStamp;
    String userId;

    public Story(){}
    public Story(String description, String timeStamp, String userId) {
        this.description = description;
        this.timeStamp = timeStamp;
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
