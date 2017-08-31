package com.hritupon.nostalgia.models;

/**
 * Created by hritupon on 26/8/17.
 */

public class Story {
    String id;
    String description;
    long timeStamp;
    String userId;
    String imageId;

    public Story(){}
    public Story(String description, long timeStamp, String userId) {
        this.description = description;
        this.timeStamp = timeStamp;
        this.userId = userId;
    }
    public Story(String description, long timeStamp, String userId, String imageId) {
        this.description = description;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.imageId = imageId;
    }
    public String getDescription() {
        return description;
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

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
