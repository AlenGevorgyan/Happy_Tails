package com.app.happytails.utils.model;

import java.util.ArrayList;

public class HomeModel {
    private String creator;
    private String dogId;
    private String dogName;
    private int fundingPercentage;
    private ArrayList<String> galleryImages;
    private String mainImage;
    private ArrayList<String> supporters;
    private double fundingAmount;
    private ArrayList<String> donationsAmount;
    private int urgencylevel;

    // Empty constructor for Firestore
    public HomeModel() {
    }

    public HomeModel(String creator, String dogId, String dogName, int fundingProgress, String mainImageUrl, ArrayList<String> supporters, int urgencylevel) {
        this.creator = creator;
        this.dogName = dogName;
        this.fundingPercentage = fundingPercentage;
        this.galleryImages = galleryImages;
        this.mainImage = mainImageUrl;
        this.supporters = supporters;
        this.dogId = dogId;
        this.urgencylevel = urgencylevel;
    }

    public HomeModel(String creator, String dogId, String dogName,
                     int fundingPercentage, ArrayList<String> galleryImages, String mainImage,
                     ArrayList<String> supporters, double fundingAmount, ArrayList<String> donationsAmount, int urgencyLevel) {
        this.creator = creator;
        this.dogName = dogName;
        this.fundingPercentage = fundingPercentage;
        this.dogId = dogId;
        this.galleryImages = galleryImages;
        this.mainImage = mainImage;
        this.supporters = supporters;
        this.fundingAmount = fundingAmount;
        this.donationsAmount = donationsAmount;
        this.urgencylevel = urgencyLevel;
    }

    // Getters and setters
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    public int getUrgencylevel() {
        return urgencylevel;
    }

    public void setUrgencylevel(int urgencylevel) {
        this.urgencylevel = urgencylevel;
    }

    public double getFundingAmount() {
        return fundingAmount;
    }

    public void setFundingAmount(double fundingAmount) {
        this.fundingAmount = fundingAmount;
    }

    public ArrayList<String> getDonationsAmount() {
        return donationsAmount;
    }

    public void setDonationsAmount(ArrayList<String> donationsAmount) {
        this.donationsAmount = donationsAmount;
    }

    public String getDogId() { return dogId; }
    public void setDogId(String dogId) { this.dogId = dogId; }

    public String getDogName() { return dogName; }
    public void setDogName(String dogName) { this.dogName = dogName; }

    public int getFundingPercentage() { return fundingPercentage; }
    public void setFundingPercentage(int fundingPercentage) { this.fundingPercentage = fundingPercentage; }

    public ArrayList<String> getGalleryImages() { return galleryImages; }
    public void setGalleryImages(ArrayList<String> galleryImages) { this.galleryImages = galleryImages; }

    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }

    public ArrayList<String> getSupporters() { return supporters; }
    public void setSupporters(ArrayList<String> supporters) { this.supporters = supporters; }

}