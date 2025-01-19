package com.app.happytails.utils.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.happytails.R;
import com.app.happytails.utils.FirebaseUtil;
import com.app.happytails.utils.model.PostModel;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class CreateFragment extends Fragment {

    private EditText vetClinicName, vetDoctorName, vetVisitDate, vetDiagnosis;
    private ImageView vetPic;
    private Button createPostButton;
    private Uri imageUri;
    private String postMainImageUrl;
    private String vetImageUrl;
    private ArrayList<String> galleryImageUrls;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_REQUEST_CODE = 2000;

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

        vetClinicName = view.findViewById(R.id.vetClinicName);
        vetDoctorName = view.findViewById(R.id.vetDoctorName);
        vetVisitDate = view.findViewById(R.id.vetVisitDate);
        vetDiagnosis = view.findViewById(R.id.vetDiagnosis);
        vetPic = view.findViewById(R.id.vetPic);
        createPostButton = view.findViewById(R.id.postCreateBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        vetVisitDate.setOnClickListener(v -> showDatePickerDialog());

        vetPic.setOnClickListener(v -> requestStoragePermission());

        createPostButton.setOnClickListener(v -> uploadVetImage());

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

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        vetVisitDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
            } else {
                selectImage();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                selectImage();
            }
        } else {
            selectImage();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == IMAGE_PICK_CODE) {
                imageUri = data.getData();
                vetPic.setImageURI(imageUri);
            }
        }
    }

    private void uploadVetImage() {
        MediaManager.get().upload(imageUri)
                .unsigned("vet_uploads")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        vetImageUrl = resultData.get("url").toString();
                        uploadGalleryImages();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(getContext(), "Error uploading vet image", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                    }
                }).dispatch();
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

        PostModel post = new PostModel(
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                dogName,
                Integer.parseInt(dogAge),
                dogGender,
                visitDate,
                description,
                clinicName,
                diagnosis,
                doctorName,
                postMainImageUrl,
                galleryImageUrls,
                0,
                supportersList,
                vetImageUrl
        );
        FirebaseFirestore.getInstance().collection("users_posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    navigateToHomeFragment();
                })
                .addOnFailureListener(e -> {
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
        vetPic.setImageResource(R.drawable.baseline_add_24);
    }
}