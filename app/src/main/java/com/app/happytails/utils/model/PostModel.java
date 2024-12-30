package com.app.happytails.utils.model;

import com.google.firebase.Timestamp;

public class PostModel {

    private String userId;
    private String username;
    private String dogName;
    private int dogAge;
    private String dogGender;  // String for easier management
    private String imageUrl;  // Store image URL instead of Base64 string
    private Timestamp timestamp;  // Timestamp field to store the time when the post is created
    private int likeCount;
    private String description;  // New field for description

    // Default constructor required for Firestore
    public PostModel() {
    }

    // Constructor with image URL and description
    public PostModel(String userId, String username, String dogName, String imageUrl, Timestamp timestamp, int dogAge, String dogGender, String description) {
        this.userId = userId;
        this.username = username;
        this.dogName = dogName;
        this.imageUrl = imageUrl;  // Store image URL
        this.timestamp = timestamp;
        this.likeCount = 0;  // Default like count is set to 0
        this.dogAge = dogAge;
        this.dogGender = dogGender;
        this.description = description;  // Set description
    }

    // Getter and setter methods for all fields
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

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public int getDogAge() {
        return dogAge;
    }

    public void setDogAge(int dogAge) {
        this.dogAge = dogAge;
    }

    public String getDogGender() {
        return dogGender;
    }

    public void setDogGender(String dogGender) {
        this.dogGender = dogGender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    // Getter and setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Optionally, you can add a method to increment likes
    public void incrementLikes() {
        this.likeCount++;
    }

    @Override
    public String toString() {
        return "PostModel{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", dogName='" + dogName + '\'' +
                ", dogAge=" + dogAge +
                ", dogGender='" + dogGender + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", timestamp=" + timestamp +
                ", likeCount=" + likeCount +
                ", description='" + description + '\'' +
                '}';
    }
}
