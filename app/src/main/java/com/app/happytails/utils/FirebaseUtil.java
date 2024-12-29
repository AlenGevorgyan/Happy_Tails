package com.app.happytails.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.app.happytails.utils.model.PostModel;

import java.io.ByteArrayOutputStream;

public class FirebaseUtil {

    // Method to convert a Bitmap image to Base64
    public static String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);  // You can change format as needed
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Method to save a post
    public static void savePost(String username, String dogName, String dogGender, int dogAge, Bitmap dogImage, String description) {
        String imageBase64 = encodeImageToBase64(dogImage);  // Convert the image to Base64
        Timestamp timestamp = new Timestamp(new java.util.Date());  // Current timestamp

        // Create a new PostModel with the provided data
        PostModel post = new PostModel(
                currentUserId(), username, dogName, imageBase64, timestamp, dogAge, dogGender, description
        );

        // Save the post to Firestore
        FirebaseFirestore.getInstance().collection("users")
                .document(currentUserId())  // Reference the current user's document
                .collection("posts")        // Inside the posts collection
                .add(post)                   // Add the post document
                .addOnSuccessListener(documentReference -> {
                    // Post was successfully saved
                    // You can show a success message or take action
                })
                .addOnFailureListener(e -> {
                    // Handle failure, you can show a failure message here
                });
    }

    // Fetch current user ID
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }
}
