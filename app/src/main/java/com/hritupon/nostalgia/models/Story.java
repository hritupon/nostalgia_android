package com.hritupon.nostalgia.models;

/**
 * Created by hritupon on 26/8/17.
 */

public class Story {
    String id;
    String description;
    long timeStamp;
    String userId;
    String imagePath;

    public Story(){}
    public Story(String description, long timeStamp, String userId) {
        this.description = description;
        this.timeStamp = timeStamp;
        this.userId = userId;
    }
    public Story(String description, long timeStamp, String userId, String imagePath) {
        this.description = description;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.imagePath = imagePath;
    }
    public String getDescription() {
        if(description.length()>1) {
            return description.substring(0, 1).toUpperCase() + description.substring(1);
        }
        return description.toUpperCase();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
