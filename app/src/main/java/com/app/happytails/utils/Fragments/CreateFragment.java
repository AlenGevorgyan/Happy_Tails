package com.app.happytails.utils.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.happytails.R;
import com.app.happytails.utils.model.HomeModel;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class CreateFragment extends Fragment {

    private EditText vetClinicName, vetDoctorName, vetVisitDate, vetDiagnosis;
    private Button createPostButton;
    private String postMainImageUrl;
    private ArrayList<String> galleryImageUrls;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String dogName, dogAge, dogGender, description;
    private Uri mainImageUri;
    private ArrayList<Uri> galleryUris;
    private ArrayList<String> supportersList;

    public CreateFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create2, container, false);

        // Initialize views
        vetClinicName = view.findViewById(R.id.vetClinicName);
        vetDoctorName = view.findViewById(R.id.vetDoctorName);
        vetVisitDate = view.findViewById(R.id.vetVisitDate);
        vetDiagnosis = view.findViewById(R.id.vetDiagnosis);
        createPostButton = view.findViewById(R.id.postCreateBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        createPostButton.setOnClickListener(v -> uploadGalleryImages());

        if (getArguments() != null) {
            dogName = getArguments().getString("dogName");
            dogAge = getArguments().getString("dogAge");
            dogGender = getArguments().getString("dogGender");
            description = getArguments().getString("description");
            mainImageUri = getArguments().getParcelable("mainImageUri");
            galleryUris = getArguments().getParcelableArrayList("galleryUris");

            Toast.makeText(getContext(), "Data received: " + dogName, Toast.LENGTH_SHORT).show();
        }

        supportersList = new ArrayList<>();
        galleryImageUrls = new ArrayList<>();

        return view;
    }

    private void uploadGalleryImages() {
        if (galleryUris != null && !galleryUris.isEmpty()) {
            for (Uri uri : galleryUris) {
                MediaManager.get().upload(uri)
                        .unsigned("gallery_uploads")
                        .callback(new UploadCallback() {
                            @Override
                            public void onStart(String requestId) {
                            }

                            @Override
                            public void onProgress(String requestId, long bytes, long totalBytes) {
                            }

                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                galleryImageUrls.add(resultData.get("url").toString());
                                if (galleryImageUrls.size() == galleryUris.size()) {
                                    // After all gallery images are uploaded, upload the main image
                                    uploadMainImage();
                                }
                            }

                            @Override
                            public void onError(String requestId, ErrorInfo error) {
                                Toast.makeText(getContext(), "Error uploading gallery image", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onReschedule(String requestId, ErrorInfo error) {
                            }
                        }).dispatch();
            }
        } else {
            // If no gallery images, directly upload the main image
            uploadMainImage();
        }
    }

    private void uploadMainImage() {
        if (mainImageUri != null) {
            MediaManager.get().upload(mainImageUri)
                    .unsigned("post_main_uploads")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            postMainImageUrl = resultData.get("url").toString();
                            savePostToFirestore();
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(getContext(), "Error uploading main image", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                        }
                    }).dispatch();
        } else {
            savePostToFirestore();
        }
    }

    private void savePostToFirestore() {
        String clinicName = vetClinicName.getText().toString();
        String doctorName = vetDoctorName.getText().toString();
        String visitDate = vetVisitDate.getText().toString();
        String diagnosis = vetDiagnosis.getText().toString();

        if (clinicName.isEmpty() || doctorName.isEmpty() || visitDate.isEmpty() || diagnosis.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference newDocRef = FirebaseFirestore.getInstance().collection("dogs").document();
        String dogId = newDocRef.getId();

        HomeModel post = new HomeModel(
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                dogId,
                Integer.parseInt(dogAge),
                dogGender,
                dogName,
                0,
                galleryImageUrls,
                postMainImageUrl,
                supportersList,
                description,
                diagnosis,
                clinicName,
                doctorName,
                visitDate
        );
        Log.d("CreateFragment", "Saving post to Firestore - Dog ID: " + dogId);

        newDocRef.set(post)
                .addOnSuccessListener(aVoid -> {
                    Log.d("CreateFragment", "Post created successfully");
                    Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    navigateToHomeFragment();
                })
                .addOnFailureListener(e -> {
                    Log.e("CreateFragment", "Error creating post: " + e.getMessage());
                    Toast.makeText(getContext(), "Error creating post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void navigateToHomeFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new HomeFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void clearFields() {
        vetClinicName.setText("");
        vetDoctorName.setText("");
        vetVisitDate.setText("");
        vetDiagnosis.setText("");
    }
}