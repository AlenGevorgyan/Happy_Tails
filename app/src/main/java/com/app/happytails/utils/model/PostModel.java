package com.app.happytails.utils.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostModel {

    private String userId; // User ID of the post creator
    private String dogName; // Name of the dog
    private int dogAge; // Age of the dog
    private String dogGender; // Gender of the dog
    private String vetLastVisitDate; // Last visit date to the veterinarian
    private String description; // Description of the post
    private String vetClinicName; // Name of the veterinary clinic
    private String diagnosis; // Diagnosis from the vet visit
    private String vetDoctorName; // Name of the veterinary doctor
    private String postMainImageUrl; // Main image URL of the post
    private List<String> galleryImageUrls; // List of gallery image URLs
    private int fundingPercentage; // Funding percentage for the post
    private List<String> supportersList; // Supporters list
    private String vetImage; // Image URL of the vet

    // Default constructor required for Firestore
    public PostModel() {
        this.userId = "";
        this.dogName = "";
        this.dogAge = 0;
        this.dogGender = "";
        this.vetLastVisitDate = "";
        this.description = "";
        this.vetClinicName = "";
        this.diagnosis = "";
        this.vetDoctorName = "";
        this.postMainImageUrl = "";
        this.galleryImageUrls = Collections.emptyList(); // Use empty list
        this.fundingPercentage = 0;
        this.supportersList = Collections.emptyList(); // Use empty list
        this.vetImage = "";
    }

    // Constructor with all fields
    public PostModel(String userId, String dogName, int dogAge, String dogGender, String vetLastVisitDate,
                     String description, String vetClinicName, String diagnosis, String vetDoctorName,
                     String postMainImageUrl, List<String> galleryImageUrls, int fundingPercentage,
                     List<String> supportersList, String vetImage) {
        this.userId = userId != null ? userId : "";
        this.dogName = dogName != null ? dogName : "";
        this.dogAge = dogAge;
        this.dogGender = dogGender != null ? dogGender : "";
        this.vetLastVisitDate = vetLastVisitDate != null ? vetLastVisitDate : "";
        this.description = description != null ? description : "";
        this.vetClinicName = vetClinicName != null ? vetClinicName : "";
        this.diagnosis = diagnosis != null ? diagnosis : "";
        this.vetDoctorName = vetDoctorName != null ? vetDoctorName : "";
        this.postMainImageUrl = postMainImageUrl != null ? postMainImageUrl : "";
        this.galleryImageUrls = galleryImageUrls != null ? galleryImageUrls : Collections.emptyList();
        this.fundingPercentage = fundingPercentage;
        this.supportersList = supportersList != null ? supportersList : Collections.emptyList();
        this.vetImage = vetImage != null ? vetImage : "";
    }

    // Getters and setters for all fields
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId != null ? userId : "";
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName != null ? dogName : "";
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
        this.dogGender = dogGender != null ? dogGender : "";
    }

    public String getVetLastVisitDate() {
        return vetLastVisitDate;
    }

    public void setVetLastVisitDate(String vetLastVisitDate) {
        this.vetLastVisitDate = vetLastVisitDate != null ? vetLastVisitDate : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public String getVetClinicName() {
        return vetClinicName;
    }

    public void setVetClinicName(String vetClinicName) {
        this.vetClinicName = vetClinicName != null ? vetClinicName : "";
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis != null ? diagnosis : "";
    }

    public String getVetDoctorName() {
        return vetDoctorName;
    }

    public void setVetDoctorName(String vetDoctorName) {
        this.vetDoctorName = vetDoctorName != null ? vetDoctorName : "";
    }

    public String getPostMainImageUrl() {
        return postMainImageUrl;
    }

    public void setPostMainImageUrl(String postMainImageUrl) {
        this.postMainImageUrl = postMainImageUrl != null ? postMainImageUrl : "";
    }

    public List<String> getGalleryImageUrls() {
        return galleryImageUrls;
    }

    public void setGalleryImageUrls(List<String> galleryImageUrls) {
        this.galleryImageUrls = galleryImageUrls != null ? galleryImageUrls : Collections.emptyList();
    }

    public int getFundingPercentage() {
        return fundingPercentage;
    }

    public void setFundingPercentage(int fundingPercentage) {
        this.fundingPercentage = fundingPercentage;
    }

    public List<String> getSupportersList() {
        return supportersList;
    }

    public void setSupportersList(List<String> supportersList) {
        this.supportersList = supportersList != null ? supportersList : Collections.emptyList();
    }

    public String getVetImage() {
        return vetImage;
    }

    public void setVetImage(String vetImage) {
        this.vetImage = vetImage != null ? vetImage : "";
    }

    @Override
    public String toString() {
        return "PostModel{" +
                "userId='" + userId + '\'' +
                ", dogName='" + dogName + '\'' +
                ", dogAge=" + dogAge +
                ", dogGender='" + dogGender + '\'' +
                ", vetLastVisitDate='" + vetLastVisitDate + '\'' +
                ", description='" + description + '\'' +
                ", vetClinicName='" + vetClinicName + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", vetDoctorName='" + vetDoctorName + '\'' +
                ", postMainImageUrl='" + postMainImageUrl + '\'' +
                ", galleryImageUrls=" + galleryImageUrls +
                ", fundingPercentage=" + fundingPercentage +
                ", supportersList=" + supportersList +
                ", vetImage='" + vetImage + '\'' +
                '}';
    }
}