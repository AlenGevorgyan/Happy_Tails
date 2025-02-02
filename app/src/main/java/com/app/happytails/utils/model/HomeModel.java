package com.app.happytails.utils.model;

import java.util.ArrayList;

public class HomeModel {
    private String creator;
    private int dogAge;
    private String dogGender;
    private String dogId;
    private String dogName;
    private int fundingPercentage;
    private ArrayList<String> galleryImages;
    private String mainImage;
    private ArrayList<String> supporters;
    private String description;
    private String diagnosis;
    private String clinicName;
    private String doctorName;
    private String vetLastVisitDate;

    // Empty constructor for Firestore
    public HomeModel() {
    }

    public HomeModel(String creator, String dogId, int dogAge, String dogGender, String dogName, int fundingProgress, String mainImageUrl, ArrayList<String> supporters) {
        this.creator = creator;
        this.dogAge = dogAge;
        this.dogGender = dogGender;
        this.dogName = dogName;
        this.fundingPercentage = fundingPercentage;
        this.galleryImages = galleryImages;
        this.mainImage = mainImageUrl;
        this.supporters = supporters;
        this.dogId = dogId;
    }

    public HomeModel(String creator, String dogId, int dogAge, String dogGender, String dogName,
                     int fundingPercentage, ArrayList<String> galleryImages, String mainImage,
                     ArrayList<String> supporters, String description, String diagnosis,
                     String clinicName, String doctorName, String vetLastVisitDate) {
        this.creator = creator;
        this.dogAge = dogAge;
        this.dogGender = dogGender;
        this.dogName = dogName;
        this.fundingPercentage = fundingPercentage;
        this.dogId = dogId;
        this.galleryImages = galleryImages;
        this.mainImage = mainImage;
        this.supporters = supporters;
        this.description = description;
        this.diagnosis = diagnosis;
        this.clinicName = clinicName;
        this.doctorName = doctorName;
        this.vetLastVisitDate = vetLastVisitDate;
    }

    // Getters and setters
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public int getDogAge() { return dogAge; }
    public void setDogAge(int dogAge) { this.dogAge = dogAge; }

    public String getDogGender() { return dogGender; }
    public void setDogGender(String dogGender) { this.dogGender = dogGender; }

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

    public String getVetLastVisitDate() { return vetLastVisitDate; }
    public void setVetLastVisitDate(String vetLastVisitDate) { this.vetLastVisitDate = vetLastVisitDate; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}