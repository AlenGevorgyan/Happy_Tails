package com.app.happytails.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.app.happytails.utils.model.PostModel;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtil {

    public static void savePost(String dogName, String dogGender, int dogAge, String vetLastVisitDate, String description,
                                String vetClinicName, String diagnosis, String vetDoctorName, String postMainImageUrl,
                                List<String> galleryImageUrls, int fundingPercentage, ArrayList<String> supportersList, String vetImage) {

        PostModel post = new PostModel(
                currentUserId(), dogName, dogAge, dogGender, vetLastVisitDate, description, vetClinicName, diagnosis,
                vetDoctorName, postMainImageUrl, galleryImageUrls, fundingPercentage, supportersList, vetImage
        );

        FirebaseFirestore.getInstance().collection("users_posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Post created successfully with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error creating post: " + e.getMessage());
                });
    }

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }
}