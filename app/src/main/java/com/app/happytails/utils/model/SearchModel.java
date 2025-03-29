package com.app.happytails.utils.model;

public class SearchModel {
    private String userId;
    private String username;
    private String dogId;
    private String dogName;

    // Default constructor
    public SearchModel() {
    }

    public SearchModel(String userId, String username, String dogId, String dogName) {
        this.userId = userId;
        this.username = username;
        this.dogId = dogId;
        this.dogName = dogName;
    }

    // Getters and setters for userId and username
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getters and setters for dogId and dogName
    public String getDogId() {
        return dogId;
    }

    public void setDogId(String dogId) {
        this.dogId = dogId;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }
}