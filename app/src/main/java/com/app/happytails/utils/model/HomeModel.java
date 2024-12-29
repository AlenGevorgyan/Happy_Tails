package com.app.happytails.utils.model;

public class HomeModel {

    private String username, timestamp, profileImage, postImage, uid;

    private int likeCount;

    public HomeModel() {
    }

    public HomeModel(String username, String timestamp, String profileImage, String postImage, String uid, int likeCount) {
        this.profileImage = profileImage;
        this.username = username;
        this.timestamp = timestamp;
        this.postImage = postImage;
        this.uid = uid;
        this.likeCount = likeCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
