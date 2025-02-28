package com.app.happytails.utils.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String username;
    private String email;
    private String userId;

    private int followers;
    private int posts;
    private Timestamp createdTimestamp;

    private String userImage;
    private String public_id;

    private String status;

    public UserModel() {
    }

    public UserModel(Timestamp createdTimestamp, String username, String email, String userId, String userImage, String public_id, int followers, int posts, String status) {
        this.createdTimestamp = createdTimestamp;
        this.username = username;
        this.email = email;
        this.userId = userId;
        this.followers = followers;
        this.posts = posts;
        this.userImage = userImage;
        this.status = status;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
