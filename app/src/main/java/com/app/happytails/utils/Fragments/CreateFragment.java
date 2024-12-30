package com.app.happytails.utils.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.happytails.R;
import com.app.happytails.utils.model.PostModel;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateFragment extends Fragment {

    private static final String TAG = "CreateFragment";

    private EditText description, dogName, dogAge, dogGender;
    private ImageView imageView, userProfileImage;
    private TextView userTv;
    private Uri imageUri;
    private Button createBtn;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private boolean isCloudinaryInitialized = false; // Flag to track Cloudinary initialization

    public CreateFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        init(view);
        initializeCloudinary(); // Initialize Cloudinary only once
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clickListener();
        loadUserData();
    }

    private void initializeCloudinary() {
        // Only initialize Cloudinary if it's not already initialized
        if (!isCloudinaryInitialized) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dzwoyslx4"); // Replace with your Cloudinary cloud name
            config.put("api_key", "936129888839456"); // Replace with your API key
            config.put("api_secret", "K4vL432ZheS8N6uJARlvzUh1Yww"); // Replace with your API secret

            MediaManager.init(requireContext(), config);
            isCloudinaryInitialized = true; // Set the flag to true after initialization
            Log.d(TAG, "Cloudinary initialized.");
        } else {
            Log.d(TAG, "Cloudinary is already initialized.");
        }
    }

    private void loadUserData() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getDisplayName();
            Uri photoUrl = currentUser.getPhotoUrl();

            userTv.setText(username != null ? username : "User");
            Glide.with(this)
                    .load(photoUrl != null ? photoUrl : R.drawable.user_icon)
                    .placeholder(R.drawable.user_icon)
                    .into(userProfileImage);
        } else {
            Log.e(TAG, "User is not authenticated.");
        }
    }

    private void clickListener() {
        imageView.setOnClickListener(v -> openGallery());
        createBtn.setOnClickListener(v -> uploadData());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadData() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Cloudinary
        MediaManager.get().upload(imageUri)
                .unsigned("user_uploads") // Replace with your unsigned upload preset
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Upload started...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG, "Uploading: " + (bytes * 100 / totalBytes) + "%");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d(TAG, "Upload successful: " + resultData.get("url"));
                        String imageUrl = resultData.get("url").toString();
                        savePostToFirestore(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload error: " + error.getDescription());
                        Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload rescheduled: " + error.getDescription());
                    }
                })
                .dispatch();
    }

    private void savePostToFirestore(String imageUrl) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User is not authenticated.");
            return;
        }

        PostModel post = new PostModel(
                currentUser.getUid(),
                currentUser.getDisplayName(),
                dogName.getText().toString(),
                imageUrl,
                new Timestamp(System.currentTimeMillis() / 1000, 0),
                Integer.parseInt(dogAge.getText().toString()),
                dogGender.getText().toString(),
                description.getText().toString()
        );

        String documentId = UUID.randomUUID().toString();

        db.collection("users")
                .document(currentUser.getUid())
                .collection("posts")
                .document(documentId)
                .set(post)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Post saved successfully");
                    Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving post", e);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void init(View view) {
        description = view.findViewById(R.id.descriptionED);
        userTv = view.findViewById(R.id.createNameTv);
        dogName = view.findViewById(R.id.dogName);
        dogAge = view.findViewById(R.id.dogAge);
        dogGender = view.findViewById(R.id.dogGender);
        imageView = view.findViewById(R.id.dogPic);
        userProfileImage = view.findViewById(R.id.userProfileImage);
        createBtn = view.findViewById(R.id.postCreateBtn);
    }

    private void clearFields() {
        description.setText("");
        dogName.setText("");
        dogAge.setText("");
        dogGender.setText("");
        imageView.setImageResource(View.LAYER_TYPE_NONE);
    }
}
